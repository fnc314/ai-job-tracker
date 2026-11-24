package dev.fnc314.aijobtracker.backend.routes

import dev.fnc314.aijobtracker.backend.BackendService
import dev.fnc314.aijobtracker.records.parser.DocumentIdGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val eventarcJson = Json { ignoreUnknownKeys = true }

/**
 * Registers Eventarc and CloudEvents ingestion routes for Firestore document mutations.
 */
public fun Route.eventarcRoutes(): Unit {
    post("/events/firestore/application-changed") {
        try {
            val ceType = call.request.header("ce-type")
            val ceSubject = call.request.header("ce-subject")
            val body = call.receiveText()

            if (body.isBlank()) {
                call.respondText(
                    """{"status":"BAD_REQUEST","message":"Empty request body"}""",
                    ContentType.Application.Json,
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }

            val jsonElement = eventarcJson.parseToJsonElement(body).jsonObject
            val targetCompanyIds = extractCompanyIdsFromCloudEvent(jsonElement, ceSubject)

            if (targetCompanyIds.isEmpty()) {
                call.respondText(
                    """{"status":"IGNORED","message":"No companyId found in payload or subject","ceType":"$ceType"}""",
                    ContentType.Application.Json,
                    status = HttpStatusCode.OK
                )
                return@post
            }

            val updatedCompanies = mutableListOf<String>()
            for (companyId in targetCompanyIds) {
                try {
                    BackendService.recalculateCompanyMetrics(companyId)
                    updatedCompanies.add(companyId)
                } catch (e: Exception) {
                    println("Warning: Failed to recalculate metrics for $companyId: ${e.message}")
                }
            }

            call.respondText(
                """{"status":"SUCCESS","companiesUpdated":${updatedCompanies.size},"companies":${eventarcJson.encodeToString(updatedCompanies)}}""",
                ContentType.Application.Json,
                status = HttpStatusCode.OK
            )
        } catch (e: Exception) {
            call.respondText(
                """{"status":"ERROR","message":"${e.message?.replace("\"", "\\\"")}"}""",
                ContentType.Application.Json,
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}

/**
 * Extracts target company IDs from a Firestore CloudEvent JSON payload.
 *
 * Inspects both `value` (new state) and `oldValue` (previous state) to handle creations,
 * updates, and deletions, falling back to document ID parsing if fields are absent.
 *
 * @param json Root JSON object representing the CloudEvent payload or Firestore event.
 * @param ceSubject Optional CloudEvents subject header (e.g. `documents/applications/{id}`).
 * @return Set of extracted canonical company IDs.
 */
public fun extractCompanyIdsFromCloudEvent(json: JsonObject, ceSubject: String? = null): Set<String> {
    val results = mutableSetOf<String>()

    fun inspectDocumentObject(docObj: JsonObject?) {
        if (docObj == null) return
        val fields = docObj["fields"]?.jsonObject
        val directCompanyId = fields?.get("companyId")?.jsonObject?.get("stringValue")?.jsonPrimitive?.contentOrNull
        val directCompany = fields?.get("company")?.jsonObject?.get("stringValue")?.jsonPrimitive?.contentOrNull

        val candidate = when {
            !directCompanyId.isNullOrBlank() -> DocumentIdGenerator.slug(directCompanyId, fallback = directCompanyId)
            !directCompany.isNullOrBlank() -> DocumentIdGenerator.slug(directCompany, fallback = "unknown")
            else -> null
        }
        if (!candidate.isNullOrBlank() && candidate != "unknown") {
            results.add(candidate)
        } else {
            // Check document name path in payload: projects/.../databases/(default)/documents/applications/{docId}
            val name = docObj["name"]?.jsonPrimitive?.contentOrNull
            if (!name.isNullOrBlank()) {
                val docId = name.substringAfterLast("/")
                extractFromDocId(docId)?.let { results.add(it) }
            }
        }
    }

    inspectDocumentObject(json["value"]?.jsonObject)
    inspectDocumentObject(json["oldValue"]?.jsonObject)

    // Direct fallback: flat fields object (e.g. from local test or custom webhook)
    if (results.isEmpty() && json.containsKey("fields")) {
        inspectDocumentObject(json)
    }

    // Direct fallback: check ceSubject header
    if (results.isEmpty() && !ceSubject.isNullOrBlank()) {
        val docId = ceSubject.substringAfterLast("/")
        extractFromDocId(docId)?.let { results.add(it) }
    }

    return results
}

private fun extractFromDocId(docId: String): String? {
    if (!docId.contains("--")) return null
    val companyPrefix = docId.substringBefore("--")
    val candidate = DocumentIdGenerator.slug(companyPrefix, fallback = companyPrefix)
    return if (candidate.isNotBlank() && candidate != "unknown") candidate else null
}

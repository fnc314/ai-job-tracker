package dev.fnc314.aijobtracker.backend.routes

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Firestore
import dev.fnc314.aijobtracker.backend.BackendService
import dev.fnc314.aijobtracker.backend.scraping.JobScraperService
import dev.fnc314.aijobtracker.backend.scraping.model.ScrapedJobPosting
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.security.MessageDigest

/**
 * Registers unversioned job scraping HTTP routes and synchronizes results directly to Firestore.
 *
 * @param scraperService Job scraper coordinator.
 * @param database Optional Firestore client instance; defaults to [BackendService.remoteDatabase].
 */
public fun Route.scrapingRoutes(
    scraperService: JobScraperService = JobScraperService.create(),
    database: Firestore? = null
): Unit {
    post("/jobs/scrape") {
        try {
            val body = call.receiveText()
            val parsedJson = if (body.isNotBlank()) Json.parseToJsonElement(body).jsonObject else null

            val query = parsedJson?.get("query")?.jsonPrimitive?.content ?: ""
            val location = parsedJson?.get("location")?.jsonPrimitive?.content ?: ""

            // Parse 'searches' or 'sources' array of enum strings (e.g. ["ASHBY", "WORKDAY", "GREENHOUSE"])
            val searchesElement = parsedJson?.get("searches") ?: parsedJson?.get("sources")
            val targetSources: Set<String>? = searchesElement?.let { element ->
                try {
                    element.jsonArray.mapNotNull { it.jsonPrimitive.content.uppercase().trim() }.toSet()
                } catch (_: Exception) {
                    null
                }
            }

            val jobs: List<ScrapedJobPosting> = scraperService(query, location, targetSources).toList()
            val db = database ?: try { BackendService.remoteDatabase } catch (_: Exception) { null }

            // Persist scraped job postings directly to Firestore collection 'scraped-postings'
            if (db != null && jobs.isNotEmpty()) {
                val coll = db.collection("scraped-postings")
                for (job in jobs) {
                    val docId = if (job.url.isNotBlank()) {
                        MessageDigest.getInstance("SHA-256")
                            .digest(job.url.toByteArray())
                            .joinToString("") { "%02x".format(it) }
                            .take(40)
                    } else {
                        job.id
                    }
                    val docRef = coll.document(docId)
                    val docData = mapOf(
                        "id" to job.id,
                        "url" to job.url,
                        "title" to job.title,
                        "company" to job.company,
                        "location" to job.location,
                        "portal" to job.portal,
                        "description" to job.description,
                        "postingDate" to job.datePosted,
                        "status" to "new",
                        "updatedAt" to Timestamp.now()
                    )
                    docRef.set(docData, com.google.cloud.firestore.SetOptions.merge())
                }
            }

            val responseJson = Json.encodeToString(jobs)
            call.respondText(
                """{"status":"SUCCESS","query":"$query","location":"$location","count":${jobs.size},"jobs":$responseJson}""",
                ContentType.Application.Json
            )
        } catch (e: Exception) {
            call.respondText(
                """{"status":"ERROR","message":"${e.message}"}""",
                ContentType.Application.Json,
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}

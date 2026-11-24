package dev.fnc314.aijobtracker.backend.routes

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventarcRoutesTest {

    @Test
    fun testExtractCompanyIdsFromCloudEvent_withNewValueDocument() {
        val payload = """
        {
            "value": {
                "name": "projects/test-proj/databases/(default)/documents/applications/snap--software-engineer",
                "fields": {
                    "companyId": { "stringValue": "snap" },
                    "status": { "stringValue": "SUBMITTED" }
                }
            }
        }
        """.trimIndent()

        val json = Json.parseToJsonElement(payload).jsonObject
        val companies = extractCompanyIdsFromCloudEvent(json)

        assertEquals(1, companies.size)
        assertTrue(companies.contains("snap"))
    }

    @Test
    fun testExtractCompanyIdsFromCloudEvent_withOldValueDocument() {
        val payload = """
        {
            "oldValue": {
                "name": "projects/test-proj/databases/(default)/documents/applications/bjak--android-dev",
                "fields": {
                    "company": { "stringValue": "BJAK" }
                }
            }
        }
        """.trimIndent()

        val json = Json.parseToJsonElement(payload).jsonObject
        val companies = extractCompanyIdsFromCloudEvent(json)

        assertEquals(1, companies.size)
        assertTrue(companies.contains("bjak"))
    }

    @Test
    fun testExtractCompanyIdsFromCloudEvent_fromSubject() {
        val payload = "{}"
        val json = Json.parseToJsonElement(payload).jsonObject
        val companies = extractCompanyIdsFromCloudEvent(json, ceSubject = "documents/applications/uber--staff-android")

        assertEquals(1, companies.size)
        assertTrue(companies.contains("uber"))
    }

    @Test
    fun testEventarcRoute_emptyBodyReturnsBadRequest() = testApplication {
        routing {
            eventarcRoutes()
        }

        val response = client.post("/events/firestore/application-changed") {
            contentType(ContentType.Application.Json)
            setBody("")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Empty request body"))
    }

    @Test
    fun testEventarcRoute_unmappedDocReturnsIgnored() = testApplication {
        routing {
            eventarcRoutes()
        }

        val response = client.post("/events/firestore/application-changed") {
            contentType(ContentType.Application.Json)
            setBody("""{"someOtherField": 123}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("IGNORED"))
    }
}

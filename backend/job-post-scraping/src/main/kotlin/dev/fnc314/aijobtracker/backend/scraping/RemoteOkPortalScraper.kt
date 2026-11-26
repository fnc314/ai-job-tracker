package dev.fnc314.aijobtracker.backend.scraping

import dev.fnc314.aijobtracker.backend.scraping.model.ScrapedJobPosting
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Native Kotlin HTTP scraper for RemoteOK remote tech postings.
 */
public class RemoteOkPortalScraper(
    private val client: HttpClient = HttpClient(CIO)
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val queryLower = query.lowercase().trim()
        val locationLower = location.lowercase().trim()

        try {
            val response = client.get("https://remoteok.com/api") {
                header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
            }
            val responseBody = response.bodyAsText()
            val rawArray = Json.parseToJsonElement(responseBody).jsonArray
            // First item in RemoteOK is a disclaimer object
            val jobsArray = if (rawArray.size > 1) rawArray.drop(1) else emptyList()

            for (element in jobsArray) {
                val obj = element.jsonObject
                val id = obj["id"]?.jsonPrimitive?.content ?: obj["slug"]?.jsonPrimitive?.content ?: ""
                val title = obj["position"]?.jsonPrimitive?.content ?: ""
                val company = obj["company"]?.jsonPrimitive?.content ?: ""
                val loc = obj["location"]?.jsonPrimitive?.content ?: "Remote"
                val url = obj["url"]?.jsonPrimitive?.content ?: "https://remoteok.com/l/$id"
                val tags = obj["tags"]?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()
                val tagsStr = tags.joinToString(" ").lowercase()

                val matchesQuery = queryLower.isEmpty() ||
                    title.lowercase().contains(queryLower) ||
                    tagsStr.contains(queryLower)
                val matchesLocation = locationLower.isEmpty() ||
                    loc.lowercase().contains(locationLower) ||
                    locationLower.contains("remote")

                if (matchesQuery && matchesLocation && title.isNotBlank()) {
                    emit(
                        ScrapedJobPosting(
                            id = id.ifBlank { url.hashCode().toString() },
                            portal = "REMOTEOK",
                            title = title,
                            company = company,
                            location = loc,
                            url = url,
                            description = obj["description"]?.jsonPrimitive?.content ?: ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Gracefully handle network or rate limit errors
        }
    }
}

package dev.fnc314.aijobtracker.backend.scraping

import dev.fnc314.aijobtracker.backend.scraping.model.ScrapedJobPosting
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Native Kotlin HTTP scraper for company career boards hosted on Greenhouse ATS.
 */
public class GreenhousePortalScraper(
    private val client: HttpClient = HttpClient(CIO),
    private val targetBoards: List<String> = listOf("duolingo", "figma", "reddit", "gusto", "robinhood", "datadog", "stripe", "pinterest")
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val queryLower = query.lowercase().trim()
        val locationLower = location.lowercase().trim()

        for (board in targetBoards) {
            try {
                val response = client.get("https://boards-api.greenhouse.io/v1/boards/$board/jobs")
                val responseBody = response.bodyAsText()
                val rootObj = Json.parseToJsonElement(responseBody).jsonObject
                val jobsArray = rootObj["jobs"]?.jsonArray ?: continue

                for (element in jobsArray) {
                    val obj = element.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: ""
                    val title = obj["title"]?.jsonPrimitive?.content ?: ""
                    val company = obj["company_name"]?.jsonPrimitive?.content ?: (board.replaceFirstChar { it.uppercase() })
                    val loc = obj["location"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: ""
                    val url = obj["absolute_url"]?.jsonPrimitive?.content ?: ""

                    val matchesQuery = queryLower.isEmpty() || title.lowercase().contains(queryLower) || loc.lowercase().contains(queryLower)
                    val matchesLocation = locationLower.isEmpty() || loc.lowercase().contains(locationLower)

                    if (matchesQuery && matchesLocation && title.isNotBlank()) {
                        emit(
                            ScrapedJobPosting(
                                id = id.ifBlank { url.hashCode().toString() },
                                portal = "Greenhouse ($board)",
                                title = title,
                                company = company,
                                location = loc.ifBlank { "Unspecified / Remote" },
                                url = url,
                                description = ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip failed board
            }
        }
    }
}

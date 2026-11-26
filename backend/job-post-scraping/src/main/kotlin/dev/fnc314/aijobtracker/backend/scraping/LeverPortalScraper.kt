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
 * Native Kotlin HTTP scraper for company career boards hosted on Lever ATS.
 */
public class LeverPortalScraper(
    private val client: HttpClient = HttpClient(CIO),
    private val targetBoards: List<String> = listOf("spotify", "palantir", "affirm", "twitch", "plaid", "benchling", "eventbrite")
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val queryLower = query.lowercase().trim()
        val locationLower = location.lowercase().trim()

        for (board in targetBoards) {
            try {
                val response = client.get("https://api.lever.co/v0/postings/$board?mode=json")
                val responseBody = response.bodyAsText()
                val postingsArray = Json.parseToJsonElement(responseBody).jsonArray

                for (element in postingsArray) {
                    val obj = element.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: ""
                    val title = obj["text"]?.jsonPrimitive?.content ?: ""
                    val company = board.replaceFirstChar { it.uppercase() }
                    val categories = obj["categories"]?.jsonObject
                    val loc = categories?.get("location")?.jsonPrimitive?.content
                        ?: obj["workplaceType"]?.jsonPrimitive?.content
                        ?: "Unspecified / Remote"
                    val url = obj["hostedUrl"]?.jsonPrimitive?.content ?: ""

                    val matchesQuery = queryLower.isEmpty() || title.lowercase().contains(queryLower) || loc.lowercase().contains(queryLower)
                    val matchesLocation = locationLower.isEmpty() || loc.lowercase().contains(locationLower)

                    if (matchesQuery && matchesLocation && title.isNotBlank()) {
                        emit(
                            ScrapedJobPosting(
                                id = id.ifBlank { url.hashCode().toString() },
                                portal = "Lever ($board)",
                                title = title,
                                company = company,
                                location = loc,
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

package dev.fnc314.aijobtracker.backend.scraping

import dev.fnc314.aijobtracker.backend.scraping.model.ScrapedJobPosting
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Native Kotlin HTTP scraper for company career boards hosted on Ashby ATS.
 */
public class AshbyPortalScraper(
    private val client: HttpClient = HttpClient(CIO),
    private val targetBoards: List<String> = listOf("openai", "ramp", "linear", "retool", "vercel", "cursor", "perplexity", "anthropic")
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val queryLower = query.lowercase().trim()
        val locationLower = location.lowercase().trim()

        for (board in targetBoards) {
            try {
                val url = "https://api.ashbyhq.com/posting-api/job-board/$board"
                val response = client.get(url)
                val responseBody = response.bodyAsText()
                val rootObj = Json.parseToJsonElement(responseBody).jsonObject
                val jobsArray = rootObj["jobs"]?.jsonArray ?: continue

                for (element in jobsArray) {
                    val obj = element.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: ""
                    val title = obj["title"]?.jsonPrimitive?.content ?: ""
                    val jobUrl = obj["jobUrl"]?.jsonPrimitive?.content ?: ""
                    val isRemote = obj["isRemote"]?.jsonPrimitive?.booleanOrNull ?: false
                    val loc = obj["location"]?.jsonPrimitive?.content
                        ?: (if (isRemote) "Remote" else "Unspecified")

                    val matchesQuery = queryLower.isEmpty() || title.lowercase().contains(queryLower)
                    val matchesLocation = locationLower.isEmpty() || loc.lowercase().contains(locationLower) ||
                        (locationLower.contains("remote") && isRemote)

                    if (matchesQuery && matchesLocation && title.isNotBlank()) {
                        emit(
                            ScrapedJobPosting(
                                id = id.ifBlank { jobUrl.hashCode().toString() },
                                portal = "ASHBY",
                                title = title,
                                company = board.replaceFirstChar { it.uppercase() },
                                location = loc,
                                url = jobUrl,
                                description = obj["descriptionPlain"]?.jsonPrimitive?.content ?: ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip failed board gracefully
            }
        }
    }
}

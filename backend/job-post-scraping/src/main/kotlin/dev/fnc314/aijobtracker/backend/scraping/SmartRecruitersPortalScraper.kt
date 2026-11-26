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
 * Native Kotlin HTTP scraper for company career boards hosted on SmartRecruiters ATS.
 */
public class SmartRecruitersPortalScraper(
    private val client: HttpClient = HttpClient(CIO),
    private val targetCompanies: List<String> = listOf("Square", "smartrecruiters", "visa", "BoschGroup", "Skechers")
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val queryLower = query.lowercase().trim()
        val locationLower = location.lowercase().trim()

        for (company in targetCompanies) {
            try {
                val queryParam = if (query.isNotBlank()) "&q=${query.trim()}" else ""
                val url = "https://api.smartrecruiters.com/v1/companies/$company/postings?limit=50$queryParam"
                val response = client.get(url)
                val responseBody = response.bodyAsText()
                val rootObj = Json.parseToJsonElement(responseBody).jsonObject
                val contentArray = rootObj["content"]?.jsonArray ?: continue

                for (element in contentArray) {
                    val obj = element.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: ""
                    val title = obj["name"]?.jsonPrimitive?.content ?: ""
                    val locObj = obj["location"]?.jsonObject
                    val city = locObj?.get("city")?.jsonPrimitive?.content ?: ""
                    val fullLocation = locObj?.get("fullLocation")?.jsonPrimitive?.content ?: city
                    val isRemote = locObj?.get("remote")?.jsonPrimitive?.booleanOrNull ?: false
                    val loc = if (isRemote && !fullLocation.contains("Remote", ignoreCase = true)) {
                        "$fullLocation (Remote)"
                    } else fullLocation.ifBlank { "Unspecified" }

                    val jobUrl = "https://jobs.smartrecruiters.com/$company/$id"
                    val matchesQuery = queryLower.isEmpty() || title.lowercase().contains(queryLower)
                    val matchesLocation = locationLower.isEmpty() || loc.lowercase().contains(locationLower) ||
                        (locationLower.contains("remote") && isRemote)

                    if (matchesQuery && matchesLocation && title.isNotBlank()) {
                        emit(
                            ScrapedJobPosting(
                                id = id.ifBlank { jobUrl.hashCode().toString() },
                                portal = "SMARTRECRUITERS",
                                title = title,
                                company = company,
                                location = loc,
                                url = jobUrl,
                                description = ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip failed company
            }
        }
    }
}

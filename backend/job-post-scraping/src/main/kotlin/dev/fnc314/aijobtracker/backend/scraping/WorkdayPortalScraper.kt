package dev.fnc314.aijobtracker.backend.scraping

import dev.fnc314.aijobtracker.backend.scraping.model.ScrapedJobPosting
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * Workday tenant connection configuration.
 */
public data class WorkdayTenantTarget(
    public val name: String,
    public val host: String,
    public val tenant: String,
    public val site: String
)

/**
 * Native Kotlin HTTP scraper for company career portals hosted on Workday CXS.
 */
public class WorkdayPortalScraper(
    private val client: HttpClient = HttpClient(CIO),
    private val tenants: List<WorkdayTenantTarget> = listOf(
        WorkdayTenantTarget("Nvidia", "nvidia.wd5.myworkdayjobs.com", "nvidia", "NVIDIAExternalCareerSite"),
        WorkdayTenantTarget("Target", "target.wd5.myworkdayjobs.com", "target", "targetcareers"),
        WorkdayTenantTarget("PayPal", "paypal.wd1.myworkdayjobs.com", "paypal", "custom_careers"),
        WorkdayTenantTarget("Autodesk", "autodesk.wd1.myworkdayjobs.com", "autodesk", "Ext")
    )
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val locationLower = location.lowercase().trim()

        for (target in tenants) {
            try {
                val url = "https://${target.host}/wday/cxs/${target.tenant}/${target.site}/jobs"
                val requestPayload = buildJsonObject {
                    putJsonObject("appliedFacets") {}
                    put("limit", 20)
                    put("offset", 0)
                    put("searchText", query)
                }

                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                    setBody(requestPayload.toString())
                }

                val responseBody = response.bodyAsText()
                val rootObj = Json.parseToJsonElement(responseBody).jsonObject
                val postingsArray = rootObj["jobPostings"]?.jsonArray ?: continue

                for (element in postingsArray) {
                    val obj = element.jsonObject
                    val title = obj["title"]?.jsonPrimitive?.content ?: ""
                    val externalPath = obj["externalPath"]?.jsonPrimitive?.content ?: ""
                    val loc = obj["locationsText"]?.jsonPrimitive?.content ?: "Unspecified / Remote"
                    val id = externalPath.split("_").lastOrNull() ?: externalPath
                    val fullUrl = "https://${target.host}/en-US/${target.site}$externalPath"

                    val matchesLocation = locationLower.isEmpty() || loc.lowercase().contains(locationLower)

                    if (matchesLocation && title.isNotBlank()) {
                        emit(
                            ScrapedJobPosting(
                                id = id.ifBlank { fullUrl.hashCode().toString() },
                                portal = "Workday (${target.name})",
                                title = title,
                                company = target.name,
                                location = loc,
                                url = fullUrl,
                                description = ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip failed tenant gracefully
            }
        }
    }
}

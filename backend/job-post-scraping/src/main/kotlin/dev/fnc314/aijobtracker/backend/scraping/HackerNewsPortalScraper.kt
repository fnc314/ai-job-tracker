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
 * Native Kotlin HTTP scraper querying Algolia API for comments in monthly Hacker News "Who is hiring?" threads.
 */
public class HackerNewsPortalScraper(
    private val client: HttpClient = HttpClient(CIO)
) : PortalScraper {

    override fun invoke(query: String, location: String): Flow<ScrapedJobPosting> = flow {
        val queryParam = query.ifBlank { "Android" }.trim()
        val locationLower = location.lowercase().trim()

        try {
            // 1. Fetch latest 'Who is hiring' thread
            val storyResp = client.get("https://hn.algolia.com/api/v1/search_by_date?tags=story,author_whoishiring&hitsPerPage=10")
            val storyBody = storyResp.bodyAsText()
            val storyHits = Json.parseToJsonElement(storyBody).jsonObject["hits"]?.jsonArray ?: return@flow
            val hiringStory = storyHits.firstOrNull { hit ->
                val title = hit.jsonObject["title"]?.jsonPrimitive?.content?.lowercase() ?: ""
                title.contains("who is hiring") && !title.contains("wants to be hired")
            }?.jsonObject ?: return@flow

            val storyId = hiringStory["objectID"]?.jsonPrimitive?.content ?: return@flow

            // 2. Query comments in thread matching query
            val commentsResp = client.get("https://hn.algolia.com/api/v1/search?tags=comment,story_$storyId&query=$queryParam&hitsPerPage=50")
            val commentsBody = commentsResp.bodyAsText()
            val commentHits = Json.parseToJsonElement(commentsBody).jsonObject["hits"]?.jsonArray ?: return@flow

            for (element in commentHits) {
                val hit = element.jsonObject
                val objectId = hit["objectID"]?.jsonPrimitive?.content ?: ""
                val author = hit["author"]?.jsonPrimitive?.content ?: ""
                val rawText = hit["comment_text"]?.jsonPrimitive?.content ?: ""
                val cleanText = rawText.replace(Regex("<[^>]+>"), " ")
                    .replace("&amp;", "&")
                    .replace("&#x2F;", "/")
                    .replace("&quot;", "\"")
                val lines = cleanText.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                val header = lines.firstOrNull() ?: cleanText.take(80)
                val parts = header.split("|").map { it.trim() }

                val company = parts.firstOrNull()?.ifBlank { author } ?: author
                val role = parts.getOrNull(1)?.ifBlank { queryParam } ?: queryParam
                val loc = parts.getOrNull(2) ?: "Remote"
                val url = "https://news.ycombinator.com/item?id=$objectId"

                val matchesLocation = locationLower.isEmpty() ||
                    loc.lowercase().contains(locationLower) ||
                    cleanText.lowercase().contains(locationLower)

                if (matchesLocation) {
                    emit(
                        ScrapedJobPosting(
                            id = objectId,
                            portal = "HACKER_NEWS",
                            title = role,
                            company = company,
                            location = loc,
                            url = url,
                            description = cleanText
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Gracefully handle errors
        }
    }
}

package dev.fnc314.aijobtracker.backend.scraping

import dev.fnc314.aijobtracker.backend.scraping.model.ScrapedJobPosting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Single Abstract Method (SAM) functional interface for multi-portal job scraping.
 *
 * Encapsulates the execution contract for querying and aggregating job postings
 * across supported platforms via [invoke].
 */
public fun interface JobScraperService {

    /**
     * Executes aggregated scraping across registered portal scrapers.
     *
     * @param query Search query keyword (e.g. role title or technology stack).
     * @param location Target geographic location or remote constraint.
     * @param sources Optional collection of channel enum names (e.g. `setOf("ASHBY", "WORKDAY")`).
     *                When null or empty, all supported scrapers are executed.
     * @return Flow emitting discovered unique [ScrapedJobPosting] instances.
     */
    public operator fun invoke(
        query: String,
        location: String,
        sources: Set<String>?
    ): Flow<ScrapedJobPosting>

    public companion object {
        /**
         * Creates a default [JobScraperService] instance backed by all registered portal scrapers.
         *
         * @param scraperMap Registry mapping channel enum strings to their respective [PortalScraper] implementations.
         * @return Configured [JobScraperService] instance.
         */
        public fun create(
            scraperMap: Map<String, PortalScraper> = mapOf(
                "GREENHOUSE" to GreenhousePortalScraper(),
                "LEVER" to LeverPortalScraper(),
                "WORKDAY" to WorkdayPortalScraper(),
                "ASHBY" to AshbyPortalScraper(),
                "SMARTRECRUITERS" to SmartRecruitersPortalScraper(),
                "REMOTEOK" to RemoteOkPortalScraper(),
                "HACKER_NEWS" to HackerNewsPortalScraper()
            )
        ): JobScraperService = JobScraperService { query, location, sources ->
            flow {
                val seenUrls = mutableSetOf<String>()
                val activeScrapers = if (sources.isNullOrEmpty()) {
                    scraperMap.values
                } else {
                    val upperSources = sources.map { it.uppercase().trim() }.toSet()
                    scraperMap.filterKeys { it in upperSources }.values
                }

                for (scraper in activeScrapers) {
                    scraper(query, location).collect { posting ->
                        if (posting.url.isNotBlank() && seenUrls.add(posting.url)) {
                            emit(posting)
                        }
                    }
                }
            }
        }
    }
}

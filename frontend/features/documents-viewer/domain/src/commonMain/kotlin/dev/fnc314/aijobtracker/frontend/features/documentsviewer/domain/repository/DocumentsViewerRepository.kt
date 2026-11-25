package dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.repository

/**
 * Repository contract for fetching artifact content from remote URLs.
 */
public fun interface DocumentsViewerRepository {
    /**
     * Fetches text content from the given URL.
     *
     * @param url The URL to fetch.
     * @return The text content or null if the fetch failed.
     */
    public suspend operator fun invoke(url: String): String?
}

package dev.fnc314.aijobtracker.frontend.features.documentsviewer.data.repository.impl

import dev.fnc314.aijobtracker.frontend.core.data.source.NetworkDataSource
import dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.repository.DocumentsViewerRepository

internal class DocumentsViewerRepositoryImpl(
    private val networkDataSource: NetworkDataSource,
) : DocumentsViewerRepository {
    override suspend fun invoke(url: String): String? =
        runCatching { networkDataSource.get(url) }.getOrNull()
}

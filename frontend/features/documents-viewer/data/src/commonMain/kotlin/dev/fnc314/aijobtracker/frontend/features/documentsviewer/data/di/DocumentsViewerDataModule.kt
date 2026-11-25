package dev.fnc314.aijobtracker.frontend.features.documentsviewer.data.di

import dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.repository.DocumentsViewerRepository
import dev.fnc314.aijobtracker.frontend.features.documentsviewer.data.repository.impl.DocumentsViewerRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module declaring data layer dependencies for the artifact previewer feature.
 */
public val documentsViewerDataModule: Module = module {
    singleOf(::DocumentsViewerRepositoryImpl) bind DocumentsViewerRepository::class
}

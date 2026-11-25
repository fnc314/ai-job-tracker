package dev.fnc314.aijobtracker.frontend.features.documentsviewer.ui.di

import dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.di.documentsViewerDomainModule
import dev.fnc314.aijobtracker.frontend.features.documentsviewer.ui.DocumentsViewerEntryRegistry
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module declaring UI layer dependencies for the artifact previewer feature.
 */
public val documentsViewerUiModule: Module = module {
    singleOf(::DocumentsViewerEntryRegistry)
}

/**
 * Aggregate Koin module uniting data, domain, and UI definitions for the artifact previewer feature.
 */
public val documentsViewerFeatureModule: Module = module {
}

package dev.fnc314.aijobtracker.frontend.features.documentsviewer.module

import dev.fnc314.aijobtracker.frontend.features.documentsviewer.data.di.documentsViewerDataModule
import dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.di.documentsViewerDomainModule
import dev.fnc314.aijobtracker.frontend.features.documentsviewer.ui.di.documentsViewerUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the artifact-previewer feature.
 */
public val documentsViewerModule: Module = module {
    includes(
        documentsViewerDataModule,
        documentsViewerDomainModule,
        documentsViewerUiModule,
    )
}

package dev.fnc314.aijobtracker.frontend.features.artifactpreviewer.module

import dev.fnc314.aijobtracker.frontend.features.artifactpreviewer.data.di.artifactPreviewerDataModule
import dev.fnc314.aijobtracker.frontend.features.artifactpreviewer.domain.di.artifactPreviewerDomainModule
import dev.fnc314.aijobtracker.frontend.features.artifactpreviewer.ui.di.artifactPreviewerUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the artifact-previewer feature.
 */
public val artifactPreviewerModule: Module = module {
    includes(
        artifactPreviewerDataModule,
        artifactPreviewerDomainModule,
        artifactPreviewerUiModule,
    )
}

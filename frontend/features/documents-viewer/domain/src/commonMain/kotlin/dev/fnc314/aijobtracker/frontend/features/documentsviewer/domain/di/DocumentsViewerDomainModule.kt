package dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.di

import dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.usecase.LoadArtifactContentUseCase
import dev.fnc314.aijobtracker.frontend.features.documentsviewer.domain.usecase.impl.LoadArtifactContentUseCaseImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module declaring domain layer dependencies for the artifact previewer feature.
 */
public val documentsViewerDomainModule: Module = module {
    factoryOf(::LoadArtifactContentUseCaseImpl) bind LoadArtifactContentUseCase::class
}

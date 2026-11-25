package dev.fnc314.aijobtracker.frontend.features.addjobapplication.module

import dev.fnc314.aijobtracker.frontend.features.addjobapplication.data.di.addJobApplicationDataModule
import dev.fnc314.aijobtracker.frontend.features.addjobapplication.domain.di.addJobApplicationDomainModule
import dev.fnc314.aijobtracker.frontend.features.addjobapplication.ui.di.addJobApplicationUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the add-job-application feature.
 */
public val addJobApplicationModule: Module = module {
    includes(
        addJobApplicationDataModule,
        addJobApplicationDomainModule,
        addJobApplicationUiModule,
    )
}

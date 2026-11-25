package dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.module

import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.data.di.jobApplicationDetailsDataModule
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.di.jobApplicationsDetailsDomainModule
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui.di.jobApplicationDetailsUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the job-application-details feature.
 */
public val jobApplicationDetailsModule: Module = module {
    includes(
        jobApplicationDetailsDataModule,
        jobApplicationsDetailsDomainModule,
        jobApplicationDetailsUiModule,
    )
}

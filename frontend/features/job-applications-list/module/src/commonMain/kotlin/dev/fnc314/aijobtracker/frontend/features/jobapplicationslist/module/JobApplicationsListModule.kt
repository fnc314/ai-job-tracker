package dev.fnc314.aijobtracker.frontend.features.jobapplicationslist.module

import dev.fnc314.aijobtracker.frontend.features.jobapplicationslist.data.di.jobApplicationsListDataModule
import dev.fnc314.aijobtracker.frontend.features.jobapplicationslist.domain.di.jobApplicationsListDomainModule
import dev.fnc314.aijobtracker.frontend.features.jobapplicationslist.ui.di.jobApplicationsListUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the job-applications-list feature.
 */
public val jobApplicationsListModule: Module = module {
    includes(
        jobApplicationsListDataModule,
        jobApplicationsListDomainModule,
        jobApplicationsListUiModule,
    )
}

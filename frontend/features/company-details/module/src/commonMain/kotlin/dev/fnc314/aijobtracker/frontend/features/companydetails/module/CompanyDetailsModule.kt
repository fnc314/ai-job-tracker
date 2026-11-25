package dev.fnc314.aijobtracker.frontend.features.companydetails.module

import dev.fnc314.aijobtracker.frontend.features.companydetails.data.di.companyDetailsDataModule
import dev.fnc314.aijobtracker.frontend.features.companydetails.domain.di.companyDetailsDomainModule
import dev.fnc314.aijobtracker.frontend.features.companydetails.ui.di.companyDetailsUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the company-details feature.
 */
public val companyDetailsModule: Module = module {
    includes(
        companyDetailsDataModule,
        companyDetailsDomainModule,
        companyDetailsUiModule,
    )
}

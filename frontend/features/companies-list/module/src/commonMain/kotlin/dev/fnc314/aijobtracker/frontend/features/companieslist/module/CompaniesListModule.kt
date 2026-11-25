package dev.fnc314.aijobtracker.frontend.features.companieslist.module

import dev.fnc314.aijobtracker.frontend.features.companieslist.data.di.companiesListDataModule
import dev.fnc314.aijobtracker.frontend.features.companieslist.domain.di.companiesListDomainModule
import dev.fnc314.aijobtracker.frontend.features.companieslist.ui.di.companiesListUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the companies-list feature.
 */
public val companiesListModule: Module = module {
    includes(
        companiesListDataModule,
        companiesListDomainModule,
        companiesListUiModule,
    )
}

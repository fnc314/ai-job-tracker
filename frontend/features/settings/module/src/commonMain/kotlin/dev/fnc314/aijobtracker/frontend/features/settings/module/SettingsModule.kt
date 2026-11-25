package dev.fnc314.aijobtracker.frontend.features.settings.module

import dev.fnc314.aijobtracker.frontend.features.settings.data.di.settingsDataModule
import dev.fnc314.aijobtracker.frontend.features.settings.domain.di.settingsDomainModule
import dev.fnc314.aijobtracker.frontend.features.settings.ui.di.settingsUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the settings feature.
 */
public val settingsModule: Module = module {
    includes(
        settingsDataModule,
        settingsDomainModule,
        settingsUiModule,
    )
}

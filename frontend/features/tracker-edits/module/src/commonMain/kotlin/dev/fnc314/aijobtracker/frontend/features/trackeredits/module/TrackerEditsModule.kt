package dev.fnc314.aijobtracker.frontend.features.trackeredits.module

import dev.fnc314.aijobtracker.frontend.features.trackeredits.data.di.trackerEditsDataModule
import dev.fnc314.aijobtracker.frontend.features.trackeredits.domain.di.trackerEditsDomainModule
import dev.fnc314.aijobtracker.frontend.features.trackeredits.ui.di.trackerEditsUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the tracker-edits feature.
 */
public val trackerEditsModule: Module = module {
    includes(
        trackerEditsDataModule,
        trackerEditsDomainModule,
        trackerEditsUiModule,
    )
}

package dev.fnc314.aijobtracker.frontend.features.signin.module

import dev.fnc314.aijobtracker.frontend.features.signin.data.di.signInDataModule
import dev.fnc314.aijobtracker.frontend.features.signin.domain.di.signInDomainModule
import dev.fnc314.aijobtracker.frontend.features.signin.ui.di.signInUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the sign-in feature.
 */
public val signInModule: Module = module {
    includes(
        signInDataModule,
        signInDomainModule,
        signInUiModule,
    )
}

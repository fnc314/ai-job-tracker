package dev.fnc314.aijobtracker.frontend.core.auth.di

import dev.fnc314.aijobtracker.frontend.core.auth.AuthTokenProvider
import dev.fnc314.aijobtracker.frontend.core.auth.createPlatformAuthTokenProvider
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module providing [AuthTokenProvider] singleton.
 */
public val coreAuthModule: Module = module {
    single<AuthTokenProvider> { createPlatformAuthTokenProvider() }
}

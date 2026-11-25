package dev.fnc314.aijobtracker.frontend.app.core.di

import dev.fnc314.aijobtracker.frontend.core.artifacts.di.coreArtifactsModule
import dev.fnc314.aijobtracker.frontend.core.auth.di.coreAuthModule
import dev.fnc314.aijobtracker.frontend.core.data.di.coreDataModule
import dev.fnc314.aijobtracker.frontend.core.logging.di.coreLoggingModule
import dev.fnc314.aijobtracker.frontend.core.network.di.coreNetworkModule
import dev.fnc314.aijobtracker.frontend.core.widgets.di.coreWidgetsModule
import dev.fnc314.aijobtracker.frontend.features.addjobapplication.module.addJobApplicationModule
import dev.fnc314.aijobtracker.frontend.features.aiapply.module.aiApplyModule
import dev.fnc314.aijobtracker.frontend.features.artifactpreviewer.module.artifactPreviewerModule
import dev.fnc314.aijobtracker.frontend.features.companieslist.module.companiesListModule
import dev.fnc314.aijobtracker.frontend.features.companydetails.module.companyDetailsModule
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.module.jobApplicationDetailsModule
import dev.fnc314.aijobtracker.frontend.features.jobapplicationslist.module.jobApplicationsListModule
import dev.fnc314.aijobtracker.frontend.features.settings.module.settingsModule
import dev.fnc314.aijobtracker.frontend.features.signin.module.signInModule
import dev.fnc314.aijobtracker.frontend.features.trackeredits.module.trackerEditsModule
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.module.Module as KoinModule
import org.koin.dsl.module

/**
 * Root Koin module annotation for application dependency graph validation.
 */
@Module
public class AppModule

/**
 * Primary application entry point for compile-time Koin verification.
 */
@KoinApplication(modules = [AppModule::class])
public class MainApplication

/**
 * Master Koin module assembling all core and feature assembly modules for the application runtime.
 */
public val appModule: KoinModule = module {
  includes(
    coreArtifactsModule,
    coreAuthModule,
    coreDataModule,
    coreLoggingModule,
    coreNetworkModule,
    coreWidgetsModule,

    // Feature Modules
    signInModule,
    settingsModule,
    addJobApplicationModule,
    aiApplyModule,
    artifactPreviewerModule,
    trackerEditsModule,
    jobApplicationsListModule,
    jobApplicationDetailsModule,
    companiesListModule,
    companyDetailsModule,
  )
}

package dev.fnc314.aijobtracker.frontend.features.aiapply.module

import dev.fnc314.aijobtracker.frontend.features.aiapply.data.di.aiApplyDataModule
import dev.fnc314.aijobtracker.frontend.features.aiapply.domain.di.aiApplyDomainModule
import dev.fnc314.aijobtracker.frontend.features.aiapply.ui.di.aiApplyUiModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Top-level Koin dependency injection module assembling the ai-apply feature.
 */
public val aiApplyModule: Module = module {
    includes(
        aiApplyDataModule,
        aiApplyDomainModule,
        aiApplyUiModule,
    )
}

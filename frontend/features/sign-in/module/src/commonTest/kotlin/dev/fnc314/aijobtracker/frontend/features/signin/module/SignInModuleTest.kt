package dev.fnc314.aijobtracker.frontend.features.signin.module

import dev.fnc314.aijobtracker.frontend.core.auth.model.AuthState
import dev.fnc314.aijobtracker.frontend.core.auth.model.UserAccount
import dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.GetCurrentUserUseCase
import dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.ObserveAuthStateUseCase
import dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.SignInUseCase
import dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.SignOutUseCase
import dev.fnc314.aijobtracker.frontend.features.signin.ui.SignInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class SignInModuleTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testSignInUiModule_resolvesSignInViewModelSuccessfully() {
        val testOverrideModule = module {
            single<SignInUseCase> { SignInUseCase { Result.success(UserAccount("uid", "email")) } }
            single<SignOutUseCase> { SignOutUseCase { Result.success(Unit) } }
            single<ObserveAuthStateUseCase> { ObserveAuthStateUseCase { MutableStateFlow(AuthState.Unauthenticated) } }
            single<GetCurrentUserUseCase> { GetCurrentUserUseCase { null } }
        }

        val app = startKoin {
            modules(
                testOverrideModule,
                signInModule,
            )
        }

        val viewModel: SignInViewModel = app.koin.get()
        assertNotNull(viewModel)
    }
}

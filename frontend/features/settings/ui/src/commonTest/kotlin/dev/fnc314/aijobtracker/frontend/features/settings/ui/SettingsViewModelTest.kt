package dev.fnc314.aijobtracker.frontend.features.settings.ui

import dev.fnc314.aijobtracker.frontend.core.data.model.AuthState
import dev.fnc314.aijobtracker.frontend.core.data.model.UserAccount
import dev.fnc314.aijobtracker.frontend.core.data.model.DeviceRegistrationStatus
import dev.fnc314.aijobtracker.frontend.core.data.settings.AppSettings
import dev.fnc314.aijobtracker.frontend.core.data.settings.UiMode
import dev.fnc314.aijobtracker.frontend.features.settings.domain.repository.SettingsRepository
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.DeregisterDeviceNotificationsUseCase
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.ObserveSettingsUseCase
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.RegisterDeviceNotificationsUseCase
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.UpdateSettingsUseCase
import dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.SignOutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
internal class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeSettingsRepository(
        initial: AppSettings = AppSettings(),
    ) : SettingsRepository {
        private val _settings = MutableStateFlow(initial)
        override val settings: StateFlow<AppSettings> = _settings
        override fun updateSettings(newSettings: AppSettings) {
            _settings.value = newSettings
        }
    }


    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialUiState_EmitsContentMatchingRepository() = runTest(testDispatcher) {
        val repo = FakeSettingsRepository(AppSettings(uiMode = UiMode.SYSTEM, seedColorHex = "#2F5597"))
        val viewModel = SettingsViewModel(
            observeSettingsUseCase = ObserveSettingsUseCase { repo.settings },
            updateSettingsUseCase = UpdateSettingsUseCase { repo.updateSettings(it) },
            signOutUseCase = { Result.success(Unit) },
            deregisterDeviceNotificationsUseCase = DeregisterDeviceNotificationsUseCase { true },
            registerDeviceNotificationsUseCase = RegisterDeviceNotificationsUseCase { true },
        )

        val state = viewModel.uiState.first { it is SettingsUiState.Content }
        assertIs<SettingsUiState.Content>(state)
        assertEquals(UiMode.SYSTEM, state.model.uiMode)
        assertEquals("#2F5597", state.model.seedColorHex)
        assertEquals(false, state.model.isSystemDark)
    }

    @Test
    fun testIntent_ChangeUiModeUpdatesRepositoryAndState() = runTest(testDispatcher) {
        val repo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(
            observeSettingsUseCase = ObserveSettingsUseCase { repo.settings },
            updateSettingsUseCase = UpdateSettingsUseCase { repo.updateSettings(it) },
            signOutUseCase = { Result.success(Unit) },
            deregisterDeviceNotificationsUseCase = DeregisterDeviceNotificationsUseCase { true },
            registerDeviceNotificationsUseCase = RegisterDeviceNotificationsUseCase { true },
        )

        viewModel.onIntent(SettingsUiIntent.ChangeUiMode(UiMode.DARK))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is SettingsUiState.Content && it.model.isSystemDark }
        assertIs<SettingsUiState.Content>(state)
        assertEquals(UiMode.DARK, state.model.uiMode)
        assertEquals(true, state.model.isSystemDark)
    }

    @Test
    fun testIntent_ChangeSeedColorUpdatesRepositoryAndState() = runTest(testDispatcher) {
        val repo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(
            observeSettingsUseCase = ObserveSettingsUseCase { repo.settings },
            updateSettingsUseCase = UpdateSettingsUseCase { repo.updateSettings(it) },
            signOutUseCase = { Result.success(Unit) },
            deregisterDeviceNotificationsUseCase = DeregisterDeviceNotificationsUseCase { true },
            registerDeviceNotificationsUseCase = RegisterDeviceNotificationsUseCase { true },
        )

        viewModel.onIntent(SettingsUiIntent.ChangeSeedColor(hex = "#FF5722"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is SettingsUiState.Content && it.model.seedColorHex == "#FF5722" }
        assertIs<SettingsUiState.Content>(state)
        assertEquals("#FF5722", state.model.seedColorHex)
    }

    @Test
    fun testIntent_ClickBackEmitsNavigateBack() = runTest(testDispatcher) {
        val repo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(
            observeSettingsUseCase = ObserveSettingsUseCase { repo.settings },
            updateSettingsUseCase = UpdateSettingsUseCase { repo.updateSettings(it) },
            signOutUseCase = { Result.success(Unit) },
            deregisterDeviceNotificationsUseCase = DeregisterDeviceNotificationsUseCase { true },
            registerDeviceNotificationsUseCase = RegisterDeviceNotificationsUseCase { true },
        )

        viewModel.onIntent(SettingsUiIntent.ClickBack)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is SettingsUiState.NavigateBack }
        assertEquals(SettingsUiState.NavigateBack, state)
    }

    @Test
    fun testIntent_RegisterNotificationsUpdatesState() = runTest(testDispatcher) {
        val repo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(
            observeSettingsUseCase = ObserveSettingsUseCase { repo.settings },
            updateSettingsUseCase = UpdateSettingsUseCase { repo.updateSettings(it) },
            signOutUseCase = { Result.success(Unit) },
            deregisterDeviceNotificationsUseCase = DeregisterDeviceNotificationsUseCase { true },
            registerDeviceNotificationsUseCase = RegisterDeviceNotificationsUseCase { true },
        )

        viewModel.onIntent(SettingsUiIntent.RegisterNotifications)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first {
            it is SettingsUiState.Content &&
            it.model.notificationStatus == DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_ACTIVE)
        }
        assertIs<SettingsUiState.Content>(state)
        assertEquals(
            DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_ACTIVE),
            state.model.notificationStatus
        )
    }

    @Test
    fun testIntent_DeregisterNotificationsUpdatesState() = runTest(testDispatcher) {
        val repo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(
            observeSettingsUseCase = ObserveSettingsUseCase { repo.settings },
            updateSettingsUseCase = UpdateSettingsUseCase { repo.updateSettings(it) },
            signOutUseCase = { Result.success(Unit) },
            deregisterDeviceNotificationsUseCase = DeregisterDeviceNotificationsUseCase { true },
            registerDeviceNotificationsUseCase = RegisterDeviceNotificationsUseCase { true },
        )

        viewModel.onIntent(SettingsUiIntent.DeregisterNotifications)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first {
            it is SettingsUiState.Content &&
            it.model.notificationStatus == DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_DEREGISTERED)
        }
        assertIs<SettingsUiState.Content>(state)
        assertEquals(
            DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_DEREGISTERED),
            state.model.notificationStatus
        )
    }
}

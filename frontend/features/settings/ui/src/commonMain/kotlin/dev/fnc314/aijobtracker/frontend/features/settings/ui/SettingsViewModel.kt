package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fnc314.aijobtracker.frontend.core.data.model.DeviceRegistrationStatus
import dev.fnc314.aijobtracker.frontend.core.data.settings.AppSettings
import dev.fnc314.aijobtracker.frontend.core.data.settings.UiMode
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.DeregisterDeviceNotificationsUseCase
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.ObserveSettingsUseCase
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.RegisterDeviceNotificationsUseCase
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.UpdateSettingsUseCase
import dev.fnc314.aijobtracker.frontend.features.signin.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Presentation ViewModel managing user appearance preferences, theme customization, and session sign-out.
 */
public class SettingsViewModel(
  private val observeSettingsUseCase: ObserveSettingsUseCase,
  private val updateSettingsUseCase: UpdateSettingsUseCase,
  private val authRepository: AuthRepository,
  private val deregisterDeviceNotificationsUseCase: DeregisterDeviceNotificationsUseCase,
  private val registerDeviceNotificationsUseCase: RegisterDeviceNotificationsUseCase,
) : ViewModel() {

  private val _isNavigatingBack: MutableStateFlow<Boolean> = MutableStateFlow(false)
  private val _isSigningOut: MutableStateFlow<Boolean> = MutableStateFlow(false)
  private val _notificationStatus: MutableStateFlow<DeviceNotificationUiState> =
    MutableStateFlow(DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_UNSPECIFIED))

  private val seedSuggestions: List<String> = listOf(
    "#2F5597", // Professional Blue
    "#1B5E20", // Emerald
    "#3F51B5", // Indigo
    "#FF8F00", // Amber
    "#00838F", // Teal
    "#B71C1C", // Crimson
  )

  /**
   * Observable stream of [SettingsUiState] emitting the current UI theme, color choices, and navigation triggers.
   */
  public val uiState: StateFlow<SettingsUiState> = combine(
    observeSettingsUseCase(),
    _isNavigatingBack,
    _isSigningOut,
    _notificationStatus,
  ) { settings: AppSettings, isNavigatingBack: Boolean, isSigningOut: Boolean, notificationStatus: DeviceNotificationUiState ->
    when {
      isNavigatingBack -> SettingsUiState.NavigateBack
      isSigningOut -> SettingsUiState.NavigateToSignIn
      else -> SettingsUiState.Content(
        model = SettingsUiModel(
          uiMode = settings.uiMode,
          seedColorHex = settings.seedColorHex,
          isSystemDark = settings.uiMode == UiMode.DARK,
          randomSeedSuggestions = seedSuggestions,
          kmpInspectorEnabled = settings.enableKmpInspector,
          inAppTextViewerEnabled = settings.renderMarkdownInternally,
          notificationStatus = notificationStatus,
        ),
      )
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = SettingsUiState.Loading,
  )

  /**
   * Dispatches a user intent to be processed by this ViewModel.
   *
   * @param intent The [SettingsUiIntent] to process.
   */
  public fun onIntent(intent: SettingsUiIntent): Unit {
    when (intent) {
      is SettingsUiIntent.ChangeUiMode -> handleChangeUiMode(intent.uiMode)
      is SettingsUiIntent.ChangeSeedColor -> handleChangeSeedColor(intent.hex)
      is SettingsUiIntent.ToggleKmpInspector -> handleKmpInspectorToggle(intent.enabled)
      is SettingsUiIntent.ToggleInAppTextViewer -> handleInAppTextViewerToggle(intent.enabled)
      SettingsUiIntent.DeregisterNotifications -> handleDeregisterNotifications()
      SettingsUiIntent.RegisterNotifications -> handleRegisterNotifications()
      SettingsUiIntent.NotificationPermissionDenied -> handleNotificationPermissionDenied()
      SettingsUiIntent.ClickSignOut -> handleSignOut()
      SettingsUiIntent.ClickBack -> handleClickBack()
    }
  }

  private fun handleKmpInspectorToggle(enabled: Boolean) {
    val currentSettings: AppSettings = observeSettingsUseCase().value
    updateSettingsUseCase(currentSettings.copy(enableKmpInspector = enabled))
  }

  private fun handleInAppTextViewerToggle(enabled: Boolean) {
    val currentSettings: AppSettings = observeSettingsUseCase().value
    updateSettingsUseCase(currentSettings.copy(
      enableInAppTextViewer = enabled,
      renderMarkdownInternally = enabled,
    ))
  }

  private fun handleChangeUiMode(uiMode: UiMode): Unit {
    val currentSettings: AppSettings = observeSettingsUseCase().value
    updateSettingsUseCase(currentSettings.copy(uiMode = uiMode))
  }

  @Suppress("MagicNumber")
  private fun handleChangeSeedColor(hex: String): Unit {
    // Defense-in-depth: accept both #RRGGBB and RRGGBB, normalize to #RRGGBB
    val normalized = hex.removePrefix("#")
    if (normalized.length == 6 && normalized.matches(Regex("[0-9a-fA-F]{6}"))) {
      val currentSettings: AppSettings = observeSettingsUseCase().value
      updateSettingsUseCase(currentSettings.copy(seedColorHex = "#$normalized"))
    }
  }

  private fun handleDeregisterNotifications() {
    viewModelScope.launch {
      _notificationStatus.value = DeviceNotificationUiState.Deregistering
      val success = deregisterDeviceNotificationsUseCase()
      _notificationStatus.value = if (success) {
        DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_DEREGISTERED)
      } else {
        DeviceNotificationUiState.Failed("Deregistration failed")
      }
    }
  }

  private fun handleRegisterNotifications() {
    viewModelScope.launch {
      _notificationStatus.value = DeviceNotificationUiState.Registering
      val success = registerDeviceNotificationsUseCase()
      _notificationStatus.value = if (success) {
        DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_ACTIVE)
      } else {
        DeviceNotificationUiState.Failed("Registration failed")
      }
    }
  }

  private fun handleNotificationPermissionDenied() {
    _notificationStatus.value = DeviceNotificationUiState.PermissionDenied
  }

  private fun handleSignOut(): Unit {
    viewModelScope.launch {
      authRepository.signOut()
      _isSigningOut.value = true
    }
  }

  private fun handleClickBack(): Unit {
    _isNavigatingBack.value = true
  }
}

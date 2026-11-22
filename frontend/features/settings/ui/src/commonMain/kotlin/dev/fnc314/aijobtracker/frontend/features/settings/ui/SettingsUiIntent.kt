package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.runtime.Immutable
import dev.fnc314.aijobtracker.frontend.core.data.model.DeviceRegistrationStatus
import dev.fnc314.aijobtracker.frontend.core.data.platform.PlatformCapabilities
import dev.fnc314.aijobtracker.frontend.core.data.settings.UiMode

/**
 * User-initiated intents dispatched to [SettingsViewModel].
 */
public sealed interface SettingsUiIntent {
    /** Changes the active theme mode ([UiMode]). */
    public data class ChangeUiMode(public val uiMode: UiMode) : SettingsUiIntent

    /** Sets the primary Material 3 seed palette color hex code. */
    public data class ChangeSeedColor(public val hex: String) : SettingsUiIntent

    /** Requests user sign out. */
    public data object ClickSignOut : SettingsUiIntent

    /** Requests back navigation to the preceding screen. */
    public data object ClickBack : SettingsUiIntent

    /** Toggles the multiplatform diagnostic inspector overlay. */
    public data class ToggleKmpInspector(public val enabled: Boolean) : SettingsUiIntent

    /** Toggles the in-app Markdown/text viewer (mobile only). */
    public data class ToggleInAppTextViewer(public val enabled: Boolean) : SettingsUiIntent

    /** Deregisters the active device from receiving push notifications. */
    public data object DeregisterNotifications : SettingsUiIntent

    /** Registers or updates the active device to receive push notifications. */
    public data object RegisterNotifications : SettingsUiIntent

    /** User denied system notification permission request. */
    public data object NotificationPermissionDenied : SettingsUiIntent
}

/**
 * Presentation state representing device push notification status, bridging canonical
 * [DeviceRegistrationStatus] with in-flight UI operation feedback.
 */
public sealed interface DeviceNotificationUiState {
    /**
     * Canonical state synced with backend/Firestore.
     */
    public data class Settled(public val status: DeviceRegistrationStatus) : DeviceNotificationUiState

    /** In-flight registration operation. */
    public data object Registering : DeviceNotificationUiState

    /** In-flight deregistration operation. */
    public data object Deregistering : DeviceNotificationUiState

    /** Permission denied by user. */
    public data object PermissionDenied : DeviceNotificationUiState

    /** Registration or deregistration operation failed. */
    public data class Failed(public val error: String? = null) : DeviceNotificationUiState
}

/**
 * Presentation model capturing user-configurable application preferences.
 *
 * @property uiMode Active theme mode ([UiMode]).
 * @property seedColorHex Primary brand seed color formatted as `#RRGGBB`.
 * @property isSystemDark Whether dark theme appearance is currently active.
 * @property randomSeedSuggestions Curated preset seed color hex strings for quick selection.
 */
@Immutable
public data class SettingsUiModel(
    public val uiMode: UiMode,
    public val seedColorHex: String,
    public val isSystemDark: Boolean,
    public val randomSeedSuggestions: List<String>,
    public val kmpInspectorEnabled: Boolean,
    public val showInAppTextViewerSetting: Boolean = PlatformCapabilities.supportsInAppTextViewerToggle,
    public val inAppTextViewerEnabled: Boolean = false,
    public val supportsPushNotifications: Boolean = PlatformCapabilities.supportsPushNotifications,
    public val notificationStatus: DeviceNotificationUiState = DeviceNotificationUiState.Settled(DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_UNSPECIFIED),
)

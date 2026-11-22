package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.runtime.Composable

/**
 * Multiplatform interface for requesting notification permission on platforms
 * that require runtime user consent (e.g., Android 13+).
 */
public interface NotificationPermissionRequester {
    /**
     * Checks if permission is needed and requests it if not already granted.
     * Invokes [onResult] with true if granted or not required on the platform.
     */
    public fun requestNotificationPermission(onResult: (Boolean) -> Unit)
}

/**
 * Composable factory providing a platform-specific [NotificationPermissionRequester].
 */
@Composable
public expect fun rememberNotificationPermissionRequester(): NotificationPermissionRequester

package dev.fnc314.aijobtracker.frontend.features.settings.ui

/**
 * Multiplatform contract allowing the app to open the OS system settings page for this app.
 */
public expect class PlatformAppSettingsOpener() {
    public fun openAppSettings(): Unit
}

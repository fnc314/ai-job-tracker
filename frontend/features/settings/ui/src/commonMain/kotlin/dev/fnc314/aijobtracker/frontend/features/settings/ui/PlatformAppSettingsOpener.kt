package dev.fnc314.aijobtracker.frontend.features.settings.ui

/**
 * Multiplatform contract allowing the app to open the OS system settings page for this app.
 */
public expect class PlatformAppSettingsOpener() {
    /**
     * Opens the operating system settings screen associated with this application.
     */
    public fun openAppSettings(): Unit
}

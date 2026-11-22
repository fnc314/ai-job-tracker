package dev.fnc314.aijobtracker.frontend.features.settings.ui

/**
 * Represents the multi-stage UI lifecycle state of a permission request flow across platforms.
 */
public enum class PermissionUiStage {
    /**
     * Initial state where the permission has not yet been requested or denied.
     */
    INITIAL,

    /**
     * The permission was denied once; platform guidelines (e.g. Android) recommend displaying
     * educational rationale explaining why the permission is needed before prompting the OS dialog again.
     */
    RATIONALE,

    /**
     * The permission has been permanently denied or denied after rationale ("Don't ask again").
     * The OS prompt will no longer appear, so the user must be directed to OS Application Settings.
     */
    PERMANENTLY_DENIED,

    /**
     * Permission has been granted.
     */
    GRANTED,
}

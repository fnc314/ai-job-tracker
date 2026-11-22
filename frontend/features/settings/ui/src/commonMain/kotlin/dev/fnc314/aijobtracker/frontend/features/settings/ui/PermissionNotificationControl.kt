package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Multiplatform control handling push notification permission requests and device registration.
 *
 * Implemented via platform actuals to handle platform-specific permission lifecycles:
 * - On Android: Handles initial prompt, educational rationale on 1st denial (with screen reader live-region announcement),
 *   second prompt invocation, and routing to system application settings upon permanent denial.
 * - On iOS: Handles single system dialog prompt, transitioning to settings routing on denial.
 * - On Desktop JVM / Web: Streamlined registration flow without mobile runtime permission dialogs.
 *
 * @param isRegistered Whether this device is currently registered for notifications with the backend.
 * @param onRegisterRequested Callback invoked when the user initiates registration (after permissions granted).
 * @param onDeregisterRequested Callback invoked when the user initiates deregistration.
 * @param modifier Composable layout modifier.
 */
@Composable
public expect fun PermissionNotificationControl(
    isRegistered: Boolean,
    onRegisterRequested: () -> Unit,
    onDeregisterRequested: () -> Unit,
    modifier: Modifier = Modifier,
)

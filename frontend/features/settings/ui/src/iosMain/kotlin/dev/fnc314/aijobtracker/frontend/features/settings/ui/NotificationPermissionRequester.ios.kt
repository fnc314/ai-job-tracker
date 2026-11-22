package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private object IosNotificationPermissionRequester : NotificationPermissionRequester {
    override fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
        onResult(true)
    }
}

@Composable
public actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester {
    return remember { IosNotificationPermissionRequester }
}

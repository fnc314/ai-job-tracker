package dev.fnc314.aijobtracker.frontend.features.settings.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private class AndroidNotificationPermissionRequester(
    private val launchRequest: ((Boolean) -> Unit) -> Unit,
) : NotificationPermissionRequester {
    override fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launchRequest(onResult)
        } else {
            onResult(true)
        }
    }
}

@Composable
public actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester {
    var pendingCallback: ((Boolean) -> Unit)? = remember { null }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        pendingCallback?.invoke(isGranted)
        pendingCallback = null
    }

    return remember {
        AndroidNotificationPermissionRequester { callback ->
            pendingCallback = callback
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

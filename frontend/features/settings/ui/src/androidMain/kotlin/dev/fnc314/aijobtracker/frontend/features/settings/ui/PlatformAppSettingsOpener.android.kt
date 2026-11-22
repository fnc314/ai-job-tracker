package dev.fnc314.aijobtracker.frontend.features.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import dev.fnc314.aijobtracker.frontend.core.data.ApplicationContextHolder

public actual class PlatformAppSettingsOpener actual constructor() {
    public actual fun openAppSettings(): Unit {
        val context = ApplicationContextHolder.application ?: return
        try {
            // First attempt: Direct App Notification Settings page (API 26+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val highlightBundle = Bundle().apply {
                    putString(":settings:fragment_args_key", "notifications")
                }
                val notificationSettingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    putExtra(":settings:show_fragment_args", highlightBundle)
                    putExtra(":settings:fragment_args_key", "notifications")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (notificationSettingsIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(notificationSettingsIntent)
                    return
                }
            }

            // Fallback: Application Details Settings page with highlight parameter for notifications / permissions row
            val highlightBundle = Bundle().apply {
                putString(":settings:fragment_args_key", "permission_settings")
            }
            val detailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                putExtra(":settings:show_fragment_args", highlightBundle)
                putExtra(":settings:fragment_args_key", "permission_settings")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(detailsIntent)
        } catch (_: Exception) {}
    }
}

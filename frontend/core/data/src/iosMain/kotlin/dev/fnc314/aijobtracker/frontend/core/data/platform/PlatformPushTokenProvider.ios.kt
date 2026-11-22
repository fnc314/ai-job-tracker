package dev.fnc314.aijobtracker.frontend.core.data.platform

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCancellableCoroutine

internal class IosPlatformPushTokenProvider : PlatformPushTokenProvider {
    override suspend fun getPushToken(): String? {
        // Request iOS User Notification permissions (Alert, Badge, Sound)
        val granted = requestNotificationPermissions()
        return if (granted) {
            // When APNS or Firebase iOS SDK is configured, device token or FID is resolved here.
            null
        } else {
            null
        }
    }

    private suspend fun requestNotificationPermissions(): Boolean = suspendCancellableCoroutine { continuation ->
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options) { granted, _ ->
            continuation.resume(granted)
        }
    }
}

public actual fun createPlatformPushTokenProvider(): PlatformPushTokenProvider =
    IosPlatformPushTokenProvider()

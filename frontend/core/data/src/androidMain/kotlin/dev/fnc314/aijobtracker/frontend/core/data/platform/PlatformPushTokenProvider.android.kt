package dev.fnc314.aijobtracker.frontend.core.data.platform

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dev.fnc314.aijobtracker.frontend.core.logging.AppLogger
import kotlinx.coroutines.tasks.await

internal class AndroidPlatformPushTokenProvider : PlatformPushTokenProvider {
    override suspend fun getPushToken(): String? {
        return try {
            // In firebase-messaging 26.0.2 with V1 registration, register() completes FCM registration
            FirebaseMessaging.getInstance().register().await()
            val fid = FirebaseInstallations.getInstance().id.await()
            AppLogger.d("PlatformPushToken") { "Obtained V1 installation ID (FID): ${fid?.take(15)}..." }
            fid
        } catch (t: Throwable) {
            AppLogger.e("PlatformPushToken") { "Error registering FCM / obtaining FID: ${t.message}" }
            null
        }
    }
}

public actual fun createPlatformPushTokenProvider(): PlatformPushTokenProvider =
    AndroidPlatformPushTokenProvider()


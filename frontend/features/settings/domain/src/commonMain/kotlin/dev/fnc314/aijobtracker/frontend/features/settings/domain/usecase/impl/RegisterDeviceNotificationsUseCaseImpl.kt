package dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.impl

import dev.fnc314.aijobtracker.frontend.core.data.platform.PlatformDeviceInfoProvider
import dev.fnc314.aijobtracker.frontend.core.data.platform.PlatformPushTokenProvider
import dev.fnc314.aijobtracker.frontend.core.data.source.NotificationRemoteDataSource
import dev.fnc314.aijobtracker.frontend.core.logging.AppLogger
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.RegisterDeviceNotificationsUseCase

internal class RegisterDeviceNotificationsUseCaseImpl(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val platformDeviceInfoProvider: PlatformDeviceInfoProvider,
    private val platformPushTokenProvider: PlatformPushTokenProvider,
) : RegisterDeviceNotificationsUseCase {
    override suspend fun invoke(): Boolean {
        AppLogger.d("RegisterNotifications") { "Fetching push token..." }
        val token = platformPushTokenProvider.getPushToken()
        AppLogger.d("RegisterNotifications") {
            "Push token result: ${if (token.isNullOrBlank()) "NULL/EMPTY" else "OBTAINED (${token.take(10)}...)"}"
        }
        if (token.isNullOrBlank()) return false
        val deviceInfo = platformDeviceInfoProvider.getDeviceInfo()
        AppLogger.d("RegisterNotifications") {
            "Calling registerDevice with installationId=${deviceInfo.installationId}"
        }
        val result = notificationRemoteDataSource.registerDevice(
            token = token,
            platform = deviceInfo.platform,
            installationId = deviceInfo.installationId,
        )
        AppLogger.i("RegisterNotifications") { "registerDevice response: $result" }
        return result
    }
}

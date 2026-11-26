package dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.impl

import dev.fnc314.aijobtracker.frontend.core.data.platform.PlatformDeviceInfoProvider
import dev.fnc314.aijobtracker.frontend.core.data.settings.SettingsStorage
import dev.fnc314.aijobtracker.frontend.core.data.source.NotificationRemoteDataSource
import dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase.DeregisterDeviceNotificationsUseCase

internal class DeregisterDeviceNotificationsUseCaseImpl(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val platformDeviceInfoProvider: PlatformDeviceInfoProvider,
    private val settingsStorage: SettingsStorage,
) : DeregisterDeviceNotificationsUseCase {
    override suspend fun invoke(): Boolean {
        val deviceInfo = platformDeviceInfoProvider.getDeviceInfo()
        val result = notificationRemoteDataSource.deregisterDevice(deviceInfo.installationId)
        if (result) {
            settingsStorage.putString(dev.fnc314.aijobtracker.frontend.core.data.settings.AppSettingsRepository.KEY_LAST_REGISTERED_PUSH_TOKEN, "")
        }
        return result
    }
}

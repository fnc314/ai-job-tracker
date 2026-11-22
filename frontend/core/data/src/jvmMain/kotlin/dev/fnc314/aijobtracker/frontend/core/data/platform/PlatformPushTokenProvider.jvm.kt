package dev.fnc314.aijobtracker.frontend.core.data.platform

internal class JvmPlatformPushTokenProvider : PlatformPushTokenProvider {
    override suspend fun getPushToken(): String? = null
}

public actual fun createPlatformPushTokenProvider(): PlatformPushTokenProvider =
    JvmPlatformPushTokenProvider()

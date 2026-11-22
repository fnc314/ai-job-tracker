package dev.fnc314.aijobtracker.frontend.core.data.platform

internal class JsPlatformPushTokenProvider : PlatformPushTokenProvider {
    override suspend fun getPushToken(): String? = null
}

public actual fun createPlatformPushTokenProvider(): PlatformPushTokenProvider =
    JsPlatformPushTokenProvider()

package dev.fnc314.aijobtracker.frontend.core.data.platform

internal class WasmJsPlatformPushTokenProvider : PlatformPushTokenProvider {
    override suspend fun getPushToken(): String? = null
}

public actual fun createPlatformPushTokenProvider(): PlatformPushTokenProvider =
    WasmJsPlatformPushTokenProvider()

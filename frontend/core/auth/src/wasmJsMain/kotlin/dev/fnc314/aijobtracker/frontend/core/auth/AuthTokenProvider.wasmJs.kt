package dev.fnc314.aijobtracker.frontend.core.auth

internal class WasmJsAuthTokenProvider : AuthTokenProvider {
    override suspend fun getIdToken(forceRefresh: Boolean): String? = null
}

public actual fun createPlatformAuthTokenProvider(): AuthTokenProvider = WasmJsAuthTokenProvider()

package dev.fnc314.aijobtracker.frontend.core.auth

internal class JvmAuthTokenProvider : AuthTokenProvider {
    override suspend fun getIdToken(forceRefresh: Boolean): String? = null
}

public actual fun createPlatformAuthTokenProvider(): AuthTokenProvider = JvmAuthTokenProvider()

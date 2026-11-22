package dev.fnc314.aijobtracker.frontend.core.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

internal class AndroidAuthTokenProvider : AuthTokenProvider {
    override suspend fun getIdToken(forceRefresh: Boolean): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return try {
            user.getIdToken(forceRefresh).await()?.token
        } catch (_: Throwable) {
            null
        }
    }
}

public actual fun createPlatformAuthTokenProvider(): AuthTokenProvider = AndroidAuthTokenProvider()

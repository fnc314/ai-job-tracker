package dev.fnc314.aijobtracker.frontend.core.auth

/**
 * Multiplatform contract providing access to user identity / ID tokens for remote API authentication.
 */
public interface AuthTokenProvider {
    /**
     * Retrieves the current user's Firebase or OAuth ID token.
     *
     * @param forceRefresh Whether to force a token refresh if available.
     * @return The ID token string, or null if unauthenticated.
     */
    public suspend fun getIdToken(forceRefresh: Boolean = false): String?
}

/**
 * Platform factory resolving the target-specific [AuthTokenProvider].
 */
public expect fun createPlatformAuthTokenProvider(): AuthTokenProvider

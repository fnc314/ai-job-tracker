package dev.fnc314.aijobtracker.frontend.core.data.platform

/**
 * Multiplatform provider for retrieving the device's push notification token or installation identifier.
 */
public interface PlatformPushTokenProvider {
    /**
     * Resolves the push notification token or installation ID for this client instance.
     *
     * @return Unique string identifier if available, or null on unsupported/unregistered platforms.
     */
    public suspend fun getPushToken(): String?
}

/**
 * Platform factory resolving the native [PlatformPushTokenProvider].
 */
public expect fun createPlatformPushTokenProvider(): PlatformPushTokenProvider

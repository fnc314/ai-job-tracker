package dev.fnc314.aijobtracker.frontend.core.data.model

import kotlinx.serialization.Serializable

/**
 * Multiplatform mirroring of tracker.DeviceRegistrationStatus protobuf enum.
 */
@Serializable
public enum class DeviceRegistrationStatus {
    DEVICE_REGISTRATION_STATUS_UNSPECIFIED,
    DEVICE_REGISTRATION_STATUS_ACTIVE,
    DEVICE_REGISTRATION_STATUS_DEREGISTERED,
    DEVICE_REGISTRATION_STATUS_BANNED,
    DEVICE_REGISTRATION_STATUS_BLOCKED;

    public companion object {
        public fun fromString(value: String?): DeviceRegistrationStatus {
            return when (value?.uppercase()) {
                "ACTIVE", "DEVICE_REGISTRATION_STATUS_ACTIVE" -> DEVICE_REGISTRATION_STATUS_ACTIVE
                "DEREGISTERED", "DEVICE_REGISTRATION_STATUS_DEREGISTERED" -> DEVICE_REGISTRATION_STATUS_DEREGISTERED
                "BANNED", "DEVICE_REGISTRATION_STATUS_BANNED" -> DEVICE_REGISTRATION_STATUS_BANNED
                "BLOCKED", "DEVICE_REGISTRATION_STATUS_BLOCKED" -> DEVICE_REGISTRATION_STATUS_BLOCKED
                else -> DEVICE_REGISTRATION_STATUS_UNSPECIFIED
            }
        }
    }
}

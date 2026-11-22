package dev.fnc314.aijobtracker.frontend.features.settings.domain.usecase

/**
 * Functional interface usecase invoking device registration for push notifications.
 */
public fun interface RegisterDeviceNotificationsUseCase {
    /**
     * Registers or updates the current host device to receive push notifications.
     *
     * @return True if registration succeeded on backend or was handled cleanly.
     */
    public suspend operator fun invoke(): Boolean
}

package dev.fnc314.aijobtracker.frontend.features.signin.data.source

import dev.fnc314.aijobtracker.frontend.core.auth.model.UserAccount

internal interface LocalAuthDataSource {
    suspend fun getCachedUser(): UserAccount?
    suspend fun saveCachedUser(user: UserAccount?)
}

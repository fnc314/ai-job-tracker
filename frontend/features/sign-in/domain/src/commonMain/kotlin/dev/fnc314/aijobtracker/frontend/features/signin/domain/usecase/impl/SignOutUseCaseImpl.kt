package dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.impl

import dev.fnc314.aijobtracker.frontend.features.signin.domain.repository.AuthRepository
import dev.fnc314.aijobtracker.frontend.features.signin.domain.usecase.SignOutUseCase

internal class SignOutUseCaseImpl(
    private val authRepository: AuthRepository,
) : SignOutUseCase {
    override suspend fun invoke(): Result<Unit> = authRepository.signOut()
}

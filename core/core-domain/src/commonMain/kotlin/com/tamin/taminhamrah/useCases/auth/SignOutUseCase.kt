package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(token: String): Flow<String> {
        return authRepository.signOut(token)
    }
}

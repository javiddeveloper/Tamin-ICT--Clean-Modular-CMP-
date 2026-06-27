package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository

class CheckUserIsNewUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nationalId: String): Boolean {
        return userRepository.checkUserIsNew(nationalId)
    }
}

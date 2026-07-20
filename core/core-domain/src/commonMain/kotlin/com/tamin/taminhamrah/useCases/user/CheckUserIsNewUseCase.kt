package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class CheckUserIsNewUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(nationalId: String): Flow<Boolean> {
        return userRepository.checkUserIsNew(nationalId)
    }
}

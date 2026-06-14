package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SendImageRequestUseCase(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(branchCode: String, serialId: String): Flow<String> {
        return userRepository.sendImageRequest(branchCode, serialId)
    }
}

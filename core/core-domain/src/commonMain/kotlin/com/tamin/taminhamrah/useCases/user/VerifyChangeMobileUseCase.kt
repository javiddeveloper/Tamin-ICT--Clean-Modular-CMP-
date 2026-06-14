package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class VerifyChangeMobileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(mobile: String, otp: String, otpHashCode: String): Flow<String> {
        return userRepository.verifyChangeMobileCode(mobile, otp, otpHashCode)
    }
}

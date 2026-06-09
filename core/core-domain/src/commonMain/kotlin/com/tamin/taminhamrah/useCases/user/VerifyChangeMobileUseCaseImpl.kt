package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

internal class VerifyChangeMobileUseCaseImpl(
    private val userRepository: UserRepository
) : VerifyChangeMobileUseCase {
    override suspend fun invoke(mobile: String, otp: String, otpHashCode: String): Flow<String> {
        return userRepository.verifyChangeMobileCode(mobile, otp, otpHashCode)
    }
}

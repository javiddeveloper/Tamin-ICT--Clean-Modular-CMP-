package com.tamin.taminhamrah.useCases.user

import kotlinx.coroutines.flow.Flow

interface VerifyChangeMobileUseCase {
    suspend operator fun invoke(mobile: String, otp: String, otpHashCode: String): Flow<String>
}

package com.tamin.taminhamrah.useCases.user

import kotlinx.coroutines.flow.Flow

interface UserProfileImageUseCase {
    suspend operator fun invoke(): Flow<String>
}

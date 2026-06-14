package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserProfileImageUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Flow<String> {
        return userRepository.getUserProfileImage()
    }
}

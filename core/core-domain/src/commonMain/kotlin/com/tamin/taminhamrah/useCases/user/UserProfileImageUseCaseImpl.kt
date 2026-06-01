package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserProfileImageUseCaseImpl(
    private val userRepository: UserRepository,
) : UserProfileImageUseCase {
    override suspend fun invoke(): Flow<String> {
        return userRepository.getUserProfileImage()
    }
}

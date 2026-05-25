package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.repository.UserRepository

class UserProfileImageUseCaseImpl(
    private val userRepository: UserRepository,
) : UserProfileImageUseCase {
    override suspend fun invoke(): String {
        return userRepository.getUserProfileImage()
    }
}

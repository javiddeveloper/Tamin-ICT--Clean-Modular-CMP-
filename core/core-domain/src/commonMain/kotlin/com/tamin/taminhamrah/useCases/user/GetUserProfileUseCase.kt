package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Flow<UserProfileDN> =
        repository.getUserProfile()
}

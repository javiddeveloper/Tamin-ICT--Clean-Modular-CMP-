package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class ChangeMobileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(mobileNumber: String): Flow<EditMobileResponseDN> {
        return userRepository.changeMobile(mobileNumber)
    }
}

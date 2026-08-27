package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetIdentityInfoUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<IdentityInfoDN> = userRepository.getIdentityInfo()
}

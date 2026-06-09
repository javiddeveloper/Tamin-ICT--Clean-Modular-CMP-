package com.tamin.taminhamrah.useCases.user


import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

internal class ChangeMobileUseCaseImpl(
    private val userRepository: UserRepository
) : ChangeMobileUseCase {
    override suspend fun invoke(filter: List<ApiFilterDN>): Flow<EditMobileResponseDN> {
        return userRepository.changeMobile(filter)
    }
}

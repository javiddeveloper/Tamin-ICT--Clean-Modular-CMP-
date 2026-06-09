package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SendImageRequestUseCase(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(branchCode: String, filter: List<ApiFilterDN>): Flow<String> {
        return userRepository.sendImageRequest(branchCode, filter)
    }
}

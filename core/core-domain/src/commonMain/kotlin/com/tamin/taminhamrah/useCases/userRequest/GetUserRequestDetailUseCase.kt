package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class GetUserRequestDetailUseCase(
    private val repository: UserRequestRepository
) {
    suspend operator fun invoke(id: Long): UserRequestDN {
        return repository.getUserRequestDetail(id)
    }
}

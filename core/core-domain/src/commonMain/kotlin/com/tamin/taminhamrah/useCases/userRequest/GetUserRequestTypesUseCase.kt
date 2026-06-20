package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.request.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class GetUserRequestTypesUseCase(
    private val repository: UserRequestRepository
) {
    suspend operator fun invoke(
        page: Int = 0,
        start: Int = 0,
        limit: Int = 100,
    ): List<UserRequestTypeDN> {
        return repository.getRequestTypes(page, start, limit)
    }
}

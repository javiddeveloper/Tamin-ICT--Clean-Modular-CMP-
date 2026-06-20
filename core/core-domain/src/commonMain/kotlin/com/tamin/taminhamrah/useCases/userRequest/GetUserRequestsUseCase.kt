package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import kotlinx.coroutines.flow.Flow

class GetUserRequestsUseCase(
    private val repository: UserRequestRepository
) {
    operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<UserRequestDN>> {
        return repository.getUserRequests(filters)
    }
}

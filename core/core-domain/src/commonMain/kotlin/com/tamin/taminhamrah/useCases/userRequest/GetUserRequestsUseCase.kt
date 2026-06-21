package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import kotlinx.coroutines.flow.Flow

class GetUserRequestsUseCase(
    private val repository: UserRequestRepository
) {
    operator fun invoke(
        search: UserRequestSearchParams = UserRequestSearchParams()
    ): Flow<List<UserRequestDN>> {
        return repository.getUserRequests(search)
    }
}

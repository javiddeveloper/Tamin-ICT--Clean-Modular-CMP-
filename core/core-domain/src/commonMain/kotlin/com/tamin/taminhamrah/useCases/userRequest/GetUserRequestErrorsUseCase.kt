package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class GetUserRequestErrorsUseCase(
    private val repository: UserRequestRepository,
) {
    suspend operator fun invoke(requestId: Long): List<RequestErrorDN> {
        return repository.getRequestErrors(requestId)
    }
}

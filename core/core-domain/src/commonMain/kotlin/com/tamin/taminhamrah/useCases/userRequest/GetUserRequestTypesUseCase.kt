package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class GetUserRequestTypesUseCase(
    private val repository: UserRequestRepository,
) {
    suspend operator fun invoke(query: ApiQueryParamDN? = null): List<UserRequestTypeDN> {
        return repository.getRequestTypes(query)
    }
}

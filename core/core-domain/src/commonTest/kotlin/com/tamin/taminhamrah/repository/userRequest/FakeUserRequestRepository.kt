package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRequestRepository : UserRequestRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var userRequestsResult: List<UserRequestDN> = emptyList()
    var requestTypesResult: List<UserRequestTypeDN> = emptyList()
    var lastSearch: UserRequestSearchParams? = null
    var lastTypesQuery: ApiQueryParamDN? = null

    override fun getUserRequests(search: UserRequestSearchParams): Flow<List<UserRequestDN>> = flow {
        lastSearch = search
        if (shouldThrowError) throw error
        emit(userRequestsResult)
    }

    override suspend fun getRequestTypes(query: ApiQueryParamDN?): List<UserRequestTypeDN> {
        lastTypesQuery = query
        if (shouldThrowError) throw error
        return requestTypesResult
    }
}

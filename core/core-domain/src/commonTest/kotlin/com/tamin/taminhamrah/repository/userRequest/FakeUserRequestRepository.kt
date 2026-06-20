package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.UserRequestDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRequestRepository : UserRequestRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var userRequestsResult: List<UserRequestDN> = emptyList()
    var lastFilters: List<ApiFilterDN>? = null

    override fun getUserRequests(filters: List<ApiFilterDN>): Flow<List<UserRequestDN>> = flow {
        lastFilters = filters
        if (shouldThrowError) throw error
        emit(userRequestsResult)
    }
}

package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.UserRequestDN
import kotlinx.coroutines.flow.Flow

interface UserRequestRepository {
    fun getUserRequests(filters: List<ApiFilterDN> = emptyList()): Flow<List<UserRequestDN>>
}

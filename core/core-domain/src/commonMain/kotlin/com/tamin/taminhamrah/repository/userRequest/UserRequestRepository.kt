package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.model.request.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import kotlinx.coroutines.flow.Flow

interface UserRequestRepository {
    fun getUserRequests(search: UserRequestSearchParams = UserRequestSearchParams()): Flow<List<UserRequestDN>>

    suspend fun getRequestTypes(
        page: Int = 0,
        start: Int = 0,
        limit: Int = 100,
    ): List<UserRequestTypeDN>
}

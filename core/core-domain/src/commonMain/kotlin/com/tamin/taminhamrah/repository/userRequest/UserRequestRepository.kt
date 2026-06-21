package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface UserRequestRepository {
    fun getUserRequests(search: UserRequestSearchParams = UserRequestSearchParams()): Flow<List<UserRequestDN>>

    suspend fun getRequestTypes(query: ApiQueryParamDN? = null): List<UserRequestTypeDN>
}

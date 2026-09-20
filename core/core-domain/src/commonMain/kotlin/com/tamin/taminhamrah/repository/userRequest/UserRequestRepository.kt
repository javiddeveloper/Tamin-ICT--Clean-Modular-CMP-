package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface UserRequestRepository {
    fun getUserRequests(search: UserRequestSearchParams = UserRequestSearchParams()): Flow<List<UserRequestDN>>

    /**
     * One-shot network refresh, for callers that need the fresh value directly rather than
     * observing [getUserRequests]'s cache-then-network `Flow`. Still writes through to the local
     * cache, so [getUserRequests] observers see the update too.
     */
    suspend fun refreshUserRequests(search: UserRequestSearchParams = UserRequestSearchParams()): List<UserRequestDN>

    suspend fun getRequestTypes(query: ApiQueryParamDN? = null): List<UserRequestTypeDN>

    suspend fun getRequestErrors(requestId: Long): List<RequestErrorDN>

    suspend fun getSmartGuideList(params: SmartGuideSearchParams): List<SmartGuideDN>

    suspend fun getUserRequestDetail(id: Long): UserRequestDN

    suspend fun getShowRequestInfo(referenceId: String, requestTypeId: Long): UserRequestDetailsDN?

    suspend fun downloadUserRequestDocument(guid: String): String
}


package com.tamin.taminhamrah.feature.userRequest.ui.screens

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRequestRepository : UserRequestRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var userRequestDetailResult: UserRequestDN? = null
    var showRequestInfoResult: UserRequestDetailsDN? = null
    var lastReferenceId: String? = null
    var lastRequestId: Long? = null
    var downloadResult: String = ""
    var lastDownloadedGuid: String? = null
    var userRequestsResult: List<UserRequestDN> = emptyList()
    var requestErrorsResult: List<RequestErrorDN> = emptyList()
    var lastErrorsRequestId: Long? = null
    var lastSearch: UserRequestSearchParams? = null
    var keepRequestsFlowOpen = false

    override fun getUserRequests(search: UserRequestSearchParams): Flow<List<UserRequestDN>> = flow {
        lastSearch = search
        if (shouldThrowError) throw error
        emit(userRequestsResult)
        // The real repository keeps observing the Room cache after its first emissions.
        if (keepRequestsFlowOpen) awaitCancellation()
    }

    override suspend fun refreshUserRequests(search: UserRequestSearchParams): List<UserRequestDN> {
        if (shouldThrowError) throw error
        return userRequestsResult
    }

    override suspend fun getRequestTypes(query: ApiQueryParamDN?): List<UserRequestTypeDN> {
        if (shouldThrowError) throw error
        return emptyList()
    }

    override suspend fun getRequestErrors(requestId: Long): List<RequestErrorDN> {
        lastErrorsRequestId = requestId
        if (shouldThrowError) throw error
        return requestErrorsResult
    }

    override suspend fun getSmartGuideList(params: SmartGuideSearchParams): List<SmartGuideDN> {
        if (shouldThrowError) throw error
        return emptyList()
    }

    override suspend fun getUserRequestDetail(id: Long): UserRequestDN {
        lastRequestId = id
        if (shouldThrowError) throw error
        return userRequestDetailResult ?: error("detail not set")
    }

    override suspend fun getShowRequestInfo(
        referenceId: String,
        requestTypeId: Long,
    ): UserRequestDetailsDN? {
        lastReferenceId = referenceId
        if (shouldThrowError) throw error
        return showRequestInfoResult
    }

    override suspend fun downloadUserRequestDocument(guid: String): String {
        lastDownloadedGuid = guid
        if (shouldThrowError) throw error
        return downloadResult
    }
}

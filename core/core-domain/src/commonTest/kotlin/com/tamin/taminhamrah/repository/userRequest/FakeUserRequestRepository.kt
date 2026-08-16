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
import kotlinx.coroutines.flow.flow

class FakeUserRequestRepository : UserRequestRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var userRequestsResult: List<UserRequestDN> = emptyList()
    var requestTypesResult: List<UserRequestTypeDN> = emptyList()
    var requestErrorsResult: List<RequestErrorDN> = emptyList()
    var smartGuideResult: List<SmartGuideDN> = emptyList()
    var lastSearch: UserRequestSearchParams? = null
    var lastTypesQuery: ApiQueryParamDN? = null
    var lastRequestId: Long? = null
    var lastSmartGuideParams: SmartGuideSearchParams? = null

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

    override suspend fun getRequestErrors(requestId: Long): List<RequestErrorDN> {
        lastRequestId = requestId
        if (shouldThrowError) throw error
        return requestErrorsResult
    }

    override suspend fun getSmartGuideList(params: SmartGuideSearchParams): List<SmartGuideDN> {
        lastSmartGuideParams = params
        if (shouldThrowError) throw error
        return smartGuideResult
    }

    var userRequestDetailResult: UserRequestDN? = null
    var showRequestInfoResult: UserRequestDetailsDN? = null
    var downloadedDocument: String = ""
    var lastReferenceId: String? = null
    var lastRequestTypeId: Long? = null
    var lastDocumentGuid: String? = null

    override suspend fun getUserRequestDetail(id: Long): UserRequestDN {
        lastRequestId = id
        if (shouldThrowError) throw error
        return userRequestDetailResult ?: throw RuntimeException("No user request detail set in fake repository")
    }

    override suspend fun getShowRequestInfo(
        referenceId: String,
        requestTypeId: Long,
    ): UserRequestDetailsDN? {
        lastReferenceId = referenceId
        lastRequestTypeId = requestTypeId
        if (shouldThrowError) throw error
        return showRequestInfoResult
    }

    override suspend fun downloadUserRequestDocument(guid: String): String {
        lastDocumentGuid = guid
        if (shouldThrowError) throw error
        return downloadedDocument
    }
}


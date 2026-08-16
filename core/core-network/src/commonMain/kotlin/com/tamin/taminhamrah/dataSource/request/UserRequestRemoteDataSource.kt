package com.tamin.taminhamrah.dataSource.request

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.Article16RequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentInfoDTO
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionHistoryDTO
import com.tamin.taminhamrah.model.userRequest.PregnancyLookupDTO
import com.tamin.taminhamrah.model.userRequest.RequestErrorDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestStatusDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDTO
import com.tamin.taminhamrah.model.utils.ListData

interface UserRequestRemoteDataSource {
    suspend fun getUserRequests(query: ApiQueryParamDN): ListData<UserRequestDTO>

    suspend fun getRequestTypes(query: ApiQueryParamDN): ListData<UserRequestTypeDTO>

    suspend fun getRequestErrors(query: ApiQueryParamDN): ListData<RequestErrorDTO>

    /**
     * The faq/limitation endpoint uses flat query params (requestType, requestStatus, isPublic)
     * NOT the standard filter JSON array used by other endpoints.
     */
    suspend fun getSmartGuideList(params: Map<String, String>): ListData<SmartGuideDTO>

    suspend fun getUserRequestDetail(id: Long): UserRequestDTO

    suspend fun getShortTermRequestStatus(referenceId: String): ListData<ShortTermRequestStatusDTO>

    suspend fun getShortTermRequestLoadData(referenceId: String): ListData<ShortTermRequestInfoDTO>

    suspend fun getPregnancyStatus(): ListData<PregnancyLookupDTO>

    suspend fun getPregnancyTypes(): ListData<PregnancyLookupDTO>

    suspend fun getArticle16RequestInfo(objectionNumber: Long): Article16RequestInfoDTO

    suspend fun getDeferredInstallmentInfo(requestId: String): DeferredInstallmentInfoDTO

    suspend fun getFollowUpObjectionHistory(referenceId: String): ListData<FollowUpObjectionHistoryDTO>

    suspend fun downloadDocument(guid: String): String
}


package com.tamin.taminhamrah.dataSource.request

import com.tamin.taminhamrah.apiService.userRequest.UserRequestApiService
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
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class UserRequestRemoteDataSourceImpl(
    private val requestApiService: UserRequestApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : UserRequestRemoteDataSource {

    override suspend fun getUserRequests(query: ApiQueryParamDN): ListData<UserRequestDTO> {
        return fetchListData { requestApiService.getUserRequests(apiQueryBuilder.buildQuery(query)) }
    }

    override suspend fun getRequestTypes(query: ApiQueryParamDN): ListData<UserRequestTypeDTO> {
        return fetchListData { requestApiService.getRequestTypes(apiQueryBuilder.buildQuery(query)) }
    }

    override suspend fun getRequestErrors(query: ApiQueryParamDN): ListData<RequestErrorDTO> {
        return fetchListData { requestApiService.getMyRequestErrorList(apiQueryBuilder.buildQuery(query)) }
    }

    override suspend fun getSmartGuideList(params: Map<String, String>): ListData<SmartGuideDTO> {
        return fetchListData { requestApiService.getSmartGuideList(params) }
    }

    override suspend fun getUserRequestDetail(id: Long): UserRequestDTO {
        return fetchData { requestApiService.getUserRequestDetail(id) }
    }

    override suspend fun getShortTermRequestStatus(referenceId: String): ListData<ShortTermRequestStatusDTO> {
        return fetchListData { requestApiService.getShortTermRequestStatus(referenceId) }
    }

    override suspend fun getShortTermRequestLoadData(referenceId: String): ListData<ShortTermRequestInfoDTO> {
        return fetchListData { requestApiService.getShortTermRequestLoadData(referenceId) }
    }

    override suspend fun getPregnancyStatus(): ListData<PregnancyLookupDTO> {
        return fetchListData { requestApiService.getPregnancyStatus() }
    }

    override suspend fun getPregnancyTypes(): ListData<PregnancyLookupDTO> {
        return fetchListData { requestApiService.getPregnancyTypes() }
    }

    override suspend fun getArticle16RequestInfo(objectionNumber: Long): Article16RequestInfoDTO {
        return fetchData { requestApiService.getArticle16RequestInfo(objectionNumber) }
    }

    override suspend fun getDeferredInstallmentInfo(requestId: String): DeferredInstallmentInfoDTO {
        return fetchData { requestApiService.getDeferredInstallmentInfo(requestId) }
    }

    override suspend fun getFollowUpObjectionHistory(
        referenceId: String
    ): ListData<FollowUpObjectionHistoryDTO> {
        return fetchListData { requestApiService.getFollowUpObjectionHistory(referenceId) }
    }

    override suspend fun downloadDocument(guid: String): String {
        return fetchData { requestApiService.downloadDocument(guid) }
    }

    private suspend fun <T> fetchListData(
        call: suspend () -> BaseDTO<ListData<T>>
    ): ListData<T> {
        return fetchData(call)
    }

    private suspend fun <T> fetchData(call: suspend () -> BaseDTO<T>): T {
        return try {
            call().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}

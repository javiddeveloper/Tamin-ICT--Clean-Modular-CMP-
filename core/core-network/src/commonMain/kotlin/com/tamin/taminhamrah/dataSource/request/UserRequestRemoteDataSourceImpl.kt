package com.tamin.taminhamrah.dataSource.request

import com.tamin.taminhamrah.apiService.userRequest.UserRequestApiService
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorDTO
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

    override suspend fun getSmartGuideList(query: ApiQueryParamDN): ListData<SmartGuideDTO> {
        return fetchListData { requestApiService.getSmartGuideList(apiQueryBuilder.buildQuery(query)) }
    }

    private suspend fun <T> fetchListData(
        call: suspend () -> BaseDTO<ListData<T>>
    ): ListData<T> {
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

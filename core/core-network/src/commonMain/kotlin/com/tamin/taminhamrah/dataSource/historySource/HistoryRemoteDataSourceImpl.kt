package com.tamin.taminhamrah.dataSource.historySource

import com.tamin.taminhamrah.apiService.HistoryApiServices
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage

internal class HistoryRemoteDataSourceImpl(
    private val apiServices: HistoryApiServices,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : HistoryRemoteDataSource {

    override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO {
        return try {
            val response = apiServices.getTalfighInfos(queryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO {
        return try {
            val response = apiServices.getDastmozdInfos(queryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getUserInfos(): UserInfoDTO {
        return try {
            val response = apiServices.getUserInfos()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendToInstitution(type1: Boolean, type2: Boolean, type3: Boolean) {
        try {
            val response = apiServices.sendToInstitution(type1, type2, type3)
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}


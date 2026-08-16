package com.tamin.taminhamrah.dataSource.orotezProtez

import com.tamin.taminhamrah.apiService.orotezProtez.OrotezProtezApiService
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class OrotezProtezRemoteDataSourceImpl(
    private val orotezProtezApiService: OrotezProtezApiService,
    private val errorParser: ErrorParser,
) : OrotezProtezRemoteDataSource {

    override suspend fun getRequestInsuredMainInfo(): RequestInsuredMainInfoDTO? {
        return try {
            val response = orotezProtezApiService.getRequestInsuredMainInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}

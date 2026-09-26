package com.tamin.taminhamrah.dataSource.orotezProtez

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.orotezProtez.OrotezProtezApiService
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonListDTO
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDTO
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisResponseDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData

class OrotezProtezRemoteDataSourceImpl(
    private val orotezProtezApiService: OrotezProtezApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : OrotezProtezRemoteDataSource {

    override suspend fun getRequestInsuredMainInfo(): RequestInsuredMainInfoDTO {
        return errorParser.safeCall("getRequestInsuredMainInfo") {
            val response = orotezProtezApiService.getRequestInsuredMainInfo()
            response.extractData()
        }
    }

    override suspend fun getInsuredPersons(query: ApiQueryParamDN): InsuredPersonListDTO {
        return errorParser.safeCall("getInsuredPersons") {
            val response = orotezProtezApiService.getInsuredPersons(apiQueryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun saveShortTermOrthosis(
        request: SaveShortTermOrthosisRequestDTO
    ): SaveShortTermOrthosisResponseDTO {
        return errorParser.safeCall("saveShortTermOrthosis") {
            val response = orotezProtezApiService.saveShortTermOrthosis(request)
            response.extractData()
        }
    }
}

package com.tamin.taminhamrah.dataSource.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonListDTO
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDTO
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisResponseDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface OrotezProtezRemoteDataSource {
    suspend fun getRequestInsuredMainInfo(): RequestInsuredMainInfoDTO?
    suspend fun getInsuredPersons(query: ApiQueryParamDN): InsuredPersonListDTO?
    suspend fun saveShortTermOrthosis(request: SaveShortTermOrthosisRequestDTO): SaveShortTermOrthosisResponseDTO?
}

package com.tamin.taminhamrah.apiService.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonListDTO
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface OrotezProtezApiService {

    @GET("shortterm-request/getRequestInsuredMainInfo/Orthosis")
    suspend fun getRequestInsuredMainInfo(): BaseDTO<RequestInsuredMainInfoDTO>

    @GET("shortterm-request/ArutzView")
    suspend fun getInsuredPersons(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<InsuredPersonListDTO>
}

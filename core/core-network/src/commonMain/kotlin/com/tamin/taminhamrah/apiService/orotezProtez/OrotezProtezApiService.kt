package com.tamin.taminhamrah.apiService.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET

interface OrotezProtezApiService {

    @GET("shortterm-request/getRequestInsuredMainInfo/Orthosis")
    suspend fun getRequestInsuredMainInfo(): BaseDTO<RequestInsuredMainInfoDTO>
}

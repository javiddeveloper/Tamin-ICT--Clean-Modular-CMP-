package com.tamin.taminhamrah.apiService.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessResponseDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface RequestPaymentForIllDaysApiService {

    @GET("shortterm-request/getRequestInsuredMainInfo")
    suspend fun getLatestInsuranceInfo(): BaseDTO<IllDaysInsuredMainInfoDTO>

    @GET("shortterm-request/getCovidResult")
    suspend fun getCovidResult(): BaseDTO<CovidResultListDTO>

    @POST("stp/saveShorttremIllness")
    suspend fun sendRequestForIllDay(
        @Body request: SaveShortTermIllnessRequestDTO
    ): BaseDTO<SaveShortTermIllnessResponseDTO>
}

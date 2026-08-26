package com.tamin.taminhamrah.apiService.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionListDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayResponseDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface PregnancyPayApiService {

    @GET("shortterm-request/getRequestInsuredMainInfo")
    suspend fun getMainInfo(): BaseDTO<PregnancyMainInfoDTO>

    @GET("shortterm-request/calcPregnancy/{startDateTimeStamp}/{endDateTimeStamp}")
    suspend fun calculateEstimate(
        @Path("startDateTimeStamp") startDateTimeStamp: String,
        @Path("endDateTimeStamp") endDateTimeStamp: String,
    ): BaseDTO<List<String>?>

    @GET("StpBaseinfo/ShorttermBarTypes")
    suspend fun getPregnancyStatusList(): BaseDTO<PregnancyOptionListDTO>

    @GET("StpBaseinfo/ShorttermBarChild")
    suspend fun getPregnancyTypeList(): BaseDTO<PregnancyOptionListDTO>

    @POST("stp/saveShorttremPragnent")
    suspend fun sendPregnancyPayRequest(
        @Body request: SendPregnancyPayRequestDTO
    ): BaseDTO<SendPregnancyPayResponseDTO>
}

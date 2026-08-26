package com.tamin.taminhamrah.dataSource.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionListDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayResponseDTO

interface PregnancyPayRemoteDataSource {
    suspend fun getMainInfo(): PregnancyMainInfoDTO?
    suspend fun getPregnancyStatusList(): PregnancyOptionListDTO?
    suspend fun getPregnancyTypeList(): PregnancyOptionListDTO?
    suspend fun sendPregnancyPayRequest(request: SendPregnancyPayRequestDTO): SendPregnancyPayResponseDTO?
    suspend fun calculateEstimate(startDateTimeStamp: String, endDateTimeStamp: String): List<String?>?
}

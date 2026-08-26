package com.tamin.taminhamrah.dataSource.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessResponseDTO

interface RequestPaymentForIllDaysRemoteDataSource {
    suspend fun getLatestInsuranceInfo(): IllDaysInsuredMainInfoDTO?
    suspend fun getCovidResult(): CovidResultListDTO?
    suspend fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDTO
    ): SaveShortTermIllnessResponseDTO?
}

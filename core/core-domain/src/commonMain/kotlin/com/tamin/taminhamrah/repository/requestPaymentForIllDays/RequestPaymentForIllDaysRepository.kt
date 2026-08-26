package com.tamin.taminhamrah.repository.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import kotlinx.coroutines.flow.Flow

interface RequestPaymentForIllDaysRepository {
    fun getLatestInsuranceInfo(): Flow<IllDaysInsuredMainInfoDN?>
    fun getCovidResult(): Flow<CovidResultDN>
    fun sendRequestForIllDay(request: SaveShortTermIllnessRequestDN): Flow<String?>
}

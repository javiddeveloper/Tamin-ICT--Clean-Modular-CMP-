package com.tamin.taminhamrah.useCases.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.repository.requestPaymentForIllDays.RequestPaymentForIllDaysRepository
import kotlinx.coroutines.flow.Flow

class GetIllDaysInsuredMainInfoUseCase(
    private val repository: RequestPaymentForIllDaysRepository
) {
    operator fun invoke(): Flow<IllDaysInsuredMainInfoDN?> {
        return repository.getLatestInsuranceInfo()
    }
}

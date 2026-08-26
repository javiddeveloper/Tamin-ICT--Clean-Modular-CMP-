package com.tamin.taminhamrah.useCases.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.repository.requestPaymentForIllDays.RequestPaymentForIllDaysRepository
import kotlinx.coroutines.flow.Flow

class GetCovidResultUseCase(
    private val repository: RequestPaymentForIllDaysRepository
) {
    operator fun invoke(): Flow<CovidResultDN> {
        return repository.getCovidResult()
    }
}

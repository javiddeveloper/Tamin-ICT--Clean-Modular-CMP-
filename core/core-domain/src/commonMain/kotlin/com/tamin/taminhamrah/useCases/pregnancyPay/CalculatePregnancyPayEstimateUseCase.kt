package com.tamin.taminhamrah.useCases.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyPayEstimateDN
import com.tamin.taminhamrah.repository.pregnancyPay.PregnancyPayRepository
import kotlinx.coroutines.flow.Flow

class CalculatePregnancyPayEstimateUseCase(
    private val pregnancyPayRepository: PregnancyPayRepository
) {
    operator fun invoke(startDateTimeStamp: Long, endDateTimeStamp: Long): Flow<PregnancyPayEstimateDN> {
        return pregnancyPayRepository.calculateEstimate(startDateTimeStamp, endDateTimeStamp)
    }
}

package com.tamin.taminhamrah.useCases.requestPaymentForIllDays

import com.tamin.taminhamrah.repository.requestPaymentForIllDays.RequestPaymentForIllDaysRepository
import kotlinx.coroutines.flow.Flow

class CalcIllnessAmountUseCase(
    private val repository: RequestPaymentForIllDaysRepository
) {
    operator fun invoke(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
        maritalStatus: String,
    ): Flow<List<String>> {
        return repository.calcIllness(
            startDateTimeStamp = startDateTimeStamp,
            endDateTimeStamp = endDateTimeStamp,
            maritalStatus = maritalStatus,
        )
    }
}

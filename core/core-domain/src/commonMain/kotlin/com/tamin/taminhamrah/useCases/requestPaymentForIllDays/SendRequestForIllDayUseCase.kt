package com.tamin.taminhamrah.useCases.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.repository.requestPaymentForIllDays.RequestPaymentForIllDaysRepository
import kotlinx.coroutines.flow.Flow

class SendRequestForIllDayUseCase(
    private val repository: RequestPaymentForIllDaysRepository
) {
    operator fun invoke(request: SaveShortTermIllnessRequestDN): Flow<String?> {
        return repository.sendRequestForIllDay(request)
    }
}

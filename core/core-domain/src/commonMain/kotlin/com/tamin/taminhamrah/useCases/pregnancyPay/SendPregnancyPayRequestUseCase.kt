package com.tamin.taminhamrah.useCases.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import com.tamin.taminhamrah.repository.pregnancyPay.PregnancyPayRepository
import kotlinx.coroutines.flow.Flow

class SendPregnancyPayRequestUseCase(
    private val pregnancyPayRepository: PregnancyPayRepository
) {
    operator fun invoke(request: SendPregnancyPayRequestDN): Flow<String?> {
        return pregnancyPayRepository.sendPregnancyPayRequest(request)
    }
}

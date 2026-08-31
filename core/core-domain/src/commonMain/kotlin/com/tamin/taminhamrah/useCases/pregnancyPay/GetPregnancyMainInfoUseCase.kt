package com.tamin.taminhamrah.useCases.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.repository.pregnancyPay.PregnancyPayRepository
import kotlinx.coroutines.flow.Flow

class GetPregnancyMainInfoUseCase(
    private val pregnancyPayRepository: PregnancyPayRepository
) {
    operator fun invoke(): Flow<PregnancyMainInfoDN?> {
        return pregnancyPayRepository.getMainInfo()
    }
}

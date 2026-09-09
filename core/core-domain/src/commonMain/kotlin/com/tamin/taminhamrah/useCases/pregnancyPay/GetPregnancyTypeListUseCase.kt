package com.tamin.taminhamrah.useCases.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN
import com.tamin.taminhamrah.repository.pregnancyPay.PregnancyPayRepository
import kotlinx.coroutines.flow.Flow

class GetPregnancyTypeListUseCase(
    private val pregnancyPayRepository: PregnancyPayRepository
) {
    operator fun invoke(): Flow<List<PregnancyOptionDN>> {
        return pregnancyPayRepository.getPregnancyTypeList()
    }
}

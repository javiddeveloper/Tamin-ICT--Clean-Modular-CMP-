package com.tamin.taminhamrah.useCases.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.repository.calculateWagePension.CalculateWagePensionRepository
import kotlinx.coroutines.flow.Flow

class GetMultipleWorkshopPersonalInfoUseCase(
    private val repository: CalculateWagePensionRepository
) {
    suspend operator fun invoke(): Flow<MultipleWorkshopPersonalInfoDN> {
        return repository.getPersonalInfo()
    }
}

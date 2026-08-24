package com.tamin.taminhamrah.useCases.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import com.tamin.taminhamrah.repository.calculateWagePension.CalculateWagePensionRepository
import kotlinx.coroutines.flow.Flow

class CheckMultipleWorkshopsUseCase(
    private val repository: CalculateWagePensionRepository
) {
    suspend operator fun invoke(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN> {
        return repository.isMultipleWorkshops(branchCode, insuranceNumber)
    }
}

package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetPensionerIdUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(): Flow<List<PensionIdDN>> {
        return pensionRepository.getPensionerId()
    }
}

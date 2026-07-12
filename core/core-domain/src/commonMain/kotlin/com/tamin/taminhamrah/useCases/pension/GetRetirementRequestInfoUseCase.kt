package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetRetirementRequestInfoUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<RetirementRequestDN>> {
        return pensionRepository.getRetirementRequestInfo(filters)
    }
}

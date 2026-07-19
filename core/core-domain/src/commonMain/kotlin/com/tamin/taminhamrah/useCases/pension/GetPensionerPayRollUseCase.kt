package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetPensionerPayRollUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> {
        return pensionRepository.getPensionerPayRoll(filters)
    }
}

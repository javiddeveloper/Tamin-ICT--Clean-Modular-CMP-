package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetEdictPensionerUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        query: ApiQueryParamDN
    ): Flow<EdictPensionerDN?> {
        return pensionRepository.getEdictPensioner(query)
    }
}

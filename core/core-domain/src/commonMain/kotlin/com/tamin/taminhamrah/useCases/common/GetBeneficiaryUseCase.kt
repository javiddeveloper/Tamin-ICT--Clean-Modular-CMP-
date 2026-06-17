package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class GetBeneficiaryUseCase(
    private val commonRepository: CommonRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<List<BeneficiaryDN>> {
        return commonRepository.getBeneficiary(query)
    }
}

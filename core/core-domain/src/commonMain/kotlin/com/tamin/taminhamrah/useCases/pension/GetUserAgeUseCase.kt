package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetUserAgeUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN>
    ): Flow<AgeDN> {
        return pensionRepository.getUserAge(filters)
    }
}

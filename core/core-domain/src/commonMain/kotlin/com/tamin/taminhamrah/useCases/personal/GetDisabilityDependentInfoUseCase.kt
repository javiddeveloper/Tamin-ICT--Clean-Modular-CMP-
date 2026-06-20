package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetDisabilityDependentInfoUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> {
        return personalRepository.getDisabilityDependentInfo(filters)
    }
}

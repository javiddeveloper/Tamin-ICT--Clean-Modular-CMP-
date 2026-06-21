package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetConfirmSurvivorsListUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> {
        return personalRepository.getConfirmSurvivorsList(filters)
    }
}

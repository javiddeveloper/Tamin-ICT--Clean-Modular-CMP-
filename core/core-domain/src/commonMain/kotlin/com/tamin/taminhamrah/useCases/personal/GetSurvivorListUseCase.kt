package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetSurvivorListUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(deceasedNationalId: String): Flow<List<SurvivorDependentDN>> =
        personalRepository.getSurvivorList(deceasedNationalId)
}

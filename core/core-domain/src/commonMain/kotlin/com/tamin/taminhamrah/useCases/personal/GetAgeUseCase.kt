package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetAgeUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(birthDate: Long): Flow<AgeDN> {
        return personalRepository.getAge(birthDate)
    }
}

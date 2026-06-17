package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetPersonalInfoUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(): Flow<PersonalInfoDN?> {
        return personalRepository.getPersonalInfo()
    }
}

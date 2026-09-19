package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetInsuredRegistrationDocListUseCase(private val repository: PersonalRepository) {
    operator fun invoke(personalId: String): Flow<List<InsuredDocDN>> =
        repository.getInsuredRegistrationDocList(personalId)
}

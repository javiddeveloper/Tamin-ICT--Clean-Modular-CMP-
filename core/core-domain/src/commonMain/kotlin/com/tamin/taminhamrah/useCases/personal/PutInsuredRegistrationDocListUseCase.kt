package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class PutInsuredRegistrationDocListUseCase(private val repository: PersonalRepository) {
    operator fun invoke(personalId: String, docs: List<InsuredDocDN>): Flow<String?> {
        return repository.putInsuredRegistrationDocList(personalId, docs)
    }
}

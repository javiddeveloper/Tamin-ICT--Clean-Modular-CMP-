package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository

class PutInsuredRegistrationDocListUseCase(private val repository: PersonalRepository) {
    suspend operator fun invoke(personalId: String, docs: List<InsuredDocDN>): String? {
        return repository.putInsuredRegistrationDocList(personalId, docs)
    }
}

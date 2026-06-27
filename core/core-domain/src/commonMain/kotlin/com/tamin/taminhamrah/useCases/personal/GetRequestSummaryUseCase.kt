package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository

class GetRequestSummaryUseCase(private val repository: PersonalRepository) {
    suspend operator fun invoke(requestId: String): NewInsuredSummaryDN? {
        return repository.getRequestSummary(requestId)
    }
}

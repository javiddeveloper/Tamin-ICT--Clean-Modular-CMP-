package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetRequestSummaryUseCase(private val repository: PersonalRepository) {
    operator fun invoke(requestId: String): Flow<NewInsuredSummaryDN?> {
        return repository.getRequestSummary(requestId)
    }
}

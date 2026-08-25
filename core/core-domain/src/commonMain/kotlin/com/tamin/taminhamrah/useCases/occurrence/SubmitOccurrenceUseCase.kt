package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class SubmitOccurrenceUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(request: OccurrenceSubmitRequestDN): OccurrenceResultDN =
        repository.submitOccurrence(request)
}

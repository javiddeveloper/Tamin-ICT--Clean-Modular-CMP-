package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class GetOccurrenceDocTypesUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(): List<OccurrenceDocTypeDN> = repository.getDocumentTypes()
}

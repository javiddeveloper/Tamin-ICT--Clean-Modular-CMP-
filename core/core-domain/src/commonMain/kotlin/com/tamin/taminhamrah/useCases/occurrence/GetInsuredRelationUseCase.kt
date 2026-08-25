package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class GetInsuredRelationUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(nationalCode: String): InsuredRelationDN =
        repository.getInsuredRelation(nationalCode)
}

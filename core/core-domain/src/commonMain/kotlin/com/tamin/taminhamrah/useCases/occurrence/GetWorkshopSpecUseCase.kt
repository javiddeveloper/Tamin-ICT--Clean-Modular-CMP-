package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class GetWorkshopSpecUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(workshopCode: String, branchCode: String): WorkshopItemDN =
        repository.getWorkshopSpec(workshopCode, branchCode)
}

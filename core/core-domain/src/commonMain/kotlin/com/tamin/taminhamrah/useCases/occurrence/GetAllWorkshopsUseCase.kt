package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class GetAllWorkshopsUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(nationalCode: String): List<WorkshopItemDN> = repository.getAllWorkshops(nationalCode)
}

package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.DrugItemDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetAllDrugsUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<DrugItemDN>> =
        repository.getAllDrugs()
}

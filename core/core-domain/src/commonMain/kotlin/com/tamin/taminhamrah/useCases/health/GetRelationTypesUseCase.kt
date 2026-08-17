package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.RelationTypeDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetRelationTypesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<RelationTypeDN>> =
        repository.getRelationTypes()
}

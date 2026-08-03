package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.BloodGroupDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetBloodGroupsUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<BloodGroupDN>> =
        repository.getBloodGroups()
}

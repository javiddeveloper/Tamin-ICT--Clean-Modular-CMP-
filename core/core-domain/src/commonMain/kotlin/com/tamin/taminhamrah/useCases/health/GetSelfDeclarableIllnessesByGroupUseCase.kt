package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessGroupDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetSelfDeclarableIllnessesByGroupUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<SelfDeclarableIllnessGroupDN>> =
        repository.getSelfDeclarableIllnessesByGroup()
}

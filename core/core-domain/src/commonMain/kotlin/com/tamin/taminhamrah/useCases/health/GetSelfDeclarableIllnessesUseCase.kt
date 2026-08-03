package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.IllnessItemDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetSelfDeclarableIllnessesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<IllnessItemDN>> =
        repository.getSelfDeclarableIllnesses()
}

package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class RefreshDependentsUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(): Flow<GeneralResultDN> = repository.refreshDependents()
}

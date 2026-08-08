package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class GetActiveBranchesUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(): Flow<List<BranchDN>> = repository.getActiveBranches()
}

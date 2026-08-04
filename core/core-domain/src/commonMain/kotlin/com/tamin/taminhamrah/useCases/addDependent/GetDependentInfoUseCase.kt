package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.DependentInfoDN
import com.tamin.taminhamrah.repository.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class GetDependentInfoUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(): Flow<List<DependentInfoDN>> = repository.getDependentInfo()
}

package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.repository.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class GetFamilyRelationshipsFromProxyUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(): Flow<List<FamilyRelationshipDN>> = repository.getFamilyRelationshipsFromProxy()
}

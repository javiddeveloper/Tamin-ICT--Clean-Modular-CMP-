package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class GetFamilyRelationshipsFromProxyUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        filter: List<ApiFilterDN> = emptyList()
    ): Flow<List<FamilyRelationshipDN>> = repository.getFamilyRelationshipsFromProxy(filter)
}

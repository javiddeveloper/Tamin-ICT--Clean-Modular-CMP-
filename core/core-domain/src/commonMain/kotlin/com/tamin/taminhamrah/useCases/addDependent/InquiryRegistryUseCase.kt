package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class InquiryRegistryUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): Flow<RegistryDataDN> = repository.inquiryRegistry(dependentNationalId, birthDateTimeStamp, dependencyCode)
}

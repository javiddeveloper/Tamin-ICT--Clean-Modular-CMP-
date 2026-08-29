package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class GetInsuranceTypesUseCase(
    private val repository: CommonRepository
) {
    operator fun invoke(searchText: String? = null): Flow<List<InsuranceTypeDN>> =
        repository.getInsuranceTypes(searchText)
}

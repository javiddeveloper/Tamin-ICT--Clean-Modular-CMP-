package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetDependantUnderEighteenUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        nationalCode: String, filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<DependantUserUnderEighteenDN>> =
        repository.getDependantUnderEighteen(nationalCode, filters)
}

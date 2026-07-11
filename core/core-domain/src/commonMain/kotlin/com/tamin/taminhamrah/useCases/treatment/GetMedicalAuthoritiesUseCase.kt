package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.MedicalAuthoritiesDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetMedicalAuthoritiesUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<MedicalAuthoritiesDN>> =
        repository.getConfirmationMedicalAuthorities(filters)
}

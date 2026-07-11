package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetElectronicPrescriptionPriceUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        noteHeadID: String, nationalCode: String, filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ElectronicPrescriptionPriceDN>> =
        repository.getElectronicPrescriptionPrice(noteHeadID, nationalCode, filters)
}

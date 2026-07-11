package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetElectronicPrescriptionDetailUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        noteHeadID: String, nationalCode: String, childNationalCode: String,
        flagSata: String, type: String, filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ElectronicPrescriptionDetailDN>> =
        repository.getElectronicPrescriptionDetail(noteHeadID, nationalCode, childNationalCode, flagSata, type, filters)
}

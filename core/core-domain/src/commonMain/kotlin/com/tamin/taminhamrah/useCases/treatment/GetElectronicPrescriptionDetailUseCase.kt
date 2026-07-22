package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetElectronicPrescriptionDetailUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        noteHeadID: String, nationalCode: String, patientNationalCode: String,
        flagSata: String, type: String
    ): Flow<List<ElectronicPrescriptionDetailDN>> =
        repository.getElectronicPrescriptionDetail(noteHeadID, nationalCode, patientNationalCode, flagSata, type)
}

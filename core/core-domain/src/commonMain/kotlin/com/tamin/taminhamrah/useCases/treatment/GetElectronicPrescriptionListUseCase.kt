package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetElectronicPrescriptionListUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        requestTypeId: String, nationalCode: String, dependantUserNationalCode: String,
        startDate: String, endDate: String
    ): Flow<List<ElectronicPrescriptionDN>> =
        repository.getElectronicPrescriptionList(requestTypeId, nationalCode, dependantUserNationalCode, startDate, endDate)
}

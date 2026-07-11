package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientDrugAllergiesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String, patientID: Int): Flow<List<DrugItemAllergiesDN>> =
        repository.getPatientDrugAllergies(natCode, patientID)
}

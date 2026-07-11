package com.tamin.taminhamrah.repository.health

import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN>
    suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): Flow<PatientSelfDeclarativeDN>
    suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): Flow<List<DrugItemAllergiesDN>>
}

package com.tamin.taminhamrah.repository.health

import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDN
import com.tamin.taminhamrah.model.health.PatientVisitDN
import com.tamin.taminhamrah.model.health.PatientLabDN
import com.tamin.taminhamrah.model.health.PatientImagingDN
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN>
    suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): Flow<PatientSelfDeclarativeDN>
    suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): Flow<List<DrugItemAllergiesDN>>
    suspend fun getPatientHospitalizations(natCode: String, patientID: Int): Flow<List<PatientHospitalizationsDN>>
    suspend fun getPatientVisits(natCode: String, patientID: Int): Flow<List<PatientVisitDN>>
    suspend fun getPatientLabs(natCode: String, patientID: Int): Flow<List<PatientLabDN>>
    suspend fun getPatientImaging(natCode: String, patientID: Int): Flow<List<PatientImagingDN>>
}

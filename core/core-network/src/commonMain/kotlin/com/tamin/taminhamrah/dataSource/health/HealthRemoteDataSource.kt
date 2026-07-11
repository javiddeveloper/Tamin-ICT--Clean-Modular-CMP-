package com.tamin.taminhamrah.dataSource.health

import com.tamin.taminhamrah.model.health.PatientGeneralDTO
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDTO
import com.tamin.taminhamrah.model.utils.ListData

interface HealthRemoteDataSource {
    suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO?
    suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): PatientSelfDeclarativeDTO?
    suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): ListData<DrugItemAllergiesDTO>?
}

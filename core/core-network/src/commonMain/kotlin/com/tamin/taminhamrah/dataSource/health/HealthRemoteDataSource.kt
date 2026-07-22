package com.tamin.taminhamrah.dataSource.health

import com.tamin.taminhamrah.model.health.PatientGeneralDTO
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDTO
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDTO
import com.tamin.taminhamrah.model.health.PatientVisitDTO
import com.tamin.taminhamrah.model.health.PatientLabDTO
import com.tamin.taminhamrah.model.health.PatientImagingDTO
import com.tamin.taminhamrah.model.utils.ListData

interface HealthRemoteDataSource {
    suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO?
    suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): PatientSelfDeclarativeDTO?
    suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): ListData<DrugItemAllergiesDTO>?
    suspend fun getPatientHospitalizations(natCode: String, patientID: Int): ListData<PatientHospitalizationsDTO>?
    suspend fun getPatientVisits(natCode: String, patientID: Int): ListData<PatientVisitDTO>?
    suspend fun getPatientLabs(natCode: String, patientID: Int): ListData<PatientLabDTO>?
    suspend fun getPatientImaging(natCode: String, patientID: Int): ListData<PatientImagingDTO>?
}

package com.tamin.taminhamrah.dataSource.health

import com.tamin.taminhamrah.model.health.ActFrequencyDTO
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeRequestDTO
import com.tamin.taminhamrah.model.health.AllergicDrugsDTO
import com.tamin.taminhamrah.model.health.BloodGroupDTO
import com.tamin.taminhamrah.model.health.DeclarableIllnessesDTO
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDTO
import com.tamin.taminhamrah.model.health.MaritalStatusDTO
import com.tamin.taminhamrah.model.health.PatientGeneralDTO
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDTO
import com.tamin.taminhamrah.model.health.PatientImagingDTO
import com.tamin.taminhamrah.model.health.PatientLabDTO
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.PatientVisitDTO
import com.tamin.taminhamrah.model.health.ProvinceCitiesDTO
import com.tamin.taminhamrah.model.health.ProvincesDTO
import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessesByGroupDTO
import com.tamin.taminhamrah.model.health.SmokingStatusDTO
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequestDTO
import com.tamin.taminhamrah.model.health.SyncIllnessesSelfDecRequestDTO
import com.tamin.taminhamrah.model.health.UpdatePatientDTO
import com.tamin.taminhamrah.model.health.UpdatePatientRequestDTO
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeRequestDTO
import com.tamin.taminhamrah.model.utils.ListData

interface HealthRemoteDataSource {

    // --- Existing ---
    suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO?
    suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): PatientSelfDeclarativeDTO?
    suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): ListData<DrugItemAllergiesDTO>?
    suspend fun getPatientHospitalizations(natCode: String, patientID: Int): ListData<PatientHospitalizationsDTO>?
    suspend fun getPatientVisits(natCode: String, patientID: Int): ListData<PatientVisitDTO>?
    suspend fun getPatientLabs(natCode: String, patientID: Int): ListData<PatientLabDTO>?
    suspend fun getPatientImaging(natCode: String, patientID: Int): ListData<PatientImagingDTO>?

    // --- Location ---
    suspend fun getAllProvinces(): ProvincesDTO?
    suspend fun getProvinceCities(provinceID: Int): ProvinceCitiesDTO?

    // --- Lookup ---
    suspend fun getBloodGroups(): List<BloodGroupDTO>?
    suspend fun getMaritalStatus(): List<MaritalStatusDTO>?
    suspend fun getSmokingStatus(): List<SmokingStatusDTO>?
    suspend fun getActFrequencies(): List<ActFrequencyDTO>?

    // --- Illnesses ---
    suspend fun getSelfDeclarableIllnesses(): DeclarableIllnessesDTO?
    suspend fun getSelfDeclarableIllnessesByGroup(): SelfDeclarableIllnessesByGroupDTO?

    // --- Drug master list ---
    suspend fun getAllDrugs(): AllergicDrugsDTO?

    // --- Mutations (POST) ---
    suspend fun updatePatient(request: UpdatePatientRequestDTO): UpdatePatientDTO?
    suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequestDTO): AddSelfDeclarativeDTO?
    suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequestDTO): UpdateSelfDeclarativeDTO?
    /** Returns the server's success message; see HealthApiService for why this is a String. */
    suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessesSelfDecRequestDTO): String?
    suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequestDTO): String?
}


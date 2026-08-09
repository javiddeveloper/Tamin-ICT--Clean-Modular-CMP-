package com.tamin.taminhamrah.repository.health

import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.HealthMutationResult
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeRequest
import com.tamin.taminhamrah.model.health.BloodGroupDN
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.DrugItemDN
import com.tamin.taminhamrah.model.health.IllnessItemDN
import com.tamin.taminhamrah.model.health.MaritalStatusDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDN
import com.tamin.taminhamrah.model.health.PatientImagingDN
import com.tamin.taminhamrah.model.health.PatientLabDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.PatientVisitDN
import com.tamin.taminhamrah.model.health.ProvinceItemDN
import com.tamin.taminhamrah.model.health.ProvinceCityItemDN
import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessGroupDN
import com.tamin.taminhamrah.model.health.SmokingStatusDN
import com.tamin.taminhamrah.model.health.ActFrequencyDN
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequest
import com.tamin.taminhamrah.model.health.SyncIllnessSelfDeclarativesRequest
import com.tamin.taminhamrah.model.health.SyncResultDN
import com.tamin.taminhamrah.model.health.UpdatePatientDN
import com.tamin.taminhamrah.model.health.UpdatePatientRequest
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeRequest
import kotlinx.coroutines.flow.Flow

interface HealthRepository {

    // --- Existing GET endpoints ---
    suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN>
    suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): Flow<PatientSelfDeclarativeDN>
    suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): Flow<List<DrugItemAllergiesDN>>
    suspend fun getPatientHospitalizations(natCode: String, patientID: Int): Flow<List<PatientHospitalizationsDN>>
    suspend fun getPatientVisits(natCode: String, patientID: Int): Flow<List<PatientVisitDN>>
    suspend fun getPatientLabs(natCode: String, patientID: Int): Flow<List<PatientLabDN>>
    suspend fun getPatientImaging(natCode: String, patientID: Int): Flow<List<PatientImagingDN>>

    // --- Location ---
    suspend fun getAllProvinces(): Flow<List<ProvinceItemDN>>
    suspend fun getProvinceCities(provinceID: Int): Flow<List<ProvinceCityItemDN>>

    // --- Lookup ---
    suspend fun getBloodGroups(): Flow<List<BloodGroupDN>>
    suspend fun getMaritalStatus(): Flow<List<MaritalStatusDN>>
    suspend fun getSmokingStatus(): Flow<List<SmokingStatusDN>>
    suspend fun getActFrequencies(): Flow<List<ActFrequencyDN>>

    // --- Illnesses ---
    suspend fun getSelfDeclarableIllnesses(): Flow<List<IllnessItemDN>>
    suspend fun getSelfDeclarableIllnessesByGroup(): Flow<List<SelfDeclarableIllnessGroupDN>>

    // --- Drug Allergies ---
    suspend fun getAllDrugs(): Flow<List<DrugItemDN>>

    // --- Mutations (POST) ---
    // Return HealthMutationResult so business-level `problems` from the backend
    // (invalid record id, duplicate declaration, etc.) reach the caller instead
    // of only surfacing as a thrown exception - see HealthMutationResult docs.
    suspend fun updatePatient(request: UpdatePatientRequest): HealthMutationResult<UpdatePatientDN>
    suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequest): HealthMutationResult<AddSelfDeclarativeDN>
    suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequest): HealthMutationResult<UpdateSelfDeclarativeDN>
    suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessSelfDeclarativesRequest): HealthMutationResult<SyncResultDN>
    suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequest): HealthMutationResult<SyncResultDN>
}


package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDN
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
import com.tamin.taminhamrah.model.health.ProvinceCityItemDN
import com.tamin.taminhamrah.model.health.ProvinceItemDN
import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessGroupDN
import com.tamin.taminhamrah.model.health.SmokingStatusDN
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequest
import com.tamin.taminhamrah.model.health.SyncIllnessSelfDeclarativesRequest
import com.tamin.taminhamrah.model.health.SyncResultDN
import com.tamin.taminhamrah.model.health.UpdatePatientDN
import com.tamin.taminhamrah.model.health.UpdatePatientRequest
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeRequest
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHealthRepository : HealthRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Health Repository Error")

    // --- Existing results ---
    var getPatientGeneralResult: PatientGeneralDN = PatientGeneralDN(0, "", "", "", "", "", "", null, null, null)
    var getPatientSelfDeclarativeResult: PatientSelfDeclarativeDN = PatientSelfDeclarativeDN(null, null, null, null, null, null, null, 0, null, null, null, null, null, null)
    var getPatientDrugAllergiesResult: List<DrugItemAllergiesDN> = emptyList()
    var getPatientHospitalizationsResult: List<PatientHospitalizationsDN> = emptyList()
    var getPatientVisitsResult: List<PatientVisitDN> = emptyList()
    var getPatientLabsResult: List<PatientLabDN> = emptyList()
    var getPatientImagingResult: List<PatientImagingDN> = emptyList()

    // --- New results ---
    var getAllProvincesResult: List<ProvinceItemDN> = emptyList()
    var getProvinceCitiesResult: List<ProvinceCityItemDN> = emptyList()
    var getBloodGroupsResult: List<BloodGroupDN> = emptyList()
    var getMaritalStatusResult: List<MaritalStatusDN> = emptyList()
    var getSmokingStatusResult: List<SmokingStatusDN> = emptyList()
    var getSelfDeclarableIllnessesResult: List<IllnessItemDN> = emptyList()
    var getSelfDeclarableIllnessesByGroupResult: List<SelfDeclarableIllnessGroupDN> = emptyList()
    var getAllDrugsResult: List<DrugItemDN> = emptyList()
    var updatePatientResult: UpdatePatientDN = UpdatePatientDN(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    var addSelfDeclarativeResult: AddSelfDeclarativeDN = AddSelfDeclarativeDN(null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    var updateSelfDeclarativeResult: UpdateSelfDeclarativeDN = UpdateSelfDeclarativeDN(null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    var syncIllnessesResult: SyncResultDN = SyncResultDN(null)
    var syncDrugAllergiesResult: SyncResultDN = SyncResultDN(null)

    // --- Existing methods ---
    override suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN> = flow {
        if (shouldThrowError) throw error
        emit(getPatientGeneralResult)
    }

    override suspend fun getPatientSelfDeclarative(natCode: String, patientID: Int): Flow<PatientSelfDeclarativeDN> = flow {
        if (shouldThrowError) throw error
        emit(getPatientSelfDeclarativeResult)
    }

    override suspend fun getPatientDrugAllergies(natCode: String, patientID: Int): Flow<List<DrugItemAllergiesDN>> = flow {
        if (shouldThrowError) throw error
        emit(getPatientDrugAllergiesResult)
    }

    override suspend fun getPatientHospitalizations(natCode: String, patientID: Int): Flow<List<PatientHospitalizationsDN>> = flow {
        if (shouldThrowError) throw error
        emit(getPatientHospitalizationsResult)
    }

    override suspend fun getPatientVisits(natCode: String, patientID: Int): Flow<List<PatientVisitDN>> = flow {
        if (shouldThrowError) throw error
        emit(getPatientVisitsResult)
    }

    override suspend fun getPatientLabs(natCode: String, patientID: Int): Flow<List<PatientLabDN>> = flow {
        if (shouldThrowError) throw error
        emit(getPatientLabsResult)
    }

    override suspend fun getPatientImaging(natCode: String, patientID: Int): Flow<List<PatientImagingDN>> = flow {
        if (shouldThrowError) throw error
        emit(getPatientImagingResult)
    }

    // --- New methods ---
    override suspend fun getAllProvinces(): Flow<List<ProvinceItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(getAllProvincesResult)
    }

    override suspend fun getProvinceCities(provinceID: Int): Flow<List<ProvinceCityItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(getProvinceCitiesResult)
    }

    override suspend fun getBloodGroups(): Flow<List<BloodGroupDN>> = flow {
        if (shouldThrowError) throw error
        emit(getBloodGroupsResult)
    }

    override suspend fun getMaritalStatus(): Flow<List<MaritalStatusDN>> = flow {
        if (shouldThrowError) throw error
        emit(getMaritalStatusResult)
    }

    override suspend fun getSmokingStatus(): Flow<List<SmokingStatusDN>> = flow {
        if (shouldThrowError) throw error
        emit(getSmokingStatusResult)
    }

    override suspend fun getSelfDeclarableIllnesses(): Flow<List<IllnessItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(getSelfDeclarableIllnessesResult)
    }

    override suspend fun getSelfDeclarableIllnessesByGroup(): Flow<List<SelfDeclarableIllnessGroupDN>> = flow {
        if (shouldThrowError) throw error
        emit(getSelfDeclarableIllnessesByGroupResult)
    }

    override suspend fun getAllDrugs(): Flow<List<DrugItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(getAllDrugsResult)
    }

    override suspend fun updatePatient(request: UpdatePatientRequest): UpdatePatientDN {
        if (shouldThrowError) throw error
        return updatePatientResult
    }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequest): AddSelfDeclarativeDN {
        if (shouldThrowError) throw error
        return addSelfDeclarativeResult
    }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequest): UpdateSelfDeclarativeDN {
        if (shouldThrowError) throw error
        return updateSelfDeclarativeResult
    }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessSelfDeclarativesRequest): SyncResultDN {
        if (shouldThrowError) throw error
        return syncIllnessesResult
    }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequest): SyncResultDN {
        if (shouldThrowError) throw error
        return syncDrugAllergiesResult
    }
}

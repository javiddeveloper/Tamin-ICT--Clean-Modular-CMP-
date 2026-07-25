package com.tamin.taminhamrah.feature.treatment.fake

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

/**
 * Configurable fake [HealthRepository] for the health-profile ViewModel tests.
 *
 * Defaults emit realistic [TreatmentTestData]; set [shouldThrowError] to drive failure
 * paths. The error is thrown inside the flow so the ViewModel's `catch` handles it.
 */
class FakeHealthRepository : HealthRepository {

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Health Repository Error")

    var patientGeneralResult: PatientGeneralDN = TreatmentTestData.patientGeneral()
    var selfDeclarativeResult: PatientSelfDeclarativeDN = TreatmentTestData.selfDeclarative()
    var drugAllergiesResult: List<DrugItemAllergiesDN> = emptyList()
    var hospitalizationsResult: List<PatientHospitalizationsDN> = emptyList()
    var visitsResult: List<PatientVisitDN> = emptyList()
    var labsResult: List<PatientLabDN> = emptyList()
    var imagingResult: List<PatientImagingDN> = emptyList()

    override suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN> = flow {
        if (shouldThrowError) throw error
        emit(patientGeneralResult)
    }

    override suspend fun getPatientSelfDeclarative(
        natCode: String, patientID: Int
    ): Flow<PatientSelfDeclarativeDN> = flow {
        if (shouldThrowError) throw error
        emit(selfDeclarativeResult)
    }

    override suspend fun getPatientDrugAllergies(
        natCode: String, patientID: Int
    ): Flow<List<DrugItemAllergiesDN>> = flow {
        if (shouldThrowError) throw error
        emit(drugAllergiesResult)
    }

    override suspend fun getPatientHospitalizations(
        natCode: String, patientID: Int
    ): Flow<List<PatientHospitalizationsDN>> = flow {
        if (shouldThrowError) throw error
        emit(hospitalizationsResult)
    }

    override suspend fun getPatientVisits(
        natCode: String, patientID: Int
    ): Flow<List<PatientVisitDN>> = flow {
        if (shouldThrowError) throw error
        emit(visitsResult)
    }

    override suspend fun getPatientLabs(
        natCode: String, patientID: Int
    ): Flow<List<PatientLabDN>> = flow {
        if (shouldThrowError) throw error
        emit(labsResult)
    }

    override suspend fun getPatientImaging(
        natCode: String, patientID: Int
    ): Flow<List<PatientImagingDN>> = flow {
        if (shouldThrowError) throw error
        emit(imagingResult)
    }

    override suspend fun getAllProvinces(): Flow<List<ProvinceItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getProvinceCities(provinceID: Int): Flow<List<ProvinceCityItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getBloodGroups(): Flow<List<BloodGroupDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getMaritalStatus(): Flow<List<MaritalStatusDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getSmokingStatus(): Flow<List<SmokingStatusDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getSelfDeclarableIllnesses(): Flow<List<IllnessItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getSelfDeclarableIllnessesByGroup(): Flow<List<SelfDeclarableIllnessGroupDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun getAllDrugs(): Flow<List<DrugItemDN>> = flow {
        if (shouldThrowError) throw error
        emit(emptyList())
    }

    override suspend fun updatePatient(request: UpdatePatientRequest): UpdatePatientDN {
        if (shouldThrowError) throw error
        return UpdatePatientDN(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequest): AddSelfDeclarativeDN {
        if (shouldThrowError) throw error
        return AddSelfDeclarativeDN(null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequest): UpdateSelfDeclarativeDN {
        if (shouldThrowError) throw error
        return UpdateSelfDeclarativeDN(null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessSelfDeclarativesRequest): SyncResultDN {
        if (shouldThrowError) throw error
        return SyncResultDN(null)
    }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequest): SyncResultDN {
        if (shouldThrowError) throw error
        return SyncResultDN(null)
    }
}

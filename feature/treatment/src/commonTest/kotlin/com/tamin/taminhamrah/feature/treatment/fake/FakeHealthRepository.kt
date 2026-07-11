package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
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
}

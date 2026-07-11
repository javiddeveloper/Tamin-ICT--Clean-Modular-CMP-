package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHealthRepository : HealthRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Health Repository Error")

    var getPatientGeneralResult: PatientGeneralDN = PatientGeneralDN(0, "", "", "", "", "", "", null, null, null)
    var getPatientSelfDeclarativeResult: PatientSelfDeclarativeDN = PatientSelfDeclarativeDN(null, null, null, null, null, null, null, 0, null, null, null, null, null, null)
    var getPatientDrugAllergiesResult: List<DrugItemAllergiesDN> = emptyList()

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
}

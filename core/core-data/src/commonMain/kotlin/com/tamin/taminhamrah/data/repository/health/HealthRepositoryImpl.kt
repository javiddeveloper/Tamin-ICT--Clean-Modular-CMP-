package com.tamin.taminhamrah.data.repository.health

import com.tamin.taminhamrah.data.mapper.health.toDomain
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSource
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class HealthRepositoryImpl(
    private val healthRemoteDataSource: HealthRemoteDataSource
) : HealthRepository {

    override suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN> = flow {
        val result = healthRemoteDataSource.getPatientGeneral(natCode)
        emit(result!!.toDomain())
    }

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): Flow<PatientSelfDeclarativeDN> = flow {
        val result = healthRemoteDataSource.getPatientSelfDeclarative(natCode, patientID)
        emit(result!!.toDomain())
    }

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): Flow<List<DrugItemAllergiesDN>> = flow {
        val remoteData = healthRemoteDataSource.getPatientDrugAllergies(natCode, patientID)
        val list = remoteData?.list?.map { it.toDomain() }
        emit(list ?: emptyList())
    }
}

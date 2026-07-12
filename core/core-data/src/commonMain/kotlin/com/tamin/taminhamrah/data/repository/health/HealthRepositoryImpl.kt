package com.tamin.taminhamrah.data.repository.health

import com.tamin.taminhamrah.data.local.dao.HealthDao
import com.tamin.taminhamrah.data.mapper.health.toDomain
import com.tamin.taminhamrah.data.mapper.health.toEntity
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSource
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDN
import com.tamin.taminhamrah.model.health.PatientImagingDN
import com.tamin.taminhamrah.model.health.PatientLabDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.PatientVisitDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Offline-first: emit the cached value immediately, refresh from remote, persist,
 * then re-emit the reactive cache flow. Remote failures are swallowed only when a
 * cache exists (so the screen keeps working offline); otherwise they propagate.
 * Mirrors the app pattern in PersonalRepositoryImpl / RecipientRepositoryImpl.
 */
internal class HealthRepositoryImpl(
    private val healthRemoteDataSource: HealthRemoteDataSource,
    private val healthDao: HealthDao
) : HealthRepository {

    override suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN> = flow {
        val local = healthDao.getGeneral(natCode).first()
        local?.let { emit(it.toDomain()) }
        try {
            val remote = healthRemoteDataSource.getPatientGeneral(natCode)?.toDomain()
            if (remote != null) healthDao.upsertGeneral(remote.toEntity(natCode))
        } catch (e: Exception) {
            if (local == null) throw e
        }
        emitAll(healthDao.getGeneral(natCode).filterNotNull().map { it.toDomain() })
    }.distinctUntilChanged()

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): Flow<PatientSelfDeclarativeDN> = flow {
        val local = healthDao.getSelfDeclarative(natCode).first()
        local?.let { emit(it.toDomain()) }
        try {
            val remote = healthRemoteDataSource.getPatientSelfDeclarative(natCode, patientID)?.toDomain()
            if (remote != null) healthDao.upsertSelfDeclarative(remote.toEntity(natCode))
        } catch (e: Exception) {
            if (local == null) throw e
        }
        emitAll(healthDao.getSelfDeclarative(natCode).filterNotNull().map { it.toDomain() })
    }.distinctUntilChanged()

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): Flow<List<DrugItemAllergiesDN>> = flow {
        val local = healthDao.getDrugAllergies(natCode).first()
        if (local.isNotEmpty()) emit(local.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientDrugAllergies(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.clearDrugAllergies(natCode)
            healthDao.insertDrugAllergies(remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(healthDao.getDrugAllergies(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientHospitalizations(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientHospitalizationsDN>> = flow {
        val local = healthDao.getHospitalizations(natCode).first()
        if (local.isNotEmpty()) emit(local.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientHospitalizations(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.clearHospitalizations(natCode)
            healthDao.insertHospitalizations(remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(healthDao.getHospitalizations(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientVisits(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientVisitDN>> = flow {
        val local = healthDao.getVisits(natCode).first()
        if (local.isNotEmpty()) emit(local.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientVisits(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.clearVisits(natCode)
            healthDao.insertVisits(remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(healthDao.getVisits(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientLabs(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientLabDN>> = flow {
        val local = healthDao.getLabs(natCode).first()
        if (local.isNotEmpty()) emit(local.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientLabs(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.clearLabs(natCode)
            healthDao.insertLabs(remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(healthDao.getLabs(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientImaging(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientImagingDN>> = flow {
        val local = healthDao.getImaging(natCode).first()
        if (local.isNotEmpty()) emit(local.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientImaging(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.clearImaging(natCode)
            healthDao.insertImaging(remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(healthDao.getImaging(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()
}

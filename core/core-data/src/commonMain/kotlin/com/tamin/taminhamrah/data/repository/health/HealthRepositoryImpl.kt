package com.tamin.taminhamrah.data.repository.health

import com.tamin.taminhamrah.data.local.dao.HealthDao
import com.tamin.taminhamrah.data.mapper.health.toDomain
import com.tamin.taminhamrah.data.mapper.health.toDTO
import com.tamin.taminhamrah.data.mapper.health.toEntity
import com.tamin.taminhamrah.dataSource.health.HealthRemoteDataSource
import com.tamin.taminhamrah.model.health.ActFrequencyDN
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
 *
 * Lookup/reference data (provinces, cities, blood groups, etc.) is remote-only
 * with no local cache since it is shared data that seldom changes.
 * Mutation endpoints (updatePatient, addSelfDeclarative, etc.) call remote directly
 * and return the server's response.
 */
internal class HealthRepositoryImpl(
    private val healthRemoteDataSource: HealthRemoteDataSource,
    private val healthDao: HealthDao
) : HealthRepository {

    override suspend fun getPatientGeneral(natCode: String): Flow<PatientGeneralDN> = flow {
        val localPatientGeneral = healthDao.getGeneral(natCode).first()
        localPatientGeneral?.let { emit(it.toDomain()) }
        try {
            val remote = healthRemoteDataSource.getPatientGeneral(natCode)?.toDomain()
            if (remote != null) healthDao.upsertGeneral(remote.toEntity(natCode))
        } catch (e: Exception) {
            if (localPatientGeneral == null) throw e
        }
        emitAll(healthDao.getGeneral(natCode).filterNotNull().map { it.toDomain() })
    }.distinctUntilChanged()

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): Flow<PatientSelfDeclarativeDN> = flow {
        val localPatientSelfDeclarative = healthDao.getSelfDeclarative(natCode).first()
        localPatientSelfDeclarative?.let { emit(it.toDomain()) }
        try {
            val remote = healthRemoteDataSource.getPatientSelfDeclarative(natCode, patientID)?.toDomain()
            if (remote != null) healthDao.upsertSelfDeclarative(remote.toEntity(natCode))
        } catch (e: Exception) {
            if (localPatientSelfDeclarative == null) throw e
        }
        emitAll(healthDao.getSelfDeclarative(natCode).filterNotNull().map { it.toDomain() })
    }.distinctUntilChanged()

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): Flow<List<DrugItemAllergiesDN>> = flow {
        val localPatientDrugAllergies = healthDao.getDrugAllergies(natCode).first()
        if (localPatientDrugAllergies.isNotEmpty()) emit(localPatientDrugAllergies.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientDrugAllergies(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.replaceDrugAllergies(natCode, remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (localPatientDrugAllergies.isEmpty()) throw e
        }
        emitAll(healthDao.getDrugAllergies(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientHospitalizations(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientHospitalizationsDN>> = flow {
        val localPatientHospitalizations = healthDao.getHospitalizations(natCode).first()
        if (localPatientHospitalizations.isNotEmpty()) emit(localPatientHospitalizations.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientHospitalizations(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.replaceHospitalizations(natCode, remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (localPatientHospitalizations.isEmpty()) throw e
        }
        emitAll(healthDao.getHospitalizations(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientVisits(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientVisitDN>> = flow {
        val localPatientVisits = healthDao.getVisits(natCode).first()
        if (localPatientVisits.isNotEmpty()) emit(localPatientVisits.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientVisits(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.replaceVisits(natCode, remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (localPatientVisits.isEmpty()) throw e
        }
        emitAll(healthDao.getVisits(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientLabs(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientLabDN>> = flow {
        val localPatientLabs = healthDao.getLabs(natCode).first()
        if (localPatientLabs.isNotEmpty()) emit(localPatientLabs.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientLabs(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.replaceLabs(natCode, remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (localPatientLabs.isEmpty()) throw e
        }
        emitAll(healthDao.getLabs(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getPatientImaging(
        natCode: String,
        patientID: Int
    ): Flow<List<PatientImagingDN>> = flow {
        val localPatientImaging = healthDao.getImaging(natCode).first()
        if (localPatientImaging.isNotEmpty()) emit(localPatientImaging.map { it.toDomain() })
        try {
            val remote = healthRemoteDataSource.getPatientImaging(natCode, patientID)?.list?.map { it.toDomain() } ?: emptyList()
            healthDao.replaceImaging(natCode, remote.map { it.toEntity(natCode) })
        } catch (e: Exception) {
            if (localPatientImaging.isEmpty()) throw e
        }
        emitAll(healthDao.getImaging(natCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    // --- Location (remote-only, shared lookup data) ---

    override suspend fun getAllProvinces(): Flow<List<ProvinceItemDN>> = flow {
        val remote = healthRemoteDataSource.getAllProvinces()?.list?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    override suspend fun getProvinceCities(provinceID: Int): Flow<List<ProvinceCityItemDN>> = flow {
        val remote = healthRemoteDataSource.getProvinceCities(provinceID)?.list?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    // --- Lookup (remote-only, shared reference data) ---

    override suspend fun getBloodGroups(): Flow<List<BloodGroupDN>> = flow {
        val remote = healthRemoteDataSource.getBloodGroups()?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    override suspend fun getMaritalStatus(): Flow<List<MaritalStatusDN>> = flow {
        val remote = healthRemoteDataSource.getMaritalStatus()?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    override suspend fun getSmokingStatus(): Flow<List<SmokingStatusDN>> = flow {
        val remote = healthRemoteDataSource.getSmokingStatus()?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    override suspend fun getActFrequencies(): Flow<List<ActFrequencyDN>> = flow {
        val remote = healthRemoteDataSource.getActFrequencies()?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    // --- Illnesses ---

    override suspend fun getSelfDeclarableIllnesses(): Flow<List<IllnessItemDN>> = flow {
        val remote = healthRemoteDataSource.getSelfDeclarableIllnesses()?.list?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    override suspend fun getSelfDeclarableIllnessesByGroup(): Flow<List<SelfDeclarableIllnessGroupDN>> = flow {
        val remote = healthRemoteDataSource.getSelfDeclarableIllnessesByGroup()?.list?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    // --- Drug master list ---

    override suspend fun getAllDrugs(): Flow<List<DrugItemDN>> = flow {
        val remote = healthRemoteDataSource.getAllDrugs()?.list?.map { it.toDomain() } ?: emptyList()
        emit(remote)
    }

    // --- Mutations (POST, remote-only) ---

    override suspend fun updatePatient(request: UpdatePatientRequest): UpdatePatientDN {
        return healthRemoteDataSource.updatePatient(request.toDTO())?.toDomain()
            ?: throw IllegalStateException("updatePatient returned null")
    }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequest): AddSelfDeclarativeDN {
        return healthRemoteDataSource.addSelfDeclarative(request.toDTO())?.toDomain()
            ?: throw IllegalStateException("addSelfDeclarative returned null")
    }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequest): UpdateSelfDeclarativeDN {
        return healthRemoteDataSource.updateSelfDeclarative(request.toDTO())?.toDomain()
            ?: throw IllegalStateException("updateSelfDeclarative returned null")
    }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessSelfDeclarativesRequest): SyncResultDN {
        return healthRemoteDataSource.syncIllnessSelfDeclaratives(request.toDTO())?.toDomain()
            ?: throw IllegalStateException("syncIllnessSelfDeclaratives returned null")
    }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequest): SyncResultDN {
        return healthRemoteDataSource.syncDrugAllergies(request.toDTO())?.toDomain()
            ?: throw IllegalStateException("syncDrugAllergies returned null")
    }
}

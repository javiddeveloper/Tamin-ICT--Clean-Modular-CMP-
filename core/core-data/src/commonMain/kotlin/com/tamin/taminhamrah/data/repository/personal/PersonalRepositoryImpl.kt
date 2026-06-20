package com.tamin.taminhamrah.data.repository.personal

import com.tamin.taminhamrah.data.local.dao.PersonalDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSource
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PersonalRepositoryImpl(
    private val personalRemoteDataSource: PersonalRemoteDataSource,
    private val personalDao: PersonalDao
) : PersonalRepository {
    override fun getPersonalInfo(): Flow<PersonalInfoDN?> = flow {
        val localInfo = personalDao.getPersonalInfo().first()
        emit(localInfo?.toDomain())

        try {
            val response = personalRemoteDataSource.getPersonalInfo()
            val remoteInfo = response?.toDomain()

            if (remoteInfo != null) {
                personalDao.upsertPersonalInfo(remoteInfo.toEntity())
            }
        } catch (e: Exception) {
            if (localInfo == null) {
                throw e
            }
        }

        emitAll(personalDao.getPersonalInfo().map { it?.toDomain() })
    }.distinctUntilChanged()

    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {
        emit(personalRemoteDataSource.getAge(birthDate).toDomain())
    }
    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> = flow {
        try {
            val response = personalRemoteDataSource.getDisabilityDependentInfo(ApiQueryParamDN(filters = filters))
            emit(response.map { it.toDomain() })
        } catch (e: Exception) {
            throw e
        }
    }
}


package com.tamin.taminhamrah.feature.profile.data.repository

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.feature.profile.data.mapper.toDomain
import com.tamin.taminhamrah.feature.profile.data.mapper.toEntity
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
) : UserRepository {

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        var cached: IdentityInfoDN? = null
        try {
            cached = userDao.getIdentityInfo().firstOrNull()?.toDomain()
            cached?.let { emit(it) }
        } catch (_: Exception) {
            // Ignore cache read errors.
        }

        try {
            val remoteData = userRemoteDataSource.getIdentityInfo()
            userDao.upsertIdentityInfo(remoteData.toEntity())
            emit(remoteData.toDomain())
        } catch (e: Throwable) {
            if (cached == null) throw e
        }
    }

    override suspend fun getUserProfileImage(): String =
        userRemoteDataSource.getUserProfileImage()

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        try {
            val remoteData = userRemoteDataSource.fetchTaminRelation()
            emit(remoteData.toDomain())
        } catch (e: Throwable) {
            throw e
        }
    }
}

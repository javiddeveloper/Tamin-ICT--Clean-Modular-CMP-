package com.tamin.taminhamrah.feature.profile.data.repository

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
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
        val cached: IdentityInfoDN? = userDao.getIdentityInfo().firstOrNull()?.toDomain()
        cached?.let { emit(it) }
        val remoteData = userRemoteDataSource.getIdentityInfo()
        userDao.upsertIdentityInfo(remoteData.toEntity())
        emit(remoteData.toDomain())
    }

    override suspend fun getUserProfileImage(): Flow<String> = flow {
        val imageData = userRemoteDataSource.getUserProfileImage()
        emit(imageData)
    }

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        val remoteData = userRemoteDataSource.fetchTaminRelation()
        emit(remoteData.toDomain())
    }

    override suspend fun sendImageRequest(
        branchCode: String,
        filter: List<ApiFilterDN>
    ) = flow {
            val remoteData = userRemoteDataSource.sendImageRequest(branchCode, filter)
            emit(remoteData)
        }
}

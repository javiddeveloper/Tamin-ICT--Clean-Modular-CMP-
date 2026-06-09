package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.relation.TaminRelationDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.VerifyMobileReq
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

    override suspend fun changeMobile(filter: List<ApiFilterDN>): Flow<EditMobileResponseDN> = flow {
        val remoteData = userRemoteDataSource.changeMobile(filter)
        emit(remoteData.toDomain())
    }

    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flow {
        val request = VerifyMobileReq(mobile, otp, otpHashCode)
        val remoteData = userRemoteDataSource.verifyChangeMobileCode(request)
        emit(remoteData)
    }

}

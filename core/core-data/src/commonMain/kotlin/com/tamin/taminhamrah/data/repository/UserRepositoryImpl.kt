package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.relation.TaminRelationDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.util.Logger
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
    ): Flow<String> = flow {
        val remoteData = userRemoteDataSource.sendImageRequest(branchCode, filter)
        emit(remoteData)
    }

    override suspend fun getSubDominantsInfo(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<SubdominantDN> = flow {
        val remoteData = userRemoteDataSource.getSubDominantsInfo(page, start, limit, filter, sort)
        Logger.d("getSubDominantsInfo", remoteData.toString())
        emit(remoteData.toDomain())
    }

    override suspend fun getBankAccountList(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<BankAccountDN>> = flow {
        val remoteData = userRemoteDataSource.getBankAccountList(page, start, limit, filter, sort)
        Logger.d("getBankAccountList", remoteData?.list.toString())
        val accountList = remoteData?.list?.map { it.toDomain() }
        emit(accountList ?: emptyList())
    }

}

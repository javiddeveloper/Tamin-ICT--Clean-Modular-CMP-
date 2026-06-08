package com.tamin.taminhamrah.feature.profile.data.repository

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.feature.profile.data.mapper.toDomain
import com.tamin.taminhamrah.feature.profile.data.mapper.toEntity
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
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
    ) = flow {
        val remoteData = userRemoteDataSource.sendImageRequest(branchCode, filter)
        emit(remoteData)
    }

    override suspend fun getSubDominantsInfo(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ) = flow {
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
    ) = flow {
        val remoteData = userRemoteDataSource.getBankAccountList(page, start, limit, filter, sort)
        Logger.d("getBankAccountList", remoteData?.list.toString())
        val accountList = remoteData?.list?.map { it.toDomain() }
        emit(accountList ?: emptyList())
    }


    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flow {
        val remoteData = userRemoteDataSource.getInsuredActiveBranch()
        Logger.d("getInsuredActiveBranch", remoteData.toString())
        val insuredActiveBranchList = remoteData?.map { it.toDomain() }
        emit(insuredActiveBranchList ?: emptyList())
    }

    override suspend fun getRelationTaminAll(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<ActiveRelationDN>> = flow {
        val remoteData = userRemoteDataSource.getRelationTaminAll(page, start, limit, filter, sort)
        Logger.d("getRelationTaminAll", remoteData?.list.toString())
        val relationList = remoteData?.list?.map { it.toDomain() }
        emit(relationList ?: emptyList())
    }

    override suspend fun getElectronicFile(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN>> = flow {
        val remoteData = userRemoteDataSource.getElectronicFile(page, start, limit, filter, sort)
        Logger.d("getElectronicFile", remoteData?.list.toString())
        val electronicFileList = remoteData?.list?.map { it.toDomain() }
        emit(electronicFileList ?: emptyList())
    }
}

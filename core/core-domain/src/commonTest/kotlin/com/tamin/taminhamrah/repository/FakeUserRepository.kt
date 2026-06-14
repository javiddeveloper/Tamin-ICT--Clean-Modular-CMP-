package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.relation.TaminRelationDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRepository : UserRepository {
    var identityInfoResult: IdentityInfoDN? = null
    var userProfileImageResult: String = ""
    var taminRelationResult: TaminRelationDN? = null
    var sendImageResult: String = ""

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        identityInfoResult?.let { emit(it) }
    }

    override suspend fun getUserProfileImage(): Flow<String> = flow {
        emit(userProfileImageResult)
    }

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        taminRelationResult?.let { emit(it) }
    }

    override suspend fun sendImageRequest(branchCode: String, filter: List<ApiFilterDN>): Flow<String> = flow {
        emit(sendImageResult)
    }
}

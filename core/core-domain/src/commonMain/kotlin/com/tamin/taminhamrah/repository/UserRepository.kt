package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getIdentityInfo(): Flow<IdentityInfoDN>
    suspend fun getUserProfileImage(): String
    suspend fun fetchTaminRelation(): Flow<TaminRelationDN>
    suspend fun sendImageRequest(branchCode: String, filter: List<ApiFilterDN>): Flow<String>
}

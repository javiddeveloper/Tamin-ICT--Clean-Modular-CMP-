package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.identity.IdentityInfoDN

import com.tamin.taminhamrah.model.relation.TaminRelationDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getIdentityInfo(): Flow<IdentityInfoDN>
    suspend fun getUserProfileImage(): Flow<String>
    suspend fun fetchTaminRelation(): Flow<TaminRelationDN>
    suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String>
    suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN>
    suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String>
}

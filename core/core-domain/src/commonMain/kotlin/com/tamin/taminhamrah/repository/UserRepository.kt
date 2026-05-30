package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getIdentityInfo(): Flow<IdentityInfoDN>
    suspend fun getUserProfileImage(): String
}

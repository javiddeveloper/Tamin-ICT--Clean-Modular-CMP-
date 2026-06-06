package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getIdentityInfo(): Flow<IdentityInfoDN>
    suspend fun getUserProfileImage(): Flow<String>
    suspend fun fetchTaminRelation(): Flow<TaminRelationDN>
    suspend fun sendImageRequest(branchCode: String, filter: List<ApiFilterDN>): Flow<String>

    suspend fun getSubDominantsInfo(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<SubdominantDN>

    suspend fun getBankAccountList(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<BankAccountDN>>

    suspend fun getInsuredActiveBranch() : Flow<List<InsuredActiveBranchDN>>

}

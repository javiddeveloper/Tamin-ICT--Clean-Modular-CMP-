package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getIdentityInfo(): Flow<IdentityInfoDN>
    suspend fun getUserProfileImage(): Flow<String>
    suspend fun fetchTaminRelation(): Flow<TaminRelationDN>
    suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String>
    suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN>
    suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String>
    suspend fun getSubDominantsInfo(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<SubdominantDN>

    suspend fun getBankAccountList(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<BankAccountDN>>

    suspend fun getInsuredActiveBranch() : Flow<List<InsuredActiveBranchDN>>

    suspend fun getRelationTaminAll(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ActiveRelationDN>>

    fun getElectronicFile(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ElectronicFileDN>>

    fun checkUserIsNew(nationalId: String): Flow<Boolean>
}

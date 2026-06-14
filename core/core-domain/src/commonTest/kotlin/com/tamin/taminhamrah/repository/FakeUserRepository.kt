package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.relation.TaminRelationDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRepository : UserRepository {
    var identityInfoResult: IdentityInfoDN? = null
    var userProfileImageResult: String = ""
    var taminRelationResult: TaminRelationDN? = null
    var sendImageResult: String = ""
    var subDominantsResult: SubdominantDN? = null
    var bankAccountListResult: List<BankAccountDN> = emptyList()
    var changeMobileResult: EditMobileResponseDN? = null
    var verifyChangeMobileResult: String = ""

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        identityInfoResult?.let { emit(it) }
    }

    override suspend fun getUserProfileImage(): Flow<String> = flow {
        emit(userProfileImageResult)
    }

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        taminRelationResult?.let { emit(it) }
    }

    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flow {
        emit(sendImageResult)
    }

    override suspend fun getSubDominantsInfo(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<SubdominantDN> = flow {
        subDominantsResult?.let { emit(it) }
    }

    override suspend fun getBankAccountList(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<BankAccountDN>> = flow {
        emit(bankAccountListResult)
    }

    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRelationTaminAll(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<ActiveRelationDN>> {
        TODO("Not yet implemented")
    }

    override suspend fun getElectronicFile(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<ElectronicFileDN>> {
        TODO("Not yet implemented")
    }

    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {
        changeMobileResult?.let { emit(it) }
    }

    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flow {
        emit(verifyChangeMobileResult)
    }

    override suspend fun sendImageRequest(
        branchCode: String,
        filter: List<ApiFilterDN>
    ): Flow<String> {
        TODO("Not yet implemented")
    }
}

package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeUserRepository : UserRepository {
    var identityInfoResult: IdentityInfoDN? = null
    var userProfileImageResult: String = ""
    var taminRelationResult: TaminRelationDN? = null
    var sendImageResult: String = ""
    var subDominantsResult: SubdominantDN? = null
    var bankAccountListResult: List<BankAccountDN> = emptyList()
    var changeMobileResult: EditMobileResponseDN? = null
    var verifyChangeMobileResult: String = ""
    var insuredActiveBranchResult: List<InsuredActiveBranchDN> = emptyList()
    var relationTaminAllResult: List<ActiveRelationDN> = emptyList()
    var electronicFileResult: List<ElectronicFileDN> = emptyList()
    var userProfileResult: UserProfileDN? = null

    var shouldThrowError = false
    var error: Throwable = RuntimeException("User Repository Error")

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        if (shouldThrowError) throw error
        identityInfoResult?.let { emit(it) }
    }

    override suspend fun getUserProfileImage(): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(userProfileImageResult)
    }

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        if (shouldThrowError) throw error
        taminRelationResult?.let { emit(it) }
    }

    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(sendImageResult)
    }

    override suspend fun getSubDominantsInfo(
        filters: List<ApiFilterDN>
    ): Flow<SubdominantDN> = flow {
        if (shouldThrowError) throw error
        subDominantsResult?.let { emit(it) }
    }

    override suspend fun getBankAccountList(
        filters: List<ApiFilterDN>
    ): Flow<List<BankAccountDN>> = flow {
        if (shouldThrowError) throw error
        emit(bankAccountListResult)
    }

    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flow {
        if (shouldThrowError) throw error
        emit(insuredActiveBranchResult)
    }

    override suspend fun getRelationTaminAll(
        filters: List<ApiFilterDN>
    ): Flow<List<ActiveRelationDN>> = flow {
        if (shouldThrowError) throw error
        emit(relationTaminAllResult)
    }

    override fun getElectronicFile(
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicFileDN>> = flow {
        if (shouldThrowError) throw error
        emit(electronicFileResult)
    }

    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {
        if (shouldThrowError) throw error
        changeMobileResult?.let { emit(it) }
    }

    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(verifyChangeMobileResult)
    }

    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {
        if (shouldThrowError) throw error
        userProfileResult?.let { emit(it) }
    }

    var checkUserIsNewResult: Boolean = false
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flow {
        if (shouldThrowError) throw error
        emit(checkUserIsNewResult)
    }

    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = flowOf(null)
}

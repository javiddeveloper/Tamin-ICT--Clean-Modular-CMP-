package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.feature.profile.data.mapper.toDomain
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.VerifyMobileRequest
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.data.mapper.user.toDomain
import com.tamin.taminhamrah.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import com.tamin.taminhamrah.model.bankAccount.BankAccountRequestDTO

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
        serialId: String
    ) = flow {
        val domainFilters = listOf(
            ApiFilterDN(
                property = FilterProperty.SERIAL_ID,
                operator = FilterOperator.EQ,
                value = serialId
            )
        )
        val remoteData = userRemoteDataSource.sendImageRequest(branchCode, domainFilters)
        emit(remoteData)
    }

    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {
        val remoteData = userRemoteDataSource.changeMobile(mobileNumber)
        emit(remoteData.toDomain())
    }

    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flow {
        val request = VerifyMobileRequest(mobile, otp, otpHashCode)
        val remoteData = userRemoteDataSource.verifyChangeMobileCode(request)
        emit(remoteData)
    }


    override suspend fun getSubDominantsInfo(
        filters: List<ApiFilterDN>
    ): Flow<SubdominantDN> = flow {
        val remoteData = userRemoteDataSource.getSubDominantsInfo(ApiQueryParamDN(filters = filters))
        Logger.d("getSubDominantsInfo", remoteData.toString())
        emit(remoteData.toDomain())
    }

    override suspend fun getBankAccountList(
        filters: List<ApiFilterDN>
    ): Flow<List<BankAccountDN>> = flow {
        val remoteData = userRemoteDataSource.getBankAccountList(ApiQueryParamDN(filters = filters))
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
        filters: List<ApiFilterDN>
    ): Flow<List<ActiveRelationDN>> = flow {
        val remoteData = userRemoteDataSource.getRelationTaminAll(ApiQueryParamDN(filters = filters))
        Logger.d("getRelationTaminAll", remoteData?.list.toString())
        val relationList = remoteData?.list?.map { it.toDomain() }
        emit(relationList ?: emptyList())
    }

    override fun getElectronicFile(
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicFileDN>> = flow {
        val remoteData = userRemoteDataSource.getElectronicFile(ApiQueryParamDN(filters = filters))
        val electronicFileList = remoteData?.list?.map { it.toDomain() }
        emit(electronicFileList ?: emptyList())
    }

    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {
        val result = userRemoteDataSource.getUserProfile()
        emit(result!!.toDomain())
    }
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flow {
        emit(userRemoteDataSource.checkUserIsNew(nationalId))
    }

    /**
     * The millis-to-string conversion lives here rather than in the caller: which shape the wire
     * wants is a data-layer concern, and the service reads `dateOfStart` as a string.
     */
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = flow {
        val created = userRemoteDataSource.registerBankAccount(
            BankAccountRequestDTO(
                accountNumber = accountNumber,
                bank = bankCode,
                accountType = accountTypeCode,
                dateOfStart = startDateMillis.toString(),
            )
        )
        emit(created?.personal?.referenceCode)
    }
}

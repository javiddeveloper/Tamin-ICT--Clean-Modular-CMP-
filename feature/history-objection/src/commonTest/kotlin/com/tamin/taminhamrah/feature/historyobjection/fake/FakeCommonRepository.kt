package com.tamin.taminhamrah.feature.historyobjection.fake

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Only [getInsuranceTypes] and [checkUserType] are exercised by tests — the rest is stubbed to satisfy the interface. */
class FakeCommonRepository : CommonRepository {
    var insuranceTypesResult: List<InsuranceTypeDN> = emptyList()
    var lastInsuranceTypeSearch: String? = null
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var userTypeResult: UserTypeInfoDN = UserTypeInfoDN(userType = UserType.INSURED)

    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> = flow {
        lastInsuranceTypeSearch = searchText
        if (shouldThrowError) throw error
        emit(insuranceTypesResult)
    }

    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow { emit(emptyList()) }
    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> = flow { emit(emptyList()) }
    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = flow { emit(byteArrayOf()) }
    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flow { emit(null) }
    override fun getRoles(): Flow<List<RoleDN>> = flow { emit(emptyList()) }
    override fun checkUserType(): Flow<UserTypeInfoDN> = flow { emit(userTypeResult) }
}

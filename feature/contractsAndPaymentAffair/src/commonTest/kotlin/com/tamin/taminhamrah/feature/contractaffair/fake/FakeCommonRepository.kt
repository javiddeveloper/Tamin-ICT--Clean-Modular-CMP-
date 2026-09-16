package com.tamin.taminhamrah.feature.contractaffair.fake

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Minimal [CommonRepository] double; only [getMainMenu] carries test-controlled data. */
class FakeCommonRepository : CommonRepository {

    var mainMenuResult: List<MainServiceDN> = emptyList()
    var shouldThrowError = false
    var mainMenuError: Throwable = RuntimeException("Menu Error")

    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> =
        flow {
            if (shouldThrowError) throw mainMenuError
            emit(mainMenuResult)
        }

    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> =
        flow { emit(emptyList()) }

    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = flow { emit(byteArrayOf()) }

    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flow { emit(null) }

    override fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> =
        flow { emit(PageDN(items = emptyList(), total = 0)) }

    override fun getRoles(): Flow<List<RoleDN>> = flow { emit(emptyList()) }

    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> =
        flow { emit(emptyList()) }

    override fun checkUserType(): Flow<UserTypeInfoDN> =
        flow { emit(UserTypeInfoDN(userType = UserType.INSURED)) }
}

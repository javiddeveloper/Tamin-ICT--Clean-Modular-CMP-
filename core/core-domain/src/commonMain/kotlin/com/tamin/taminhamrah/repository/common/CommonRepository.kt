package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CommonRepository {
    fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>>
    fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>>
    fun getRegistrationDeclarationForm(): Flow<ByteArray>
    fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?>
    fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> = flow {
        getJobTitle(query).collect { listDn ->
            emit(PageDN(items = listDn?.list.orEmpty(), total = listDn?.total))
        }
    }
    fun getRoles(): Flow<List<com.tamin.taminhamrah.model.common.RoleDN>>
    fun getInsuranceTypes(searchText: String? = null): Flow<List<InsuranceTypeDN>>
    fun checkUserType(): Flow<UserTypeInfoDN>
}


package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCommonRepository : CommonRepository {
    var beneficiaryResult: List<BeneficiaryDN> = emptyList()
    var shouldThrowError = false
    var getBeneficiaryError: Throwable = RuntimeException("Error")
    var mainMenuResult: List<MainServiceDN> = emptyList()
    var getMainMenuError: Throwable = RuntimeException("Menu Error")
    var rolesResult: List<RoleDN> = emptyList()
    var getRolesError: Throwable = RuntimeException("Roles Error")


    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow {
        if (shouldThrowError) {
            throw getBeneficiaryError
        }
        emit(beneficiaryResult)
    }


    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> = flow {
        if (shouldThrowError) {
            throw getMainMenuError
        }
        emit(mainMenuResult)
    }

    override fun getRoles(): Flow<List<RoleDN>> = flow {
        if (shouldThrowError) {
            throw getRolesError
        }
        emit(rolesResult)
    }
}

package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktor.client.statement.HttpStatement

class FakeCommonRepository : CommonRepository {
    var beneficiaryResult: List<BeneficiaryDN> = emptyList()
    var shouldThrowError = false
    var getBeneficiaryError: Throwable = RuntimeException("Error")
    var mainMenuResult: List<MainServiceDN> = emptyList()
    var getMainMenuError: Throwable = RuntimeException("Menu Error")
    var rolesResult: List<RoleDN> = emptyList()
    var getRolesError: Throwable = RuntimeException("Roles Error")
    var jobTitleResult: JobTitleListDN? = null
    var registrationDeclarationFormResult: ByteArray = byteArrayOf()
    var registrationDeclarationFormError: Throwable = RuntimeException("PDF Error")
    var insuranceTypesResult: List<InsuranceTypeDN> = emptyList()
    var getInsuranceTypesError: Throwable = RuntimeException("Insurance Type Error")
    var lastInsuranceTypeSearch: String? = null


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

    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = flow {
        if (shouldThrowError) {
            throw registrationDeclarationFormError
        }
        emit(registrationDeclarationFormResult)
    }

    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flow {
        if (shouldThrowError) throw getBeneficiaryError
        emit(jobTitleResult)
    }

    override fun getRoles(): Flow<List<RoleDN>> = flow {
        if (shouldThrowError) {
            throw getRolesError
        }
        emit(rolesResult)
    }

    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> = flow {
        lastInsuranceTypeSearch = searchText
        if (shouldThrowError) throw getInsuranceTypesError
        emit(insuranceTypesResult)
    }
}

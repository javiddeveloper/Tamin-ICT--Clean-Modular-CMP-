package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.MainServiceDN
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

    override suspend fun getRegistrationDeclarationForm(): HttpStatement {
        if (shouldThrowError) throw getBeneficiaryError
        throw NotImplementedError("Fake does not support HttpStatement")
    }

    override suspend fun getJobTitle(query: ApiQueryParamDN): JobTitleListDN? {
        if (shouldThrowError) throw getBeneficiaryError
        return null
    }
}

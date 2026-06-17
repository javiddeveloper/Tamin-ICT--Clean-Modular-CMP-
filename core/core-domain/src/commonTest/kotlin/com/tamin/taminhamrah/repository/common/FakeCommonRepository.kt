package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCommonRepository : CommonRepository {
    var beneficiaryResult: List<BeneficiaryDN> = emptyList()
    var shouldThrowError = false
    var getBeneficiaryError: Throwable = RuntimeException("Error")

    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow {
        if (shouldThrowError) {
            throw getBeneficiaryError
        }
        emit(beneficiaryResult)
    }
}

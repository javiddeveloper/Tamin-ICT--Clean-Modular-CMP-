package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface CommonRepository {
    fun getBeneficiary(query: ApiQueryParamDN): Flow<List<BeneficiaryDN>>
}

package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface CommonRepository {
    fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>>
    suspend fun getRegistrationDeclarationForm(): Any
    suspend fun getJobTitle(query: ApiQueryParamDN): JobTitleListDN?
}

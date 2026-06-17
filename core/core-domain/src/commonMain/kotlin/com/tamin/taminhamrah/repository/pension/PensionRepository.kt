package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface PensionRepository {
    suspend fun getPensionInquiry(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<PensionInquiryDN>>

    suspend fun getPensionerId(): Flow<List<PensionIdDN>>

    suspend fun getEdictPensioner(
        query: ApiQueryParamDN
    ): Flow<EdictPensionerDN?>
}


package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import kotlinx.coroutines.flow.Flow

interface PensionRepository {
    suspend fun getPensionInquiry(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<PensionInquiryDN>>

    suspend fun getPensionerId(): Flow<List<PensionIdDN>>
}


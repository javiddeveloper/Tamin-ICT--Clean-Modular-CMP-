package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePensionRepository : PensionRepository {
    var pensionInquiryResult: List<PensionInquiryDN> = emptyList()
    var shouldThrowError: Boolean = false
    var getPensionInquiryError: Throwable? = null

    override suspend fun getPensionInquiry(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<PensionInquiryDN>> = flow {
        if (shouldThrowError) {
            throw getPensionInquiryError!!
        }
        emit(pensionInquiryResult)
    }
}

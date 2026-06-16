package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePensionRepository : PensionRepository {
    var pensionInquiryResult: List<PensionInquiryDN> = emptyList()
    var pensionIdResult: List<PensionIdDN> = emptyList()
    var shouldThrowError: Boolean = false
    var getPensionInquiryError: Throwable? = null

    override suspend fun getPensionInquiry(
        filters: List<ApiFilterDN>
    ): Flow<List<PensionInquiryDN>> = flow {
        if (shouldThrowError) {
            throw getPensionInquiryError ?: RuntimeException("Unknown error")
        }
        emit(pensionInquiryResult)
    }

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        if (shouldThrowError) {
            throw getPensionInquiryError ?: RuntimeException("Unknown error")
        }
        emit(pensionIdResult)
    }
}

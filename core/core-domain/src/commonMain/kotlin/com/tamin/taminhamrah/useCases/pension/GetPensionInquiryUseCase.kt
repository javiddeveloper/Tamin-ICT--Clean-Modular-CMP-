package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetPensionInquiryUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<PensionInquiryDN>> {
        return pensionRepository.getPensionInquiry(page, start, limit, filter, sort)
    }
}

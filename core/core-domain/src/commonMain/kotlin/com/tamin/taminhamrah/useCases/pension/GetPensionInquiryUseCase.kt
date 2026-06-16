package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetPensionInquiryUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<PensionInquiryDN>> {
        return pensionRepository.getPensionInquiry(filters)
    }
}

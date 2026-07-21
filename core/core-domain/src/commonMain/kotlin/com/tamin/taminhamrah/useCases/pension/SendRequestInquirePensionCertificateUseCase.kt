package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SendRequestInquirePensionCertificateUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN>
    ): Flow<InquirePensionCertificateDN> {
        return pensionRepository.sendRequestInquirePensionCertificate(filters)
    }
}

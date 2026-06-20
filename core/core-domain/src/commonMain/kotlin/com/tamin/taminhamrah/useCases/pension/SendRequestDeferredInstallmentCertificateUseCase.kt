package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SendRequestDeferredInstallmentCertificateUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        request: DeferredInstallmentRequestDN
    ): Flow<DeferredInstallmentCertificateDN> {
        return pensionRepository.sendRequestDeferredInstallmentCertificate(request)
    }
}

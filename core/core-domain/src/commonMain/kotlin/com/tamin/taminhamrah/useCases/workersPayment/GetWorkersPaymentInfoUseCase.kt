package com.tamin.taminhamrah.useCases.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository

class GetWorkersPaymentInfoUseCase(
    private val repository: WorkersPaymentRepository,
) {
    suspend operator fun invoke(): WorkersPaymentInfoListDN =
        repository.getWorkersPaymentInfo()
}

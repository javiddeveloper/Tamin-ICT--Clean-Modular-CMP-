package com.tamin.taminhamrah.useCases.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository

class PayWorkersDebitUseCase(
    private val repository: WorkersPaymentRepository,
) {
    suspend operator fun invoke(params: WorkersPayDebitParamsDN): WorkersPayDebitResultDN =
        repository.payWorkersDebit(params)
}

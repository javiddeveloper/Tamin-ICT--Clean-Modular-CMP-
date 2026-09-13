package com.tamin.taminhamrah.repository.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN

interface WorkersPaymentRepository {
    suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoListDN

    suspend fun payWorkersDebit(params: WorkersPayDebitParamsDN): WorkersPayDebitResultDN

    /** Verifies a payment after the gateway callback; returns the backend status message. */
    suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String
}

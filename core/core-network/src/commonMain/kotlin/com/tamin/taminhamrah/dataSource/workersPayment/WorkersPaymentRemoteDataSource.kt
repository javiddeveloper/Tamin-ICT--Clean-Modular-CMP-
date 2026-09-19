package com.tamin.taminhamrah.dataSource.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDataDTO

interface WorkersPaymentRemoteDataSource {
    suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoDataDTO

    suspend fun payWorkersDebit(
        request: WorkersPayDebitRequestDTO,
        redirectUrl: String,
    ): WorkersPayDebitDTO

    /** Returns the backend's (already localized) status message; throws on any failure. */
    suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String
}

package com.tamin.taminhamrah.feature.taminServices.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository

/**
 * Feature-local fake for [WorkersPaymentRepository]. Test sources aren't shared across modules, so
 * this mirrors `core-domain`'s `FakeWorkersPaymentRepository` rather than reusing it — same
 * `shouldThrowError` toggle + last-args capture style as the sibling `FakeInspectionRepository`.
 */
class FakeWorkersPaymentRepository : WorkersPaymentRepository {

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake WorkersPayment Repository Error")

    var paymentInfoResult = WorkersPaymentInfoListDN(
        totalAmount = 0L,
        totalPenalty = 0L,
        totalPremium = 0L,
        total = 0,
        list = emptyList(),
    )
    var payDebitResult = WorkersPayDebitResultDN(paymentUrl = null, ticket = null, paymentInfo = null)
    var inspectTicketResult = "پرداخت با موفقیت انجام شد."

    var lastPayDebitParams: WorkersPayDebitParamsDN? = null
    var lastInspectTicketParams: Pair<String?, String?>? = null

    var getPaymentInfoCallCount = 0

    override suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoListDN {
        getPaymentInfoCallCount++
        if (shouldThrowError) throw error
        return paymentInfoResult
    }

    override suspend fun payWorkersDebit(params: WorkersPayDebitParamsDN): WorkersPayDebitResultDN {
        lastPayDebitParams = params
        if (shouldThrowError) throw error
        return payDebitResult
    }

    override suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String {
        lastInspectTicketParams = ticket to paymentInfo
        if (shouldThrowError) throw error
        return inspectTicketResult
    }
}

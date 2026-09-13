package com.tamin.taminhamrah.repository.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN

/**
 * Hand-written fake for [WorkersPaymentRepository] — every method is a thin, configurable
 * pass-through with a `shouldThrowError` toggle and last-args capture, matching the style of
 * [com.tamin.taminhamrah.repository.personalInbox.FakePersonalInboxRepository].
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

    override suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoListDN {
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

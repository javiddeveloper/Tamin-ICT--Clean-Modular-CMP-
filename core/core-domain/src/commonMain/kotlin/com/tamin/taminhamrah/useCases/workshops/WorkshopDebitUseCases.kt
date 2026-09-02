package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** جزئیات محاسبه گردش حساب بدهی — the payable debts of one workshop. */
class GetWorkshopDebitsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkShopDebtDN> = repository.getWorkshopDebits(workshopId, branchCode, page)
}

/** اسناد مطالبه of one debt. */
class GetDemandDocumentsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        debitNumber: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkshopDemandDocDN> = repository.getDemandDocuments(debitNumber, branchCode, page)
}

/** جزئیات محاسبه of a demand document, as a PDF. */
class GetDebitTurnoverPdfUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(debitNumber: String, branchCode: String): PdfDownloadDN =
        repository.getDebitTurnoverPdf(debitNumber, branchCode)
}

/**
 * Pays a debt online.
 *
 * The pre-check and the payment are one operation, not two: the pre-check exists only to gate the
 * payment, and splitting them across two callers is how the old client ended up able to pay
 * without checking. A refused pre-check returns [DebitPaymentDN] with `succeeded = false` so the
 * caller has one shape to handle.
 *
 * Three calls, because the gateway is asked to confirm the ticket before anyone is sent to it:
 * pre-check, pay, then `payment/ticket/current-user/{ticket}` on TFH's own host. The old client
 * (`my-tamin-droid`, `WorkshopInfoViewModel.normalDebitPaymentPreview()`) made the same three, and
 * the address opened afterward is byte-for-byte the one built here.
 *
 * One thing is deliberately not copied. That client ignored the confirmation's outcome — its
 * fragment read `if (result.isSuccess)` with no `else` — so a ticket the gateway would not honor
 * left the pay button doing nothing at all. Here a refused confirmation throws, and the caller
 * reports it like any other payment failure.
 */
class PayWorkshopDebitUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: DebitPaymentRequestDN): DebitPaymentDN {
        val preCheck = repository.checkDebitPayment(request.debitNumber, request.branchCode)
        if (!preCheck.allowed) return DebitPaymentDN(succeeded = false)

        val payment = repository.payWorkshopDebit(request)
        // Nothing to confirm unless the service both agreed and named a ticket; a refusal is
        // returned as it stands so the caller can repeat the reason the service gave.
        if (!payment.isPayable) return payment

        repository.confirmPaymentTicket(payment.ticket)
        return payment
    }
}

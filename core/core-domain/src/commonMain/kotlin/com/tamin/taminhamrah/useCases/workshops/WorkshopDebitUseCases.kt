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
 * The old client made a third call before opening the payment page and this one deliberately does
 * not, so that the next reader does not have to derive it from `my-tamin-droid` again:
 * `normalDebitPaymentPreview()` issued `GET {TFH_URL}ticket/current-user/{ticket}` and then threw
 * the response away. Its fragment used only `isSuccess` plus `data.ticket`, and `data.ticket` had
 * been overwritten client-side with the ticket the app already held, so the address it opened —
 * `TFH_PAYMENT_VIEW_PAGE + ticket` — is byte-for-byte the one built here. The call was left over
 * from an in-app payment screen that was abandoned; the block that consumed its payload is still
 * there, commented out, under the author's note that it did not work.
 *
 * It is a GET, so it reads rather than binds the ticket. Dropping it also drops a silent failure:
 * that fragment had no `else`, so a preview that failed left the button doing nothing at all.
 * Here the page opens and the gateway reports its own problems.
 */
class PayWorkshopDebitUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: DebitPaymentRequestDN): DebitPaymentDN {
        val preCheck = repository.checkDebitPayment(request.debitNumber, request.branchCode)
        if (!preCheck.allowed) return DebitPaymentDN(succeeded = false)
        return repository.payWorkshopDebit(request)
    }
}

package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionResultDN
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** اعتراض به بدهی — the debts of one workshop an objection can still be filed against. */
class GetObjectionableDebitsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkShopDebtDN> = repository.getObjectionableDebits(workshopId, branchCode, page)
}

/**
 * Whether an objection may still be filed against [debt].
 *
 * The service answers only with a day count; which deadline that count is measured against comes
 * from the kind of objection the row admits — 31 days for a برآوردی debt, 21 for a بدوی vote.
 * A row with no filing window at all is refused without asking the service.
 */
class CheckObjectionDeadlineUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(debt: WorkShopDebtDN): Boolean {
        if (debt.objectionKind == ObjectionKind.FILED) return false
        val elapsedDays = repository.getObjectionElapsedDays(debt.orderRecipeDate.ifBlank { "0" })
        return debt.objectionKind.isWithinWindow(elapsedDays)
    }
}

class SaveDebitObjectionUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: DebitObjectionRequestDN): DebitObjectionResultDN =
        repository.saveDebitObjection(request)
}

/** The already-filed objection, as a PDF. */
class GetDebitObjectionPdfUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(seqNo: Long): PdfDownloadDN = repository.getDebitObjectionPdf(seqNo)
}

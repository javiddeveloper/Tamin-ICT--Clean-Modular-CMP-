package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.Article16RequestInfoDN
import com.tamin.taminhamrah.model.workshop.Article16SaveRequestDN
import com.tamin.taminhamrah.model.workshop.Article16SaveResultDN
import com.tamin.taminhamrah.model.workshop.Article16WorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/**
 * The debts a ماده ۱۶ request can be filed against.
 *
 * An empty page is the answer the action menu acts on: it is what tells the user this workshop has
 * no such debt, before the screen is ever opened.
 */
class GetArticle16DebtsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: Article16DebtQuery): PagedListDN<WorkshopsDebtListModelDN> =
        repository.getArticle16Debts(query)
}

/** The read-only workshop panel on step 1 of the request. */
class GetArticle16WorkshopInfoUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(workshopId: String, branchCode: String): Article16WorkshopInfoDN =
        repository.getArticle16WorkshopInfo(workshopId, branchCode)
}

/** پیام کارشناس on a نقص مدارک request, plus the documents already on file. */
class GetArticle16RequestInfoUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(objectionNumber: Long): Article16RequestInfoDN =
        repository.getArticle16RequestInfo(objectionNumber)
}

class SaveArticle16RequestUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: Article16SaveRequestDN): Article16SaveResultDN =
        repository.saveArticle16Request(request)
}

/** The filed ماده ۱۶ request, as a PDF. */
class GetArticle16ReportPdfUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(seqNo: Long): PdfDownloadDN = repository.getArticle16ReportPdf(seqNo)
}

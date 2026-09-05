package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtQuery
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/**
 * The debts a ماده ۱۶ request can be filed against.
 *
 * An empty page is the answer the action menu acts on: it is what tells the user this workshop has
 * no such debt, before the screen is ever opened.
 */
class GetArticleSixteenDebtsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: ArticleSixteenDebtQuery): PagedListDN<WorkshopsDebtListModelDN> =
        repository.getArticleSixteenDebts(query)
}

/** The read-only workshop panel on step 1 of the request. */
class GetArticleSixteenWorkshopInfoUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(workshopId: String, branchCode: String): ArticleSixteenWorkshopInfoDN =
        repository.getArticleSixteenWorkshopInfo(workshopId, branchCode)
}

/** پیام کارشناس on a نقص مدارک request, plus the documents already on file. */
class GetArticleSixteenRequestInfoUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(objectionNumber: Long): ArticleSixteenRequestInfoDN =
        repository.getArticleSixteenRequestInfo(objectionNumber)
}

class SaveArticleSixteenRequestUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(request: ArticleSixteenSaveRequestDN): ArticleSixteenSaveResultDN =
        repository.saveArticleSixteenRequest(request)
}

/** The filed ماده ۱۶ request, as a PDF. */
class GetArticleSixteenReportPdfUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(seqNo: Long): PdfDownloadDN = repository.getArticleSixteenReportPdf(seqNo)
}

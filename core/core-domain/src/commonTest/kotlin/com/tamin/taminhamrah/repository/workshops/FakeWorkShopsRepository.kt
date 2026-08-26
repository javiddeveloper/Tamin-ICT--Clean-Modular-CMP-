package com.tamin.taminhamrah.repository.workshops

import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberIsNewDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtQuery
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionResultDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetQuery
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/**
 * A workshops repository a test can drive.
 *
 * Each call records the query it was handed and answers from a field, so a test can assert on what
 * was *asked* as well as on what came back — which is where the interesting failures live: the
 * filter that quietly went missing, the page that was requested twice.
 */
class FakeWorkShopsRepository : WorkShopsRepository {

    var employerAgreements: PagedListDN<EmployerAgreementDN> = PagedListDN()
    var paymentSheets: PagedListDN<PaymentSheetDN> = PagedListDN()
    var debitReasons: PagedListDN<DebitReasonDN> = PagedListDN()
    var workshopDebits: PagedListDN<WorkShopDebtDN> = PagedListDN()
    var demandDocuments: PagedListDN<WorkshopDemandDocDN> = PagedListDN()
    var objectionableDebits: PagedListDN<WorkShopDebtDN> = PagedListDN()
    var articleSixteenDebts: PagedListDN<WorkshopsDebtListModelDN> = PagedListDN()
    var members: PagedListDN<WorkshopMemberDN> = PagedListDN()
    var stackHolders: PagedListDN<WorkshopStackHolderDN> = PagedListDN()
    var recentlyAddedMembers: PagedListDN<WorkshopNewMemberDN> = PagedListDN()

    var debtInquiry: WorkshopDebtInquiryDN = WorkshopDebtInquiryDN()
    var paymentPreCheck: DebitPaymentPreCheckDN = DebitPaymentPreCheckDN()
    var paymentResult: DebitPaymentDN = DebitPaymentDN()
    var objectionElapsedDays: Int = 0
    var objectionResult: DebitObjectionResultDN = DebitObjectionResultDN()
    var articleSixteenWorkshopInfo: ArticleSixteenWorkshopInfoDN = ArticleSixteenWorkshopInfoDN()
    var articleSixteenRequestInfo: ArticleSixteenRequestInfoDN = ArticleSixteenRequestInfoDN()
    var articleSixteenSaveResult: ArticleSixteenSaveResultDN = ArticleSixteenSaveResultDN()
    var pdf: PdfDownloadDN = PdfDownloadDN()
    var confirmReferenceCode: String = ""

    /** Set to make the next call throw instead of answering. */
    var error: Throwable? = null

    var lastWorkshopListQuery: WorkshopListQuery? = null
        private set
    var lastPaymentSheetQuery: PaymentSheetQuery? = null
        private set
    var lastMemberQuery: WorkshopMemberQuery? = null
        private set
    var lastStackHolderQuery: WorkshopStackHolderQuery? = null
        private set
    var lastNewMemberQuery: WorkshopNewMemberQuery? = null
        private set
    var lastArticleSixteenQuery: ArticleSixteenDebtQuery? = null
        private set
    var lastPaymentRequest: DebitPaymentRequestDN? = null
        private set
    var deletedPersonalId: Long? = null
    var newMemberIsNew: NewMemberIsNewDN = NewMemberIsNewDN()
    var registrationResult: NewMemberRegistrationResultDN = NewMemberRegistrationResultDN()
    var lastRegistrationRequest: NewMemberRegistrationDN? = null
        private set

    override suspend fun getEmployerAgreements(
        query: WorkshopListQuery,
    ): PagedListDN<EmployerAgreementDN> = answer {
        lastWorkshopListQuery = query
        employerAgreements
    }

    override suspend fun getPaymentSheets(query: PaymentSheetQuery): PagedListDN<PaymentSheetDN> =
        answer {
            lastPaymentSheetQuery = query
            paymentSheets
        }

    override suspend fun getDebitReasons(page: Int): PagedListDN<DebitReasonDN> =
        answer { debitReasons }

    override suspend fun getWorkshopDebits(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkShopDebtDN> = answer { workshopDebits }

    override suspend fun getDemandDocuments(
        debitNumber: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkshopDemandDocDN> = answer { demandDocuments }

    override suspend fun getDebitTurnoverPdf(
        debitNumber: String,
        branchCode: String,
    ): PdfDownloadDN = answer { pdf }

    override suspend fun checkDebitPayment(
        debitNumber: String,
        branchCode: String,
    ): DebitPaymentPreCheckDN = answer { paymentPreCheck }

    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDN): DebitPaymentDN = answer {
        lastPaymentRequest = request
        paymentResult
    }

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String,
    ): WorkshopDebtInquiryDN = answer { debtInquiry }

    override suspend fun getObjectionableDebits(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkShopDebtDN> = answer { objectionableDebits }

    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int =
        answer { objectionElapsedDays }

    override suspend fun saveDebitObjection(
        request: DebitObjectionRequestDN,
    ): DebitObjectionResultDN = answer { objectionResult }

    override suspend fun getDebitObjectionPdf(seqNo: Long): PdfDownloadDN = answer { pdf }

    override suspend fun getRecentlyAddedMembers(
        query: WorkshopNewMemberQuery,
    ): PagedListDN<WorkshopNewMemberDN> = answer {
        lastNewMemberQuery = query
        recentlyAddedMembers
    }

    override suspend fun confirmRecentlyAddedMember(requestId: Long): String =
        answer { confirmReferenceCode }

    override suspend fun deleteRecentlyAddedMember(personalId: Long) {
        answer { deletedPersonalId = personalId }
    }

    override suspend fun checkNewMemberIsNew(nationalId: String): NewMemberIsNewDN =
        answer { newMemberIsNew }

    override suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDN,
    ): NewMemberRegistrationResultDN = answer {
        lastRegistrationRequest = request
        registrationResult
    }

    override suspend fun getArticleSixteenDebts(
        query: ArticleSixteenDebtQuery,
    ): PagedListDN<WorkshopsDebtListModelDN> = answer {
        lastArticleSixteenQuery = query
        articleSixteenDebts
    }

    override suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String,
    ): ArticleSixteenWorkshopInfoDN = answer { articleSixteenWorkshopInfo }

    override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDN =
        answer { articleSixteenRequestInfo }

    override suspend fun saveArticleSixteenRequest(
        request: ArticleSixteenSaveRequestDN,
    ): ArticleSixteenSaveResultDN = answer { articleSixteenSaveResult }

    override suspend fun getArticleSixteenReportPdf(seqNo: Long): PdfDownloadDN = answer { pdf }

    override suspend fun getWorkshopMembers(
        query: WorkshopMemberQuery,
    ): PagedListDN<WorkshopMemberDN> = answer {
        lastMemberQuery = query
        members
    }

    override suspend fun getWorkshopStackHolders(
        query: WorkshopStackHolderQuery,
    ): PagedListDN<WorkshopStackHolderDN> = answer {
        lastStackHolderQuery = query
        stackHolders
    }

    private inline fun <T> answer(block: () -> T): T {
        error?.let { throw it }
        return block()
    }
}

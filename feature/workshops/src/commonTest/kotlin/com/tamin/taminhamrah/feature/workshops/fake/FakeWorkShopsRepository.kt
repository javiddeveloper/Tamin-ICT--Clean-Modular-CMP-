package com.tamin.taminhamrah.feature.workshops.fake

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
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
import com.tamin.taminhamrah.model.workshop.ContractRowQuery
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractQuery
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBaseQuery
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetQuery
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionQuery
import com.tamin.taminhamrah.model.workshop.SmsMessageDN
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDN
import com.tamin.taminhamrah.model.workshop.SettlementRequestDN
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A workshops repository a test can drive.
 *
 * Each call records the query it was handed and answers from a field, so a test can assert on what
 * was *asked* as well as on what came back — which is where the interesting failures live: the
 * filter that quietly went missing, the page that was requested twice.
 */
class FakeWorkShopsRepository : WorkShopsRepository {

    var employerAgreements: PagedListDN<EmployerAgreementDN> = PagedListDN()
    var contractRowsWithAgreement: PagedListDN<EmployerAgreementDN> = PagedListDN()
    var contractRowsWithoutAgreement: PagedListDN<WorkshopContractDN> = PagedListDN()
    var paymentSheets: PagedListDN<PaymentSheetDN> = PagedListDN()
    var debitReasons: PagedListDN<DebitReasonDN> = PagedListDN()
    var workshopDebits: PagedListDN<WorkShopDebtDN> = PagedListDN()
    var demandDocuments: PagedListDN<WorkshopDemandDocDN> = PagedListDN()
    var objectionableDebits: PagedListDN<WorkShopDebtDN> = PagedListDN()
    var articleSixteenDebts: PagedListDN<WorkshopsDebtListModelDN> = PagedListDN()
    var members: PagedListDN<WorkshopMemberDN> = PagedListDN()
    var stackHolders: PagedListDN<WorkshopStackHolderDN> = PagedListDN()
    var recentlyAddedMembers: PagedListDN<WorkshopNewMemberDN> = PagedListDN()
    var workshopsWithoutContract: PagedListDN<WorkshopWithoutContractDN> = PagedListDN()
    var workshopContractRows: PagedListDN<WorkshopContractRowDN> = PagedListDN()
    var workShopObjections: PagedListDN<WorkShopObjectionDN> = PagedListDN()
    var objectionSms: PagedListDN<SmsMessageDN> = PagedListDN()

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
    var employerContactInfo: EmployerContactInfoDN = EmployerContactInfoDN()
    var ticketRequestMessage: String = ""
    var employerAgreementSubmitMessage: String = ""
    var lastEmployerAgreementSubmission: EmployerAgreementSubmissionDN? = null
        private set

    /** Set to make the next call throw instead of answering. */
    var error: Throwable? = null

    var lastWorkshopListQuery: WorkshopListQuery? = null
        private set
    var lastPaymentSheetQuery: PaymentSheetQuery? = null
        private set
    var lastContractRowQuery: ContractRowQuery? = null
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
    var confirmedTicket: String? = null
        private set
    var deletedPersonalId: Long? = null
    var confirmedRequestId: Long? = null
        private set
    var newMemberIsNew: Boolean = true
    var registrationResult: NewMemberRegistrationResultDN = NewMemberRegistrationResultDN()
    var lastRegistrationRequest: NewMemberRegistrationDN? = null
        private set
    var lastWorkShopObjectionQuery: WorkShopObjectionQuery? = null
        private set
    var lastObjectionSmsSeqNo: Long? = null
        private set
    var lastDebitObjectionPdfSeqNo: Long? = null
        private set
    var lastArticleSixteenReportPdfSeqNo: Long? = null
        private set

    override suspend fun getEmployerAgreements(
        query: WorkshopListQuery,
    ): PagedListDN<EmployerAgreementDN> = answer {
        lastWorkshopListQuery = query
        employerAgreements
    }

    override suspend fun getContractRowsWithAgreement(
        query: ContractRowQuery,
    ): PagedListDN<EmployerAgreementDN> = answer {
        lastContractRowQuery = query
        contractRowsWithAgreement
    }

    override suspend fun getContractRowsWithoutAgreement(
        query: ContractRowQuery,
    ): PagedListDN<WorkshopContractDN> = answer {
        lastContractRowQuery = query
        contractRowsWithoutAgreement
    }

    // ------------------------------------------------------------------------ واگذارندگان

    var assignerContracts: PagedListDN<AssignerContractDN> = PagedListDN()

    /** One answer per page, for the paging tests; a page with no entry answers [assignerContracts]. */
    var assignerContractPages: Map<Int, PagedListDN<AssignerContractDN>> = emptyMap()
    var lastAssignerContractQuery: AssignerContractQuery? = null
    val assignerContractQueries = mutableListOf<AssignerContractQuery>()
    var computationalBases: PagedListDN<ComputationalBaseDN> = PagedListDN()
    var lastComputationalBaseQuery: ComputationalBaseQuery? = null
    var computationalBasePdf: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var lastPdfDocumentId: String? = null

    override suspend fun getAssignerContracts(
        query: AssignerContractQuery,
    ): PagedListDN<AssignerContractDN> = answer {
        lastAssignerContractQuery = query
        assignerContractQueries += query
        assignerContractPages[query.page] ?: assignerContracts
    }

    override suspend fun getComputationalBases(
        query: ComputationalBaseQuery,
    ): PagedListDN<ComputationalBaseDN> = answer {
        lastComputationalBaseQuery = query
        computationalBases
    }

    override suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDN = answer {
        lastPdfDocumentId = documentId
        computationalBasePdf
    }

    // --------------------------------------------------------------- درخواست مفاصاحساب

    var settlementSubjects: List<SettlementSubjectDN> = emptyList()
    var settlementPdfId: String = "pdf-id"
    var settlementSubmitMessage: String = ""
    var lastSettlementPdfName: String? = null
        private set
    var lastSettlementRequest: SettlementRequestDN? = null
        private set

    override suspend fun getSettlementSubjects(): List<SettlementSubjectDN> =
        answer { settlementSubjects }

    override suspend fun uploadSettlementPdf(fileName: String, bytes: ByteArray): String = answer {
        lastSettlementPdfName = fileName
        settlementPdfId
    }

    override suspend fun submitSettlementRequest(request: SettlementRequestDN): String = answer {
        lastSettlementRequest = request
        settlementSubmitMessage
    }

    var settlementCertificate: SettlementCertificateDN? = null

    /** workshopId, branchCode, contractRow, contractNumber — as the last certificate call sent them. */
    var lastCertificateArgs: List<String>? = null
        private set

    override suspend fun getSettlementCertificate(
        workshopId: String,
        branchCode: String,
        contractRow: String,
        contractNumber: String,
    ): SettlementCertificateDN? = answer {
        lastCertificateArgs = listOf(workshopId, branchCode, contractRow, contractNumber)
        settlementCertificate
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

    override suspend fun confirmPaymentTicket(ticket: String) {
        answer { confirmedTicket = ticket }
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

    /** What was actually filed, so a test can tell «asked to confirm» from «sent». */
    var lastDebitObjectionRequest: DebitObjectionRequestDN? = null
        private set

    override suspend fun saveDebitObjection(
        request: DebitObjectionRequestDN,
    ): DebitObjectionResultDN = answer {
        lastDebitObjectionRequest = request
        objectionResult
    }

    var debitObjectionPdfCallCount: Int = 0
        private set
    var debitObjectionPdfGate: kotlinx.coroutines.CompletableDeferred<Unit>? = null

    override suspend fun getDebitObjectionPdf(seqNo: Long): PdfDownloadDN {
        error?.let { throw it }
        lastDebitObjectionPdfSeqNo = seqNo
        debitObjectionPdfCallCount++
        debitObjectionPdfGate?.await()
        return pdf
    }

    override suspend fun getRecentlyAddedMembers(
        query: WorkshopNewMemberQuery,
    ): PagedListDN<WorkshopNewMemberDN> = answer {
        lastNewMemberQuery = query
        recentlyAddedMembers
    }

    override suspend fun confirmRecentlyAddedMember(requestId: Long): String = answer {
        confirmedRequestId = requestId
        confirmReferenceCode
    }

    override suspend fun deleteRecentlyAddedMember(personalId: Long) {
        answer { deletedPersonalId = personalId }
    }

    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean =
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

    override suspend fun getArticleSixteenReportPdf(seqNo: Long): PdfDownloadDN = answer {
        lastArticleSixteenReportPdfSeqNo = seqNo
        pdf
    }

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

    override suspend fun getWorkShopObjections(
        query: WorkShopObjectionQuery,
    ): PagedListDN<WorkShopObjectionDN> = answer {
        lastWorkShopObjectionQuery = query
        workShopObjections
    }

    override suspend fun getWorkShopObjectionSms(
        seqNo: Long,
        page: Int,
    ): PagedListDN<SmsMessageDN> = answer {
        lastObjectionSmsSeqNo = seqNo
        objectionSms
    }

    override suspend fun requestEmployerAgreementTicket(mobile: String, email: String): String =
        answer { ticketRequestMessage }

    override suspend fun getEmployerAgreementContactInfo(
        verificationCode: String,
    ): EmployerContactInfoDN = answer { employerContactInfo }

    override suspend fun getWorkshopsWithoutContract(
        page: Int,
    ): PagedListDN<WorkshopWithoutContractDN> = answer { workshopsWithoutContract }

    override suspend fun getWorkshopContractRows(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkshopContractRowDN> = answer { workshopContractRows }

    override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmissionDN): String =
        answer {
            lastEmployerAgreementSubmission = request
            employerAgreementSubmitMessage
        }

    private inline fun <T> answer(block: () -> T): T {
        error?.let { throw it }
        return block()
    }

    var legalRepresentativeWorkshopsResult: LegalRepresentativeWorkshopListDN? = null
    override fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?> = flow {
        emit(answer { legalRepresentativeWorkshopsResult })
    }

    var legalRepresentativesResult: LegalRepresentativeListDN? = null
    override fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?> = flow {
        emit(answer { legalRepresentativesResult })
    }

    var legalRepresentativeWorkshopContractsResult: LegalRepresentativeContractListDN? = null
    override fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeContractListDN?> = flow {
        emit(answer { legalRepresentativeWorkshopContractsResult })
    }

    var requestTicketCallCount: Int = 0
        private set
    var lastRequestTicketNationalCode: String? = null
        private set
    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) {
        answer {
            requestTicketCallCount++
            lastRequestTicketNationalCode = nationalCode
        }
    }

    var lastVerifiedTicket: String? = null
        private set
    override suspend fun verifyLegalRepresentativeTicket(ticket: String) {
        answer { lastVerifiedTicket = ticket }
    }

    var lastSubmittedTicket: String? = null
        private set
    var lastSubmittedRequest: LegalRepresentativeRequestDN? = null
        private set
    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN) {
        answer {
            lastSubmittedTicket = ticket
            lastSubmittedRequest = request
        }
    }

    override suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long) {
        answer { }
    }
}

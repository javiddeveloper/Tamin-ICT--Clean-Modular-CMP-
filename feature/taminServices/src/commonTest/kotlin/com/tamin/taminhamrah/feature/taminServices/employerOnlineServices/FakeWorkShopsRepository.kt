package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.*
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

/**
 * A [WorkShopsRepository] scoped to what [com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesViewModel]
 * actually calls — the six خدمات غیرحضوری کارفرما endpoints. Every other method of the (large,
 * remote-only) interface throws, so a test that accidentally exercises one fails loudly rather than
 * passing on a silent default.
 */
class FakeWorkShopsRepository : WorkShopsRepository {

    var agreements: PagedListDN<EmployerAgreementDN> = PagedListDN()
    var contractRows: PagedListDN<WorkshopContractRowDN> = PagedListDN()
    var workshopsWithoutContract: PagedListDN<WorkshopWithoutContractDN> = PagedListDN()
    var contactInfo: EmployerContactInfoDN = EmployerContactInfoDN()
    var ticketMessage: String = "ارسال شد"
    var submitMessage: String = "ثبت شد"

    /** Which single call should fail; leave null for the happy path. */
    var failing: Call? = null

    var lastTicketRequest: Pair<String, String>? = null
        private set
    var lastVerificationCode: String? = null
        private set
    var lastContractRowsArgs: Triple<String, String, Int>? = null
        private set
    var lastSubmission: EmployerAgreementSubmissionDN? = null
        private set
    var lastAgreementsQuery: WorkshopListQuery? = null
        private set
    var agreementsQueryCount: Int = 0
        private set
    var lastWorkshopsWithoutContractPage: Int? = null
        private set

    enum class Call { AGREEMENTS, CONTACT_INFO, WORKSHOPS_WITHOUT_CONTRACT, CONTRACT_ROWS, TICKET, SUBMIT }

    private fun failIf(call: Call) {
        if (failing == call) throw RuntimeException("boom: $call")
    }

    /**
     * Filters and slices [agreements] the way the real service would: `workshopId`/`branchCode`
     * narrow the set, then `page`/`pageSize` window it — so a test can drive a real search + a real
     * "load more" through the ViewModel's [com.tamin.taminhamrah.paging.Paginator], not just assert on
     * a canned single response.
     *
     * When unfiltered, [PagedListDN.total] on [agreements] is honored as-is (a test may set it above
     * `items.size` to simulate more server-side rows than were stubbed); a filtered query reports the
     * filtered count instead, matching what a real search would answer.
     */
    override suspend fun getEmployerAgreements(query: WorkshopListQuery): PagedListDN<EmployerAgreementDN> {
        failIf(Call.AGREEMENTS)
        lastAgreementsQuery = query
        agreementsQueryCount++

        val isFiltered = !query.workshopId.isNullOrBlank() || !query.branchCode.isNullOrBlank()
        val filtered = agreements.items.filter { item ->
            (query.workshopId.isNullOrBlank() || item.workshop.workshopId == query.workshopId) &&
                (query.branchCode.isNullOrBlank() || item.workshop.branchCode == query.branchCode)
        }

        val fromIndex = (query.page * query.pageSize).coerceIn(0, filtered.size)
        val toIndex = (fromIndex + query.pageSize).coerceIn(fromIndex, filtered.size)

        return PagedListDN(
            items = filtered.subList(fromIndex, toIndex),
            total = if (isFiltered) filtered.size else agreements.total,
        )
    }
    override suspend fun requestEmployerAgreementTicket(mobile: String, email: String): String {
        lastTicketRequest = mobile to email
        failIf(Call.TICKET)
        return ticketMessage
    }
    override suspend fun getContractRowsWithAgreement(query: ContractRowQuery): PagedListDN<EmployerAgreementDN> =
        PagedListDN()

    override suspend fun getContractRowsWithoutAgreement(query: ContractRowQuery): PagedListDN<WorkshopContractDN> = PagedListDN()

    override suspend fun getEmployerAgreementContactInfo(verificationCode: String): EmployerContactInfoDN {
        lastVerificationCode = verificationCode
        failIf(Call.CONTACT_INFO)
        return contactInfo
    }

    override suspend fun getWorkshopsWithoutContract(page: Int): PagedListDN<WorkshopWithoutContractDN> {
        failIf(Call.WORKSHOPS_WITHOUT_CONTRACT)
        lastWorkshopsWithoutContractPage = page
        return workshopsWithoutContract.page(page)
    }

    override suspend fun getWorkshopContractRows(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkshopContractRowDN> {
        lastContractRowsArgs = Triple(workshopId, branchCode, page)
        failIf(Call.CONTRACT_ROWS)
        return contractRows.page(page)
    }
    private fun <T> PagedListDN<T>.page(page: Int): PagedListDN<T> {
        val fromIndex = (page * WORKSHOP_PAGE_SIZE).coerceIn(0, items.size)
        val toIndex = (fromIndex + WORKSHOP_PAGE_SIZE).coerceIn(fromIndex, items.size)
        return PagedListDN(items = items.subList(fromIndex, toIndex), total = total)
    }

    override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmissionDN): String {
        lastSubmission = request
        failIf(Call.SUBMIT)
        return submitMessage
    }

    // --------------------------------------------------------- not used by this ViewModel

    private fun notUsed(): Nothing = error("not used by EmployerOnlineServicesViewModel tests")

    override suspend fun getWorkShopObjections(query: WorkShopObjectionQuery) = notUsed()
    override suspend fun getWorkShopObjectionSms(seqNo: Long, page: Int) = notUsed()
    override suspend fun confirmPaymentTicket(ticket: String) = notUsed()
    override suspend fun getPaymentSheets(query: PaymentSheetQuery): PagedListDN<PaymentSheetDN> = notUsed()
    override suspend fun getAssignerContracts(query: AssignerContractQuery): PagedListDN<AssignerContractDN> = notUsed()
    override suspend fun getComputationalBases(query: ComputationalBaseQuery): PagedListDN<ComputationalBaseDN> = notUsed()
    override suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDN = notUsed()
    override suspend fun getDebitReasons(page: Int): PagedListDN<DebitReasonDN> = notUsed()
    override suspend fun getWorkshopDebits(workshopId: String, branchCode: String, page: Int): PagedListDN<WorkShopDebtDN> = notUsed()
    override suspend fun getDemandDocuments(debitNumber: String, branchCode: String, page: Int): PagedListDN<WorkshopDemandDocDN> = notUsed()
    override suspend fun getDebitTurnoverPdf(debitNumber: String, branchCode: String): PdfDownloadDN = notUsed()
    override suspend fun checkDebitPayment(debitNumber: String, branchCode: String): DebitPaymentPreCheckDN = notUsed()
    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDN): DebitPaymentDN = notUsed()
    override suspend fun getWorkshopDebtInquiry(workshopId: String, branchCode: String): WorkshopDebtInquiryDN = notUsed()
    override suspend fun getObjectionableDebits(workshopId: String, branchCode: String, page: Int): PagedListDN<WorkShopDebtDN> = notUsed()
    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int = notUsed()
    override suspend fun saveDebitObjection(request: DebitObjectionRequestDN): DebitObjectionResultDN = notUsed()
    override suspend fun getDebitObjectionPdf(seqNo: Long): PdfDownloadDN = notUsed()
    override suspend fun getRecentlyAddedMembers(query: WorkshopNewMemberQuery): PagedListDN<WorkshopNewMemberDN> = notUsed()
    override suspend fun confirmRecentlyAddedMember(requestId: Long): String = notUsed()
    override suspend fun deleteRecentlyAddedMember(personalId: Long) = notUsed()
    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean = notUsed()
    override suspend fun createNewMemberRegistration(request: NewMemberRegistrationDN): NewMemberRegistrationResultDN = notUsed()
    override suspend fun getArticleSixteenDebts(query: ArticleSixteenDebtQuery): PagedListDN<WorkshopsDebtListModelDN> = notUsed()
    override suspend fun getArticleSixteenWorkshopInfo(workshopId: String, branchCode: String): ArticleSixteenWorkshopInfoDN = notUsed()
    override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDN = notUsed()
    override suspend fun saveArticleSixteenRequest(request: ArticleSixteenSaveRequestDN): ArticleSixteenSaveResultDN = notUsed()
    override suspend fun getArticleSixteenReportPdf(seqNo: Long): PdfDownloadDN = notUsed()
    override suspend fun getWorkshopMembers(query: WorkshopMemberQuery): PagedListDN<WorkshopMemberDN> = notUsed()
    override suspend fun getWorkshopStackHolders(query: WorkshopStackHolderQuery): PagedListDN<WorkshopStackHolderDN> = notUsed()

    override fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?> = notUsed()
    override fun getLegalRepresentatives(workshopId: String, branchCode: String): Flow<LegalRepresentativeListDN?> = notUsed()
    override fun getLegalRepresentativeWorkshopContracts(workshopId: String, branchCode: String): Flow<LegalRepresentativeContractListDN?> = notUsed()
    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) = notUsed()
    override suspend fun verifyLegalRepresentativeTicket(ticket: String) = notUsed()
    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN) = notUsed()
    override suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long) = notUsed()
}

package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDTO
import com.tamin.taminhamrah.model.workshop.AssignerContractDTO
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitReasonDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmitRequestDTO
import com.tamin.taminhamrah.model.workshop.EmployerCommitmentInfoDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.NewMemberConfirmResultDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDTO
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDetailDTO
import com.tamin.taminhamrah.model.workshop.SettlementRequestDTO
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDTO
import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO

interface WorkShopsRemoteDataSource {

    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO>

    /** ردیف پیمانهای one workshop that has a تعهدنامه. */
    suspend fun getEmployerAgreementsByWorkshop(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN,
    ): ListData<EmployerAgreementDTO>

    /** ردیف پیمانهای one workshop with no تعهدنامه — a different row model. */
    suspend fun getWorkshopContracts(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN,
    ): ListData<WorkshopContractDTO>

    // ---------------------------------------------------------------------------- واگذارندگان

    /**
     * پیمانهایی که کارفرما واگذارندهٔ آنهاست.
     *
     * Unlike the two calls above, the identity travels in [query]'s filter array rather than in the
     * path — the repository builds those clauses, so a blank code widens the result instead of
     * addressing a different route.
     */
    suspend fun getAssignerContracts(query: ApiQueryParamDN): ListData<AssignerContractDTO>

    /**
     * مبانی محاسباتی of one پیمان.
     *
     * All four keys are required by the caller, not by the wire: the service answers a partial set
     * with every base it holds for the workshop, which is a different record than the one the user
     * tapped. See `ComputationalBaseQuery` for where the four come from.
     */
    suspend fun getComputationalBases(
        workshopId: String,
        contractRow: String,
        brchCode: String,
        contractSequence: String,
        query: ApiQueryParamDN,
    ): ListData<ComputationalBaseDTO>

    /** A PDF document attached to a مبنای محاسباتی, drained to bytes before the response closes. */
    suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDTO

    /** موضوعات کار a درخواست مفاصاحساب can be filed under. */
    suspend fun getSettlementSubjects(query: ApiQueryParamDN): ListData<SettlementSubjectDTO>

    /** Stores one PDF of a درخواست مفاصاحساب; returns the id the request names it by. */
    suspend fun uploadSettlementPdf(fileName: String, bytes: ByteArray): String

    /** Files a درخواست مفاصاحساب under [id]; returns the service's confirmation. */
    suspend fun submitSettlementRequest(id: String, request: SettlementRequestDTO): String

    /** مفاصاحسابهای ماده ۳۸ of one ردیف پیمان, as the service lists them. */
    suspend fun getSettlementCertificates(
        workshopId: String,
        branchCode: String,
        contractRow: String,
        query: ApiQueryParamDN,
    ): ListData<SettlementCertificateDTO>

    /** The certificate [getSettlementCertificates] listed under [serial]. */
    suspend fun getSettlementCertificateDetail(
        workshopId: String,
        branchCode: String,
        contractRow: String,
        serial: String,
        query: ApiQueryParamDN,
    ): ListData<SettlementCertificateDetailDTO>

    suspend fun getWorkshopPaymentSheets(
        query: ApiQueryParamDN
    ): ListData<PaymentSheetDTO>

    suspend fun getDebitReasons(
        query: ApiQueryParamDN
    ): ListData<DebitReasonDTO>

    suspend fun getWorkshopDebitList(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkShopDebtDTO>

    suspend fun getWorkshopDemandDocuments(
        debitNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopDemandDocDTO>

    suspend fun getDebitTurnoverPdf(
        debitNumber: String,
        branchCode: String
    ): PdfDownloadDTO

    suspend fun checkDebitPayment(
        debitNumber: String,
        branchCode: String
    ): DebitPaymentPreCheckDTO

    suspend fun payWorkshopDebit(request: DebitPaymentRequestDTO): DebitPaymentDTO

    suspend fun confirmPaymentTicket(ticket: String)

    suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDTO

    suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkShopDebtDTO>

    suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int

    suspend fun saveDebitObjection(
        request: DebitObjectionSaveRequestDTO
    ): DebitObjectionSaveResultDTO

    suspend fun getDebitObjectionPdf(seqNumber: Long): PdfDownloadDTO

    suspend fun getWorkshopRecentlyAddedMembers(
        query: ApiQueryParamDN
    ): ListData<WorkshopNewMemberDTO>

    suspend fun confirmRecentlyAddedMember(requestId: Long): NewMemberConfirmResultDTO

    suspend fun deleteRecentlyAddedMember(personalId: Long)

    suspend fun checkNewMemberIsNew(nationalId: String): Boolean

    suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDTO,
    ): NewMemberRegistrationResultDTO

    suspend fun updateNewMemberRegistration(
        personalId: Long,
        request: NewMemberRegistrationDTO,
    ): NewMemberRegistrationResultDTO

    suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopsDebtListModelDTO>

    suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String
    ): ArticleSixteenWorkshopInfoDTO

    suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDTO

    suspend fun saveArticleSixteenRequest(request: ArticleSixteenSaveRequestDTO): ArticleSixteenSaveResultDTO

    suspend fun getArticleSixteenReportPdf(seqNumber: Long): PdfDownloadDTO

    suspend fun getWorkshopMembers(
        query: ApiQueryParamDN
    ): ListData<WorkshopMemberDTO>

    suspend fun getWorkshopStackHolders(
        query: ApiQueryParamDN
    ): ListData<WorkshopStackHolderDTO>

    suspend fun getLegalRepresentativeWorkshops(): ListData<LegalRepresentativeWorkshopDTO>?

    suspend fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeDTO>?

    suspend fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeContractDTO>?

    suspend fun requestLegalRepresentativeTicket(nationalCode: String?)

    suspend fun verifyLegalRepresentativeTicket(ticket: String)

    suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDTO)

    suspend fun deleteLegalRepresentative(ticket: String, stackId: Long)

    // ------------------------------------------------- خدمات غیرحضوری کارفرما (employerEservicesAgreement)

    /**
     * Step 1 — request the OTP ticket for registering an employer online-services agreement.
     * Builds the `mobileNumber` / `email` / `serviceName` filter internally. Returns the backend's
     * bare confirmation message.
     */
    suspend fun requestEmployerAgreementTicket(
        mobileNumber: String,
        email: String,
    ): String

    /** Step 2 — exchange the entered OTP for the employer's identity block. */
    suspend fun getEmployerAgreementUserInfo(
        verificationCode: String,
    ): EmployerCommitmentInfoDTO

    /** Step 2 — paged list of the employer's workshops that have no contract yet. */
    suspend fun getEmployerWorkshopsWithoutContract(
        query: ApiQueryParamDN
    ): ListData<WorkshopWithoutContractDTO>

    /** Contract / پیمانکار rows of one workshop (by workshop + branch code). */
    suspend fun getEmployerWorkshopContractList(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopContractRowDTO>

    /** Step 3 — submit the final agreement. Returns the backend's bare success message. */
    suspend fun submitEmployerAgreement(
        request: EmployerAgreementSubmitRequestDTO,
    ): String

    suspend fun getWorkShopObjections(query: ApiQueryParamDN): ListData<WorkShopObjectionDTO>

    suspend fun getWorkShopObjectionSms(
        objectionCode: Long,
        query: ApiQueryParamDN,
    ): ListData<SmsMessageDTO>
}

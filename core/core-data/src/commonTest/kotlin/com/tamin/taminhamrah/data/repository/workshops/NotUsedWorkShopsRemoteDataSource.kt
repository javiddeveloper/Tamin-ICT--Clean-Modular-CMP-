package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.*

/**
 * Every [WorkShopsRemoteDataSource] method, throwing.
 *
 * `WorkShopsRemoteDataSource` is large and remote-only, and a test normally exercises three or four
 * of its calls. Subclassing this and overriding just those keeps a fake honest — an unintended call
 * fails loudly instead of returning a silent default — without every suite carrying its own wall of
 * stubs, and it means widening the interface is one edit here rather than one per test.
 */
internal abstract class NotUsedWorkShopsRemoteDataSource : WorkShopsRemoteDataSource {

    protected fun notUsed(): Nothing =
        error("this WorkShopsRemoteDataSource method is not exercised by the test that reached it")

    override suspend fun getAllEmployerAgreementByNationalId(query: ApiQueryParamDN): ListData<EmployerAgreementDTO> = notUsed()
    override suspend fun getEmployerWorkshopsWithoutContract(query: ApiQueryParamDN): ListData<WorkshopWithoutContractDTO> = notUsed()
    override suspend fun getEmployerWorkshopContractList(workshopId: String, branchCode: String, query: ApiQueryParamDN): ListData<WorkshopContractRowDTO> = notUsed()
    override suspend fun getEmployerAgreementUserInfo(verificationCode: String): EmployerCommitmentInfoDTO = notUsed()
    override suspend fun requestEmployerAgreementTicket(mobileNumber: String, email: String): String = notUsed()
    override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmitRequestDTO): String = notUsed()
    override suspend fun getEmployerAgreementsByWorkshop(workshopId: String, branchCode: String, query: ApiQueryParamDN): ListData<EmployerAgreementDTO> = notUsed()
    override suspend fun getWorkshopContracts(workshopId: String, branchCode: String, query: ApiQueryParamDN): ListData<WorkshopContractDTO> = notUsed()
    override suspend fun getAssignerContracts(query: ApiQueryParamDN): ListData<AssignerContractDTO> = notUsed()
    override suspend fun getComputationalBases(workshopId: String, contractRow: String, brchCode: String, contractSequence: String, query: ApiQueryParamDN): ListData<ComputationalBaseDTO> = notUsed()
    override suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDTO = notUsed()
    override suspend fun getSettlementSubjects(query: ApiQueryParamDN): ListData<SettlementSubjectDTO> = notUsed()
    override suspend fun uploadSettlementPdf(fileName: String, bytes: ByteArray): String = notUsed()
    override suspend fun submitSettlementRequest(id: String, request: SettlementRequestDTO): String = notUsed()
    override suspend fun getSettlementCertificates(workshopId: String, branchCode: String, contractRow: String, query: ApiQueryParamDN): ListData<SettlementCertificateDTO> = notUsed()
    override suspend fun getSettlementCertificateDetail(workshopId: String, branchCode: String, contractRow: String, serial: String, query: ApiQueryParamDN): ListData<SettlementCertificateDetailDTO> = notUsed()
    override suspend fun getWorkshopPaymentSheets(query: ApiQueryParamDN): ListData<PaymentSheetDTO> = notUsed()
    override suspend fun getDebitReasons(query: ApiQueryParamDN): ListData<DebitReasonDTO> = notUsed()
    override suspend fun getWorkshopDebitList(workshopId: String, branchCode: String, query: ApiQueryParamDN): ListData<WorkShopDebtDTO> = notUsed()
    override suspend fun getWorkshopDemandDocuments(debitNumber: String, branchCode: String, query: ApiQueryParamDN): ListData<WorkshopDemandDocDTO> = notUsed()
    override suspend fun getDebitTurnoverPdf(debitNumber: String, branchCode: String): PdfDownloadDTO = notUsed()
    override suspend fun checkDebitPayment(debitNumber: String, branchCode: String): DebitPaymentPreCheckDTO = notUsed()
    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDTO): DebitPaymentDTO = notUsed()
    override suspend fun confirmPaymentTicket(ticket: String) = notUsed()
    override suspend fun getWorkshopDebtInquiry(workshopId: String, branchCode: String): WorkshopDebtInquiryDTO = notUsed()
    override suspend fun getWorkshopObjectionableDebitList(workshopNumber: String, branchCode: String, query: ApiQueryParamDN): ListData<WorkShopDebtDTO> = notUsed()
    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int = notUsed()
    override suspend fun saveDebitObjection(request: DebitObjectionSaveRequestDTO): DebitObjectionSaveResultDTO = notUsed()
    override suspend fun getDebitObjectionPdf(seqNumber: Long): PdfDownloadDTO = notUsed()
    override suspend fun getWorkShopObjections(query: ApiQueryParamDN): ListData<WorkShopObjectionDTO> = notUsed()
    override suspend fun getWorkShopObjectionSms(objectionCode: Long, query: ApiQueryParamDN): ListData<SmsMessageDTO> = notUsed()
    override suspend fun getWorkshopRecentlyAddedMembers(query: ApiQueryParamDN): ListData<WorkshopNewMemberDTO> = notUsed()
    override suspend fun confirmRecentlyAddedMember(requestId: Long): NewMemberConfirmResultDTO = notUsed()
    override suspend fun deleteRecentlyAddedMember(personalId: Long) = notUsed()
    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean = notUsed()
    override suspend fun createNewMemberRegistration(request: NewMemberRegistrationDTO): NewMemberRegistrationResultDTO = notUsed()
    override suspend fun getWorkshopsDebtsList(workshopId: String, branchId: String, query: ApiQueryParamDN): ListData<WorkshopsDebtListModelDTO> = notUsed()
    override suspend fun getArticleSixteenWorkshopInfo(workshopId: String, branchCode: String): ArticleSixteenWorkshopInfoDTO = notUsed()
    override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDTO = notUsed()
    override suspend fun saveArticleSixteenRequest(request: ArticleSixteenSaveRequestDTO): ArticleSixteenSaveResultDTO = notUsed()
    override suspend fun getArticleSixteenReportPdf(seqNumber: Long): PdfDownloadDTO = notUsed()
    override suspend fun getWorkshopMembers(query: ApiQueryParamDN): ListData<WorkshopMemberDTO> = notUsed()
    override suspend fun getWorkshopStackHolders(query: ApiQueryParamDN): ListData<WorkshopStackHolderDTO> = notUsed()
    override suspend fun getLegalRepresentativeWorkshops(): ListData<LegalRepresentativeWorkshopDTO> = notUsed()

    override suspend fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeDTO> = notUsed()

    override suspend fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeContractDTO> = notUsed()

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) = notUsed()

    override suspend fun verifyLegalRepresentativeTicket(ticket: String) = notUsed()

    override suspend fun submitLegalRepresentative(
        ticket: String,
        request: LegalRepresentativeRequestDTO
    )  = notUsed()

    override suspend fun deleteLegalRepresentative(
        ticket: String,
        stackId: Long
    ) = notUsed()
}

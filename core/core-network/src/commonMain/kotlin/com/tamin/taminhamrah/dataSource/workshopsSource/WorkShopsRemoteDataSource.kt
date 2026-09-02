package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitReasonDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.NewMemberConfirmResultDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO

interface WorkShopsRemoteDataSource {

    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO>

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
}

package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
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
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import kotlinx.coroutines.flow.Flow

/**
 * Everything the کارگاه‌های کارفرما feature reads and writes.
 *
 * All of it is remote-only — nothing here is cached in the database, so the calls are plain
 * suspending functions rather than flows over a local table.
 */
interface WorkShopsRepository {

    suspend fun getEmployerAgreements(query: WorkshopListQuery): PagedListDN<EmployerAgreementDN>

    // -------------------------------------------------------------------------- برگ پرداخت‌ها

    suspend fun getPaymentSheets(query: PaymentSheetQuery): PagedListDN<PaymentSheetDN>

    suspend fun getDebitReasons(page: Int = 0): PagedListDN<DebitReasonDN>

    // ------------------------------------------------- گردش حساب بدهی + پرداخت

    suspend fun getWorkshopDebits(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkShopDebtDN>

    suspend fun getDemandDocuments(
        debitNumber: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkshopDemandDocDN>

    suspend fun getDebitTurnoverPdf(debitNumber: String, branchCode: String): PdfDownloadDN

    suspend fun checkDebitPayment(debitNumber: String, branchCode: String): DebitPaymentPreCheckDN

    suspend fun payWorkshopDebit(request: DebitPaymentRequestDN): DebitPaymentDN

    // ---------------------------------------------------------------------- استعلام بدهی کارگاه

    suspend fun getWorkshopDebtInquiry(workshopId: String, branchCode: String): WorkshopDebtInquiryDN

    // ------------------------------------------------------------------------------ اعتراض به بدهی

    suspend fun getObjectionableDebits(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkShopDebtDN>

    /** Days elapsed since a debt was served, which the filing window is checked against. */
    suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int

    suspend fun saveDebitObjection(request: DebitObjectionRequestDN): DebitObjectionResultDN

    suspend fun getDebitObjectionPdf(seqNo: Long): PdfDownloadDN

    // ------------------------------------------------- نام نویسی غیر حضوری بیمه شده

    suspend fun getRecentlyAddedMembers(
        query: WorkshopNewMemberQuery,
    ): PagedListDN<WorkshopNewMemberDN>

    /** Returns the tracking code the confirmation message quotes. */
    suspend fun confirmRecentlyAddedMember(requestId: Long): String

    suspend fun deleteRecentlyAddedMember(personalId: Long)

    /**
     * Whether the organisation has never registered this national id.
     *
     * `relation-tamins/isnew` answers a bare boolean — there is no id in the reply, so a
     * person it already knows is reported, not silently reused.
     */
    suspend fun checkNewMemberIsNew(nationalId: String): Boolean

    suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDN,
    ): NewMemberRegistrationResultDN

    // ---------------------------------------------------------------------- رسیدگی به بدهی ماده ۱۶

    suspend fun getArticleSixteenDebts(query: ArticleSixteenDebtQuery): PagedListDN<WorkshopsDebtListModelDN>

    suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String,
    ): ArticleSixteenWorkshopInfoDN

    suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDN

    suspend fun saveArticleSixteenRequest(request: ArticleSixteenSaveRequestDN): ArticleSixteenSaveResultDN

    suspend fun getArticleSixteenReportPdf(seqNo: Long): PdfDownloadDN

    // ------------------------------------------------------------------------ کارکنان / ذینفعان

    suspend fun getWorkshopMembers(query: WorkshopMemberQuery): PagedListDN<WorkshopMemberDN>

    suspend fun getWorkshopStackHolders(
        query: WorkshopStackHolderQuery,
    ): PagedListDN<WorkshopStackHolderDN>

    /** Workshops (کارگاه‌های حقوقی) the current user has legal-representative rights on. */
    fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?>

    /** Representatives already registered for one workshop. */
    fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?>

    /** Contracts (پیمان‌ها) belonging to one workshop — only relevant for a "special" workshop. */
    fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeContractListDN?>

    /** Requests a one-time verification code, optionally scoped to a specific national code. */
    suspend fun requestLegalRepresentativeTicket(nationalCode: String? = null)

    /** Verifies a one-time code. The code itself becomes the "ticket" used by later calls. */
    suspend fun verifyLegalRepresentativeTicket(ticket: String)

    suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN)

    suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long)
}

package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.Article16RequestInfoDN
import com.tamin.taminhamrah.model.workshop.Article16SaveRequestDN
import com.tamin.taminhamrah.model.workshop.Article16SaveResultDN
import com.tamin.taminhamrah.model.workshop.Article16WorkshopInfoDN
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
import com.tamin.taminhamrah.model.workshop.NewMemberIsNewDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN

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

    /** Asked before a create: an existing person is edited rather than added again. */
    suspend fun checkNewMemberIsNew(nationalId: String): NewMemberIsNewDN

    suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDN,
    ): NewMemberRegistrationResultDN

    // ---------------------------------------------------------------------- رسیدگی به بدهی ماده ۱۶

    suspend fun getArticle16Debts(query: Article16DebtQuery): PagedListDN<WorkshopsDebtListModelDN>

    suspend fun getArticle16WorkshopInfo(
        workshopId: String,
        branchCode: String,
    ): Article16WorkshopInfoDN

    suspend fun getArticle16RequestInfo(objectionNumber: Long): Article16RequestInfoDN

    suspend fun saveArticle16Request(request: Article16SaveRequestDN): Article16SaveResultDN

    suspend fun getArticle16ReportPdf(seqNo: Long): PdfDownloadDN

    // ------------------------------------------------------------------------ کارکنان / ذینفعان

    suspend fun getWorkshopMembers(query: WorkshopMemberQuery): PagedListDN<WorkshopMemberDN>

    suspend fun getWorkshopStackHolders(
        query: WorkshopStackHolderQuery,
    ): PagedListDN<WorkshopStackHolderDN>
}

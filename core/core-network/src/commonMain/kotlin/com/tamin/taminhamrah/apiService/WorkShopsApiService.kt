/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.Article16RequestInfoDTO
import com.tamin.taminhamrah.model.workshop.Article16SaveRequestDTO
import com.tamin.taminhamrah.model.workshop.Article16SaveResultDTO
import com.tamin.taminhamrah.model.workshop.Article16WorkshopInfoDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitReasonDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.NewMemberConfirmResultDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonElement

internal interface WorkShopsApiService {

    // ---------------------------------------------------------------- کارگاه‌های کارفرما (the list)

    @GET("workshop-services/employer/get-all-employer-agreement-by-national-id")
    suspend fun getAllEmployerAgreementByNationalId(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<EmployerAgreementDTO>>

    // -------------------------------------------------------------------------- برگ پرداخت‌ها

    @GET("workshop-services/payment-sheets")
    suspend fun getWorkshopPaymentSheets(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<PaymentSheetDTO>>

    /** The علت ایجاد بدهی picker on the payment-sheet search. */
    @GET("debit-reason")
    suspend fun getDebitReasons(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<DebitReasonDTO>>

    // ------------------------------------------------- جزئیات محاسبه گردش حساب بدهی + پرداخت

    @GET("debit-online-payment/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebitList(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkShopDebtDTO>>

    /** اسناد مطالبه of one debt. `eclaim-…` is the service's own prefix, not a typo. */
    @GET("debit-objection/eclaim-detail-objection-workshop-debit/{debitNumber}/{branchCode}")
    suspend fun getWorkshopDemandDocuments(
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopDemandDocDTO>>

    /** جزئیات محاسبه of one demand document, as a PDF byte stream. */
    @GET("debit-objection-reports/gardesh-list/{debitNumber}/{branchCode}")
    suspend fun getDebitTurnoverPdf(
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
    ): HttpStatement

    /** The trailing `1` is fixed in the route the service publishes. */
    @GET("debit-online-payment/debit-select-pre-check/{debitNumber}/{branchCode}/1")
    suspend fun checkDebitPayment(
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
    ): BaseDTO<DebitPaymentPreCheckDTO>

    @POST("debit-online-payment/pay-normal-debit")
    suspend fun payWorkshopDebit(
        @Body request: DebitPaymentRequestDTO,
    ): BaseDTO<DebitPaymentDTO>

    // ---------------------------------------------------------------------- استعلام بدهی کارگاه

    @GET("workshop-services/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebtInquiry(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String
    ): BaseDTO<WorkshopDebtInquiryDTO>

    // ------------------------------------------------------------------------------ اعتراض به بدهی

    @GET("debit-objection/objection-workshop-debit/{workshopNumber}/{branchCode}")
    suspend fun getWorkshopObjectionableDebitList(
        @Path("workshopNumber") workshopNumber: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkShopDebtDTO>>

    /**
     * Days elapsed since the debt was served. The filing window is 31 days for a برآوردی debt and
     * 21 for a بدوی vote; the service answers with the count only, and the client decides.
     */
    @GET("debit-objection/diff-days/{orderRecipeDate}")
    suspend fun getObjectionElapsedDays(
        @Path("orderRecipeDate") orderRecipeDate: String
    ): BaseDTO<Int>

    @POST("debit-objection/objection-save")
    suspend fun saveDebitObjection(
        @Body request: DebitObjectionSaveRequestDTO,
    ): BaseDTO<DebitObjectionSaveResultDTO>

    /** The filed objection, as a PDF byte stream. */
    @GET("debit-objection-reports/objection/{seqNumber}")
    suspend fun getDebitObjectionPdf(
        @Path("seqNumber") seqNumber: Long,
    ): HttpStatement

    // ------------------------------------------------- نام نویسی غیر حضوری بیمه شده

    @GET("employers")
    suspend fun getWorkshopRecentlyAddedMembers(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopNewMemberDTO>>

    @PUT("requests/confirm/{requestId}")
    suspend fun confirmRecentlyAddedMember(
        @Path("requestId") requestId: Long,
    ): BaseDTO<NewMemberConfirmResultDTO>

    @DELETE("subdominants/{personalId}")
    suspend fun deleteRecentlyAddedMember(
        @Path("personalId") personalId: Long,
    ): BaseDTO<JsonElement?>

    // ---------------------------------------------------------------------- رسیدگی به بدهی ماده ۱۶

    @GET("debit-objection/management-workshop-debit/{workshopId}/{branchId}")
    suspend fun getWorkshopsDebtsList(
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopsDebtListModelDTO>>

    @GET("workshop-services/get-workshops-info/{workshopId}/{branchCode}")
    suspend fun getArticle16WorkshopInfo(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
    ): BaseDTO<Article16WorkshopInfoDTO>

    @GET("debit-objection/objection-request/{objectionNumber}")
    suspend fun getArticle16RequestInfo(
        @Path("objectionNumber") objectionNumber: Long,
    ): BaseDTO<Article16RequestInfoDTO>

    @POST("debit-objection/debit-comitte-save")
    suspend fun saveArticle16Request(
        @Body request: Article16SaveRequestDTO,
    ): BaseDTO<Article16SaveResultDTO>

    /** The filed ماده ۱۶ request, as a PDF byte stream. */
    @GET("debit-objection-reports/comitte/{seqNumber}")
    suspend fun getArticle16ReportPdf(
        @Path("seqNumber") seqNumber: Long,
    ): HttpStatement

    // ------------------------------------------------------------------------ کارکنان / ذینفعان

    @GET("workshop-services/member/get-all")
    suspend fun getWorkshopMembers(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopMemberDTO>>

    @GET("workshop-services/workshop-stackholders/get-all")
    suspend fun getWorkshopStackHolders(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopStackHolderDTO>>
}

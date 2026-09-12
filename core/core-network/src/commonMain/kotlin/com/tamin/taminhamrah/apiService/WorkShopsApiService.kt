/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

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
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDTO
import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonElement

internal interface WorkShopsApiService {

    // ---------------------------------------------------------------- کارگاه‌های کارفرما (the list)

    @GET("workshop-services/employer/get-all-employer-agreement-by-national-id")
    suspend fun getAllEmployerAgreementByNationalId(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<EmployerAgreementDTO>>

    // ------------------------------------------------------------------------ ردیف‌های پیمان

    /**
     * ردیف پیمان‌های a workshop that *has* a تعهدنامه — same row shape as the list above, narrowed
     * to one workshop/branch instead of every workshop the signed-in national id holds.
     */
    @GET("workshop-services/get-employer-agreement-by-workshop-id-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getEmployerAgreementsByWorkshop(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<EmployerAgreementDTO>>

    /**
     * ردیف پیمان‌های a workshop with no تعهدنامه on file — a leaner row, and a different model.
     *
     * `contract-employer-workshop-info-…` is the service's own spelling, and the response names
     * two fields differently from the call above; see [WorkshopContractDTO].
     */
    @GET("workshop-services/contract-employer-workshop-info-with-workshop-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getWorkshopContracts(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<WorkshopContractDTO>>

    // ---------------------------------------------------------------------------- واگذارندگان
    //
    // پیمان‌هایی که کارفرمای واردشده «واگذارنده»ی آن‌هاست, plus the computational bases filed under
    // each and the documents attached to those.
    //
    // `requestissuanceinvoices38` is all lower case with no separators. That is the published
    // route, not a typo — the tidy spelling 404s. Do not let a formatter or a rename touch it.

    /**
     * لیست پیمان‌های واگذارنده, paged.
     *
     * The workshop, branch and ردیف are **filter clauses**, not path segments — they travel inside
     * the `filter` array `query` already carries (`workshop.workshopId`, `workshop.branchCode`,
     * `contractRow`). A blank one is simply omitted and widens the result; it does not address a
     * different route the way it does on the ردیف‌های پیمان endpoints.
     */
    @GET("requestissuanceinvoices38/assignersContracts-request-issuance-invoices38")
    suspend fun getAssignerContracts(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<AssignerContractDTO>>

    /**
     * مبانی محاسباتی of one پیمان, paged.
     *
     * Four identity parameters, all of them **query** parameters here.
     *
     * ⚠️ `brchCode` is spelled exactly that way — abbreviated, and *not* the `branchCode` every
     * neighboring workshop service uses. It was read off the old app's published interface. A
     * linter or an IDE rename that "corrects" it leaves the request looking fine while the branch
     * silently stops narrowing anything.
     */
    @GET("requestissuanceinvoices38/det-request-issuance-invoices38")
    suspend fun getComputationalBases(
        @Query("workshopId") workshopId: String,
        @Query("contractRow") contractRow: String,
        @Query("brchCode") brchCode: String,
        @Query("contractSequence") contractSequence: String,
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<ComputationalBaseDTO>>

    /**
     * A document attached to a مبنای محاسباتی, as a PDF byte stream.
     *
     * Typed [HttpStatement] so the bytes can be drained inside `execute` — see `readPdfChannel`.
     */
    @GET("requestissuanceinvoices38/getPdf-request-issuance-invoices38/{documentId}")
    suspend fun getComputationalBasePdf(
        @Path("documentId") documentId: String,
    ): HttpStatement

    // The image half of the same pair reuses the shared `upload-image` route, which answers with a
    // base64 payload rather than bytes; `UserRequestApiService.downloadDocument` already declares
    // it, so nothing is added here for it.

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

    /**
     * Whether this national id is someone the organization has never registered.
     *
     * `relation-tamins/isnew` — the registration asks before it creates, because an existing
     * person is edited rather than added again.
     */
    @GET("relation-tamins/isnew/{nationalId}")
    suspend fun checkNewMemberIsNew(
        @Path("nationalId") nationalId: String,
    ): BaseDTO<Boolean>

    /** Creates the registration. `employers`, as the old app posts it. */
    @POST("employers")
    suspend fun createNewMemberRegistration(
        @Body request: NewMemberRegistrationDTO,
    ): BaseDTO<NewMemberRegistrationResultDTO>

    // ---------------------------------------------------------------------- رسیدگی به بدهی ماده ۱۶

    @GET("debit-objection/management-workshop-debit/{workshopId}/{branchId}")
    suspend fun getWorkshopsDebtsList(
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopsDebtListModelDTO>>

    @GET("workshop-services/get-workshops-info/{workshopId}/{branchCode}")
    suspend fun getArticleSixteenWorkshopInfo(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
    ): BaseDTO<ArticleSixteenWorkshopInfoDTO>

    @GET("debit-objection/objection-request/{objectionNumber}")
    suspend fun getArticleSixteenRequestInfo(
        @Path("objectionNumber") objectionNumber: Long,
    ): BaseDTO<ArticleSixteenRequestInfoDTO>

    @POST("debit-objection/debit-comitte-save")
    suspend fun saveArticleSixteenRequest(
        @Body request: ArticleSixteenSaveRequestDTO,
    ): BaseDTO<ArticleSixteenSaveResultDTO>

    /** The filed ماده ۱۶ request, as a PDF byte stream. */
    @GET("debit-objection-reports/comitte/{seqNumber}")
    suspend fun getArticleSixteenReportPdf(
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

    @GET("v.1/legal-stakeholders/units")
    suspend fun getLegalRepresentativeWorkshops(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<LegalRepresentativeWorkshopDTO>>

    @GET("legal-stakeholders")
    suspend fun getLegalRepresentatives(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<LegalRepresentativeDTO>>

    @GET("workshop-services/get-employer-agreement-by-workshop-id-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getLegalRepresentativeWorkshopContracts(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<LegalRepresentativeContractDTO>>

    @GET("legal-ticket")
    suspend fun requestLegalTicket(): BaseDTO<JsonElement?>

    @GET("legal-ticket/{nationalCode}")
    suspend fun requestLegalTicketWithNationalCode(
        @Path("nationalCode") nationalCode: String
    ): BaseDTO<JsonElement?>

    @POST("legal-ticket/validate/{ticket}")
    suspend fun validateLegalTicket(
        @Path("ticket") ticket: String
    ): BaseDTO<JsonElement?>

    @POST("legal-stakeholders/{ticket}")
    suspend fun submitLegalRepresentative(
        @Path("ticket") ticket: String,
        @Body request: LegalRepresentativeRequestDTO,
    ): BaseDTO<JsonElement?>

    @DELETE("legal-stakeholders/{ticket}/{stackId}")
    suspend fun deleteLegalRepresentative(
        @Path("ticket") ticket: String,
        @Path("stackId") stackId: Long,
    ): BaseDTO<JsonElement?>

    // ------------------------------------------------- خدمات غیرحضوری کارفرما (employerServicesAgreement)
    //
    // Employer → Online Services. A three-step stepper (request ticket -> verify code -> confirm
    // agreement) plus two management-side drill-downs. See EmployerOnlineServiceDTO.kt for the flow.

    /**
     * Step 1 — درخواست کد تایید (تیکت) برای ثبت تعهد خدمات غیرحضوری.
     *
     * The employer enters the mobile/email they want registered; the backend sends an OTP and
     * this returns a bare confirmation message. Called from the first stepper page when the
     * requested contact details differ from the current ones.
     *
     * [filter] is the `[{property,value,operator}]` JSON array built in
     * [com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource.requestEmployerAgreementTicket]
     * (`mobileNumber`, `email`, `serviceName=employerEservicesAgreement`).
     */
    @GET("workshop-services/request-ticket")
    suspend fun requestEmployerAgreementTicket(
        @Query("filter") filter: String,
    ): BaseDTO<JsonElement?>

    /**
     * Step 2 — تایید کد و دریافت مشخصات هویتی کارفرما.
     *
     * Exchanges the OTP the employer typed for their identity block (نام، کد ملی، موبایل/ایمیل
     * فعلی), which the second stepper page shows next to the newly requested values before the
     * employer accepts the rules.
     */
    @GET("workshop-services/employer-info/{verificationCode}")
    suspend fun getEmployerAgreementUserInfo(
        @Path("verificationCode") verificationCode: String,
    ): BaseDTO<EmployerCommitmentInfoDTO>

    /**
     * Step 2 — لیست کارگاه‌های بدون قرارداد کارفرما.
     *
     * Paged list shown on the second stepper page while the employer confirms the agreement.
     * Tapping a row opens [getEmployerWorkshopContractList] for that workshop.
     */
    @GET("workshop-services/employer-workshops-info-with-out-contract")
    suspend fun getEmployerWorkshopsWithoutContract(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<WorkshopWithoutContractDTO>>

    /**
     * پیمانکاران / قراردادهای یک کارگاه.
     *
     * Opened when the employer drills into a workshop — from the without-contract list (step 2)
     * or from a registered agreement on the management side. Paged.
     */
    @GET("workshop-services/contract-employer-workshop-info-with-workshop-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getEmployerWorkshopContractList(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<WorkshopContractRowDTO>>

    /**
     * Step 3 — ثبت نهایی تعهد خدمات غیرحضوری کارفرما.
     *
     * Posted from the last stepper page once the employer ticks the قوانین checkbox. Returns a
     * bare success message; the screen navigates back on success.
     */
    @POST("workshop-services/employer-agreement")
    suspend fun submitEmployerAgreement(
        @Body request: EmployerAgreementSubmitRequestDTO,
    ): BaseDTO<JsonElement?>

    @GET("debit-objection/objection-all")
    suspend fun getWorkShopObjections(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkShopObjectionDTO>>

    /** پیامک‌های one filed objection. `objectionCode` is the row's own `seqNo`. */
    @GET("debit-objection/objection-detail/{objectionCode}/")
    suspend fun getWorkShopObjectionSms(
        @Path("objectionCode") objectionCode: Long,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<SmsMessageDTO>>
}

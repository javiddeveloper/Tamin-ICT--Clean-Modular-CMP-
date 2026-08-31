/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.http.cio.Response
import kotlinx.serialization.json.JsonElement

internal interface WorkShopsApiService {

    @GET("workshop-services/employer/get-all-employer-agreement-by-national-id")
    suspend fun getAllEmployerAgreementByNationalId(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<EmployerAgreementDTO>>


    @GET("workshop-services/payment-sheets")
    suspend fun getWorkshopPaymentSheets(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<PaymentSheetDTO>>

    @GET("debit-online-payment/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebit(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopDebitDTO>>

    @GET("workshop-services/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebtInquiry(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String
    ): BaseDTO<WorkshopDebtInquiryDTO>

    @GET("debit-objection/objection-workshop-debit/{workshopNumber}/{branchCode}")
    suspend fun getWorkshopObjectionableDebitList(
        @Path("workshopNumber") workshopNumber: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkShopDebtDTO>>

    @GET("employers")
    suspend fun getWorkshopRecentlyAddedMembers(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopNewMemberDTO>>

    @GET("debit-objection/management-workshop-debit/{workshopId}/{branchId}")
    suspend fun getWorkshopsDebtsList(
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopsDebtListModelDTO>>

    @GET("workshop-services/member/get-all")
    suspend fun getWorkshopMembers(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopMemberDTO>>

    @GET("workshop-services/workshop-stackholders/get-all")
    suspend fun getWorkshopStackHolders(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopStackHolderDTO>>

    // ─── Legal representative introduction (معرفی نماینده اشخاص حقوقی) ────────────────

    @GET("v.1/legal-stakeholders/units")
    suspend fun getLegalRepresentativeWorkshops(
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseDTO<ListData<LegalRepresentativeWorkshopDTO>>

    @GET("legal-stakeholders")
    suspend fun getLegalRepresentatives(
        @Query("stackType") stackType: String = "4",
        @Query("workshopId") workshopId: String,
        @Query("branchCode") branchCode: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseDTO<ListData<LegalRepresentativeDTO>>

    // The workshop's contracts (پیمان‌ها) — only relevant for a "special" workshop. Mirrors the
    // legacy Android app's real endpoint of the same shape.
    @GET("workshop-services/get-employer-agreement-by-workshop-id-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getLegalRepresentativeWorkshopContracts(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "100",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
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
}

package com.tamin.taminhamrah.apiService.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDTO
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonElement


interface PensionApiService {

    @GET("pension-inquiry")
    suspend fun getPensionInquiry(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PensionInquiryDTO>>

    @GET("pensioner-no")
    suspend fun getPensionerId(): BaseDTO<ListData<PensionIdDTO>>

    @GET("hokm")
    suspend fun getEdictPensioner(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<EdictPensionerDTO>

    @Headers("Content-Type: application/json")
    @POST("wage-assignment")
    suspend fun sendRequestDeferredInstallmentCertificate(
        @Body deferredInstallmentRequest: DeferredInstallmentRequest
    ): BaseDTO<DeferredInstallmentCertificateDTO>

    @GET("disability-request/personal")
    suspend fun getDisabilityPersonalInfo(
    ): BaseDTO<DisabilityPersonalInfoDTO>

    @GET("pension-request/age")
    suspend fun getUserAge(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<AgeDTO>

    @Streaming
    @GET("fish/report")
    suspend fun pensionerPayRollPDF(
        @QueryMap parameters: Map<String, String>
    ): HttpStatement

    @GET("fish")
    suspend fun getPensionerPayRoll(
        @Query("filter") filter: String,
    ): BaseDTO<ListData<PayRollDTO>>

    @GET("fish/annoncment")
    suspend fun sendPayRollToInbox(
        @Query("filter") filter: String,
    ): BaseDTO<JsonElement?>?

    @Streaming
    @GET("hokm/report")
    suspend fun getEdictReportPDF(
        @QueryMap parameters: Map<String, String>
    ): HttpStatement

    @GET("pension-request")
    suspend fun getRetirementRequestInfo(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<RetirementRequestDTO>>

    @GET("pension-request/personal")
    suspend fun authenticationAndGetPersonalInfo(
        @Query("ticketCode") authenticationsCode: Long
    ): BaseDTO<RetirementPersonalDTO>

    /**
     * Creates the retirement request from the confirmed identity/workshop form and returns its id.
     * The OTP ticket is re-presented here as `ticketCode`, exactly as `pension-request/personal`
     * takes it — the server treats the ticket, not a session flag, as proof of verification.
     */
    @Headers("Content-Type: application/json")
    @POST("pension-request")
    suspend fun createRetirementRequest(
        @Query("ticketCode") authenticationsCode: Long,
        @Body body: RetirementRequestFormDTO
    ): BaseDTO<RetirementRequestCreatedDTO>

    @GET("pension-request/checkRequests")
    suspend fun checkRetirementStatus(): BaseDTO<RetirementStatusDTO>

    @GET("pension-request/getTicket")
    suspend fun getAuthenticationCode(
    ): BaseDTO<AuthenticationTicketDTO>

    @GET("pension-inquiry/announcement/")
    suspend fun sendRequestInquirePensionCertificate(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<JsonElement?>


    @Headers("Content-Type: application/json")
    @PUT("pension-request/{requestId}")
    suspend fun sendRetirementDocument(
        @Path("requestId") requestId: String,
        @Body body: RetirementSaveDocumentRequest
    ): BaseDTO<String?>

    @GET("hokm/annoncment")
    suspend fun sendEdictPensionerToMyInbox(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<JsonElement?>

}

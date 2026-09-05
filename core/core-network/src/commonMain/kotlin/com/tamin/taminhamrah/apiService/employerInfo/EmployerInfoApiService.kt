package com.tamin.taminhamrah.apiService.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonElement

/**
 * تکمیل اطلاعات کارفرمایی.
 *
 * The base path alternates between `workshop-service` and `workshop-services` from call to call.
 * That is how the backend spells them; neither is a typo to be tidied up.
 */
interface EmployerInfoApiService {

    @GET("workshop-service/legal-inquiry/{legalWorkshopId}")
    suspend fun getLegalWorkshop(
        @Path("legalWorkshopId") legalWorkshopId: String,
    ): BaseDTO<LegalWorkshopDTO>

    @GET("workshop-service/inquiry/{nationalCode}/{birthDate}")
    suspend fun getLegalWorkshopCeo(
        @Path("nationalCode") nationalCode: String,
        @Path("birthDate") birthDate: String,
    ): BaseDTO<LegalWorkshopCeoDTO>

    /** Ticket for the legal path — carries the manager's national code as well as the contacts. */
    @GET("workshop-services/request-ticket2")
    suspend fun requestLegalTicket(
        @Query("filter") filter: String,
    ): BaseDTO<JsonElement?>

    @POST("workshop-service/save-stack-holders")
    suspend fun submitLegalWorkshopInfo(
        @Body body: LegalWorkshopInfoRequestDTO,
    ): BaseDTO<JsonElement?>

    /** Ticket for the real path — contacts only, taken from the signed-in user's profile. */
    @GET("workshop-services/request-ticket")
    suspend fun requestRealTicket(
        @Query("filter") filter: String,
    ): BaseDTO<JsonElement?>

    @POST("workshop-service/save-real-person-info")
    suspend fun submitRealWorkshopInfo(
        @Body body: RealWorkshopInfoRequestDTO,
    ): BaseDTO<JsonElement?>
}

package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonElement

interface PersonalApiService {

    @GET("survivor-request/personal")
    suspend fun getPersonalInfo(
    ): BaseDTO<PersonalInfoDTO>

    @GET("survivor-request/national-id")
    suspend fun getDeceasedInfo(
        @Query("id") nationalId: String
    ): BaseDTO<DeceasedInfoDTO>


    @GET("survivor-request/age")
    suspend fun getAge(
        @Query("birthDate") birthDate: Long = 0L
    ): BaseDTO<AgeDTO>

    @GET("disability-request/subdominant")
    suspend fun getDisabilityDependentInfo(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<DisabilityDependentDTO>>

    @GET("survivor-request/subdominant")
    suspend fun getSurvivorList(
        @Query("id") id: String,
    ): BaseDTO<ListData<SurvivorDependentDTO>>

    @GET("survivor-request/condition")
    suspend fun checkGirlSurvivorConditions(
        @Query("code") nationalCode: String,
        @Query("rel") relation: String = "04",
        @Query("pensionerId") pensionerId: String,
    ): BaseDTO<JsonElement?>

    @GET("survivor-request/list")
    suspend fun confirmSurvivorsList(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<ConfirmSurvivorDTO>>

    @PUT("survivor-request/{requestId}")
    suspend fun submitFinalSurvivorPension(
        @Path("requestId") requestId: Int,
        @Body body: SubmitFinalSurvivorPensionRequest
    ): BaseDTO<JsonElement?>


    @POST("survivor-request")
    suspend fun saveSurvivorInfo(
        @Body body: SaveSurvivorInfoRequest
    ): BaseDTO<JsonElement?>

    @Streaming
    @GET("survivor-request/final-report")
    suspend fun getFinalSurvivorPensionPDF(
    ): HttpStatement

    @Streaming
    @GET("survivor-request/report")
    suspend fun getGirlSurvivorReport(
        @Query("address") address: String,
        @Query("tel") tel: String,
        @Query("postalCode") postalCode: String,
        @Query("fatherName") fatherName: String?,
        @Query("birthDate") birthDate: Long?,
        @Query("insuranceId") insuranceId: String?,
        @Query("parentCode") parentCode: String,
        @Query("pensionerId") pensionerId: String,
    ): HttpStatement

    @POST("female-request")
    suspend fun confirmGirlSurvivor(
        @Body body: ConfirmGirlSurvivorRequestDTO,
    ): BaseDTO<JsonElement?>

    @PUT("documents/{personalId}")
    suspend fun putInsuredRegistrationDocList(
        @Path("personalId") personalId: String,
        @Body body: List<InsuredDocDTO>
    ): BaseDTO<String?>

    /** The documents filed against a person — the list [putInsuredRegistrationDocList] replaces. */
    @GET("documents")
    suspend fun getInsuredRegistrationDocList(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<InsuredDocDTO>>

    @GET("personals/summary/{requestId}")
    suspend fun getRequestSummary(
        @Path("requestId") requestId: String
    ): BaseDTO<NewInsuredSummaryDTO>

}

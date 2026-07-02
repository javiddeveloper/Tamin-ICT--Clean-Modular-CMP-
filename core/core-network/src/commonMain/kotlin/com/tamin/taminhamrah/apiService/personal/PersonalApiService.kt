package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
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

    @GET("survivor-request/condition")
    suspend fun checkGirlSurvivorConditions(
        @Query("code") nationalCode: String,
        @Query("rel") relation: String = "04",
        @Query("pensionerId") pensionerId: String,
    ): BaseDTO<String?>

    @GET("survivor-request/list")
    suspend fun confirmSurvivorsList(
        @QueryMap parameters: Map<String, String>,
    ): BaseDTO<ListData<ConfirmSurvivorDTO>>

    @PUT("survivor-request/{requestId}")
    suspend fun submitFinalSurvivorPension(
        @Path("requestId") requestId: Int,
        @Body body: SubmitFinalSurvivorPensionRequest
    ): BaseDTO<String>


    @POST("survivor-request")
    suspend fun saveSurvivorInfo(
        @Body body: SaveSurvivorInfoRequest
    ): BaseDTO<String>

    @Streaming
    @GET("survivor-request/final-report")
    suspend fun getFinalSurvivorPensionPDF(
    ): HttpStatement

}

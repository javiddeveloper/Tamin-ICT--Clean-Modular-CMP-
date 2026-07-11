package com.tamin.taminhamrah.apiService.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.TreatmentCostDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.statement.HttpStatement

internal interface TreatmentApiService {

    @GET("booklet-req/lackEntitlement/{nationalCode}")
    suspend fun getDeservedTreatment(
        @Path("nationalCode") nationalCode: String,
    ): BaseDTO<ListData<DeservedTreatmentDTO>>

    @GET("patient-history/get-dependent-children/{nationalCode}")
    suspend fun getDependantUnderEighteen(
        @Path("nationalCode") nationalCode: String,
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<DependantUserUnderEighteenDTO>>

    @GET("health/tcr-price-certificate")
    suspend fun getTreatmentCosts(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<TreatmentCostDTO>>

    @GET("health/tcr-price-certificate/report/{repId}")
    suspend fun getTreatmentCostsPDF(
        @Path("repId") repId: String
    ): HttpStatement

    @GET("health/tcr-price-certificate/announcement/{repId}")
    suspend fun sendToInboxTreatmentCosts(
        @Path("repId") repId: String
    ): BaseDTO<String>
}

package com.tamin.taminhamrah.apiService.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.MedicalAuthoritiesDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

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

    @GET("shortterm-request/commission-confrimation")
    suspend fun getConfirmationMedicalAuthorities(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<MedicalAuthoritiesDTO>>
}

package com.tamin.taminhamrah.apiService.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.serialization.json.JsonElement

interface OccurrenceApiService {

    @GET("occurence/office-personalInfo")
    suspend fun getPersonalInfo(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<OccurrencePersonalInfoDTO>

    @GET("occurence/all-workshop")
    suspend fun getAllWorkshops(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<List<JsonElement?>>>

    @GET("occurence/workshop-specifications")
    suspend fun getWorkshopSpec(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<WorkshopItemDTO>

    @GET("occurence/insured-relation")
    suspend fun getInsuredRelation(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<InsuredRelationDTO>

    @GET("occurrence-document-type")
    suspend fun getDocumentTypes(
        @QueryMap queries: Map<String, String>,
    ): BaseDTO<ListData<OccurrenceDocTypeDTO>>

    @POST("upload-image/occurrenceImage")
    suspend fun uploadImage(
        @Body content: MultiPartFormDataContent,
    ): BaseDTO<OccurrenceResponseDTO>

    @POST("occurence")
    suspend fun submitOccurrence(
        @Body request: OccurrenceRequestDTO,
    ): BaseDTO<OccurrenceResponseDTO>
}

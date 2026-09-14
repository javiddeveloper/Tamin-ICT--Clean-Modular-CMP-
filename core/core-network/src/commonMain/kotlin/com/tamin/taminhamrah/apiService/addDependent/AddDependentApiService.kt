package com.tamin.taminhamrah.apiService.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDTO
import com.tamin.taminhamrah.model.addDependent.DependentInfoDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDTO
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDTO
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDTO
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.request.forms.MultiPartFormDataContent

interface AddDependentApiService {

    @GET("services/dependent-info")
    suspend fun getDependentInfo(): BaseDTO<List<DependentInfoDTO>>

    @GET("subdominants/getInsuredActiveBranch")
    suspend fun getActiveBranches(): BaseDTO<List<BranchDTO>>

    @GET("services/family-relationships")
    suspend fun getFamilyRelationships(
        @QueryMap parameters: Map<String, String> = emptyMap()
    ): BaseDTO<List<FamilyRelationshipDTO>>

    @GET("proxy/models/dependency")
    suspend fun getFamilyRelationshipsFromProxy(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<FamilyRelationshipProxyDTO>>

    @GET("subdominants/getOfficeData/{nationalCode}/{timeStampBirthDay}/{dependencyCode}")
    suspend fun inquiryRegistry(
        @Path("nationalCode") dependentNationalId: String,
        @Path("timeStampBirthDay") birthDateTimeStamp: String,
        @Path("dependencyCode") dependencyCode: String
    ): BaseDTO<RegistryDataDTO>

    @GET("services/inquiry-education")
    suspend fun inquiryEducationCode(
        @Query("nationalId") nationalId: String,
        @Query("educationCode") educationCode: String
    ): BaseDTO<String>

    @POST("upload-image")
    suspend fun uploadImage(
        @Body body: MultiPartFormDataContent
    ): UploadImageResponseDTO

    @POST("services/add-dependent")
    suspend fun addNewDependent(
        @Body request: RequestAddDependentDTO
    ): BaseDTO<GeneralResponseDTO>

    @POST("subdominants/transfer")
    suspend fun refreshDependents(): BaseDTO<GeneralResponseDTO>
}

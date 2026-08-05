package com.tamin.taminhamrah.apiService.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.DependentInfoDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface AddDependentApiService {

    @GET("services/dependent-info")
    suspend fun getDependentInfo(): BaseDTO<List<DependentInfoDto>>

    @GET("services/active-branches")
    suspend fun getActiveBranches(): BaseDTO<List<BranchDto>>

    @GET("services/family-relationships")
    suspend fun getFamilyRelationships(
        @Query("query") queryJson: String? = null
    ): BaseDTO<List<FamilyRelationshipDto>>

    @GET("proxy/models/dependency")
    suspend fun getFamilyRelationshipsFromProxy(): BaseDTO<List<FamilyRelationshipProxyDto>>

    @GET("services/inquiry-registry")
    suspend fun inquiryRegistry(
        @Query("dependentNationalId") dependentNationalId: String,
        @Query("birthDateTimeStamp") birthDateTimeStamp: String,
        @Query("dependencyCode") dependencyCode: String
    ): BaseDTO<RegistryDataDto>

    @GET("services/inquiry-education")
    suspend fun inquiryEducationCode(
        @Query("nationalId") nationalId: String,
        @Query("educationCode") educationCode: String
    ): BaseDTO<String>

    @POST("services/upload-image")
    suspend fun uploadImage(
        @Body imageBytes: ByteArray
    ): BaseDTO<UploadImageResponseDto>

    @POST("services/add-dependent")
    suspend fun addNewDependent(
        @Body request: RequestAddDependentDto
    ): BaseDTO<GeneralResponseDto>
}

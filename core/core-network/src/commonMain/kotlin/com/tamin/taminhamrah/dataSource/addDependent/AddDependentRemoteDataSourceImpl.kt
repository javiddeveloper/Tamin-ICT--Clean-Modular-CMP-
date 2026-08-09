package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.apiService.addDependent.AddDependentApiService
import com.tamin.taminhamrah.model.addDependent.BranchDTO
import com.tamin.taminhamrah.model.addDependent.DependentInfoDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDTO
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDTO
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDTO
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeCall
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class AddDependentRemoteDataSourceImpl(
    private val apiService: AddDependentApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : AddDependentRemoteDataSource {

    override suspend fun getDependentInfo(): List<DependentInfoDto> =
        errorParser.safeCall("getDependentInfo") {
            apiService.getDependentInfo().extractData()
        }

    override suspend fun getActiveBranches(): List<BranchDto> =
        errorParser.safeCall("getActiveBranches") {
            apiService.getActiveBranches().extractData()
        }

    override suspend fun getFamilyRelationships(
        filter: List<ApiFilterDN>
    ): List<FamilyRelationshipDto> = errorParser.safeCall("getFamilyRelationships") {
        val parameters = if (filter.isEmpty()) {
            emptyMap()
        } else {
            mapOf("query" to apiQueryBuilder.buildFilterJson(filter))
        }
        apiService.getFamilyRelationships(parameters).extractData()
    }

    override suspend fun getFamilyRelationshipsFromProxy(
        filter: List<ApiFilterDN>
    ): List<FamilyRelationshipProxyDto> = errorParser.safeCall("getFamilyRelationshipsFromProxy") {
        val parameters = apiQueryBuilder.buildQuery(
            ApiQueryParamDN(page = 1, filters = filter)
        )
        apiService.getFamilyRelationshipsFromProxy(parameters).extractData().list.orEmpty()
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDto = errorParser.safeCall("inquiryRegistry") {
        apiService.inquiryRegistry(
            dependentNationalId,
            birthDateTimeStamp,
            dependencyCode
        ).extractData()
    }

    override suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): String = errorParser.safeCall("inquiryEducationCode") {
        apiService.inquiryEducationCode(nationalId, educationCode).extractData()
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): UploadImageResponseDto = errorParser.safeCall("uploadImage") {
        apiService.uploadImage(createUploadImageRequest(imageBytes, fileName, mimeType)).extractData()
    }

    private fun createUploadImageRequest(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        )
    }

    override suspend fun addNewDependent(request: RequestAddDependentDto): GeneralResponseDto =
        errorParser.safeCall("addNewDependent") {
            apiService.addNewDependent(request).extractData()
        }
}

package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.apiService.addDependent.AddDependentApiService
import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.DependentInfoDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal class AddDependentRemoteDataSourceImpl(
    private val apiService: AddDependentApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : AddDependentRemoteDataSource {

    override suspend fun getDependentInfo(): List<DependentInfoDto> {
        return try {
            val response = apiService.getDependentInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getActiveBranches(): List<BranchDto> {
        return try {
            val response = apiService.getActiveBranches()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getFamilyRelationships(
        filter: List<ApiFilterDN>
    ): List<FamilyRelationshipDto> {
        return try {
            val parameters = if (filter.isEmpty()) {
                emptyMap()
            } else {
                mapOf("query" to apiQueryBuilder.buildFilterJson(filter))
            }
            val response = apiService.getFamilyRelationships(parameters)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getFamilyRelationshipsFromProxy(
        filter: List<ApiFilterDN>
    ): List<FamilyRelationshipProxyDto> {
        return try {
            val parameters = apiQueryBuilder.buildQuery(
                ApiQueryParamDN(page = 1, filters = filter)
            )
            val response = apiService.getFamilyRelationshipsFromProxy(parameters)
            response.extractData().list.orEmpty()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDto {
        return try {
            val response = apiService.inquiryRegistry(dependentNationalId, birthDateTimeStamp, dependencyCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): String {
        return try {
            val response = apiService.inquiryEducationCode(nationalId, educationCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): UploadImageResponseDto {
        return try {
            val response = apiService.uploadImage(createUploadImageRequest(imageBytes, fileName, mimeType))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
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

    override suspend fun addNewDependent(request: RequestAddDependentDto): GeneralResponseDto {
        return try {
            val response = apiService.addNewDependent(request)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}

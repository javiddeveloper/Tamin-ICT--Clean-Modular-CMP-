package com.tamin.taminhamrah.dataSource.occurrence

import com.tamin.taminhamrah.apiService.occurrence.OccurrenceApiService
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal class OccurrenceRemoteDataSourceImpl(
    private val apiService: OccurrenceApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : OccurrenceRemoteDataSource {

    override suspend fun getPersonalInfo(nationalCode: String, birthDate: String): OccurrencePersonalInfoDTO {
        return try {
            val queries = queryBuilder.buildQuery(
                ApiQueryParamDN(
                    filters = listOf(
                        ApiFilterDN(
                            property = FilterProperty.NATIONAL_CODE,
                            value = nationalCode,
                            operator = FilterOperator.EQUAL,
                        ),
                        ApiFilterDN(
                            property = FilterProperty.BIRTH_DATE,
                            value = birthDate,
                            operator = FilterOperator.EQUAL,
                        ),
                    )
                )
            )
            apiService.getPersonalInfo(queries).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getAllWorkshops(nationalCode: String): ListData<WorkshopItemDTO> {
        return try {
            val queries = queryBuilder.buildQuery(
                ApiQueryParamDN(
                    filters = listOf(
                        ApiFilterDN(
                            property = FilterProperty.PAYMENT_WORKSHOP_ID,
                            value = nationalCode,
                            operator = FilterOperator.EQUAL,
                        )
                    )
                )
            )
            val response = apiService.getAllWorkshops(queries).extractData()
            ListData(
                total = response.total,
                list = response.list?.map { arr ->
                    val workshopCode = arr.getOrNull(0)?.jsonPrimitive?.contentOrNull
                    WorkshopItemDTO(
                        id = workshopCode,
                        workshopCode = workshopCode,
                        branchCode = arr.getOrNull(2)?.jsonPrimitive?.contentOrNull,
                    )
                }
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            println("OccurrenceDataSource.getAllWorkshops: ${e::class.simpleName}: ${e.message}")
            e.printStackTrace()
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDTO {
        return try {
            val queries = queryBuilder.buildQuery(
                ApiQueryParamDN(
                    filters = listOf(
                        ApiFilterDN(
                            property = FilterProperty.WORKSHOP_CODE,
                            value = workshopCode,
                            operator = FilterOperator.EQUAL,
                        ),
                        ApiFilterDN(
                            property = FilterProperty.PAYMENT_BRANCH_CODE,
                            value = branchCode,
                            operator = FilterOperator.EQUAL,
                        ),
                    )
                )
            )
            apiService.getWorkshopSpec(queries).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDTO {
        return try {
            // occurence/insured-relation's filter key is literally "workshopId" (FilterProperty.PAYMENT_WORKSHOP_ID),
            // but the value it expects is the requesting person's national code, not an actual workshop id.
            val queries = queryBuilder.buildQuery(
                ApiQueryParamDN(
                    filters = listOf(
                        ApiFilterDN(
                            property = FilterProperty.PAYMENT_WORKSHOP_ID,
                            value = nationalCode,
                            operator = FilterOperator.EQUAL,
                        )
                    )
                )
            )
            apiService.getInsuredRelation(queries).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getDocumentTypes(): ListData<OccurrenceDocTypeDTO> {
        return try {
            val queries = queryBuilder.buildQuery(ApiQueryParamDN())
            apiService.getDocumentTypes(queries).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String {
        return try {
            val content = MultiPartFormDataContent(
                formData {
                    append(
                        key = "file",
                        value = fileBytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        },
                    )
                },
            )
            apiService.uploadImage(content).extractData().trackingCode
                ?: throw TaminErrorUriException(ErrorUri.UNKNOWN)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun submitOccurrence(request: OccurrenceRequestDTO): OccurrenceResponseDTO {
        return try {
            apiService.submitOccurrence(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}

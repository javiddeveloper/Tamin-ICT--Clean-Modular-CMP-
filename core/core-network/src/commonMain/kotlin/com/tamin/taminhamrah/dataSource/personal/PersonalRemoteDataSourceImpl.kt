package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import io.ktor.client.statement.bodyAsChannel

class PersonalRemoteDataSourceImpl(
    private val personalApiService: PersonalApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : PersonalRemoteDataSource {

    override suspend fun getPersonalInfo(): PersonalInfoDTO? {
        return try {
            val response = personalApiService.getPersonalInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getDeceasedInfo(nationalId: String): DeceasedInfoDTO {
        return try {
            val response = personalApiService.getDeceasedInfo(nationalId)
            val data = response.extractData()
            if (data.related == "0") {
                throw TaminErrorUriException(ErrorUri.RESOURCE_NOT_FOUND)
            }
            data
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getAge(birthDate: Long): AgeDTO {
        return try {
            val response = personalApiService.getAge(birthDate)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getDisabilityDependentInfo(query: ApiQueryParamDN): List<DisabilityDependentDTO> {
        return try {
            val response = personalApiService.getDisabilityDependentInfo(
                queryBuilder.buildQuery(query)
            )
            response.extractData().list ?: emptyList()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String
    ): String? {
        return try {
            val response = personalApiService.checkGirlSurvivorConditions(
                nationalCode = nationalCode,
                pensionerId = pensionerId
            )
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun confirmSurvivorsList(query: ApiQueryParamDN): List<ConfirmSurvivorDTO> {
        return try {
            val response = personalApiService.confirmSurvivorsList(
                queryBuilder.buildQuery(query)
            )
            response.extractData().list ?: emptyList()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionRequest
    ): String? {
        return try {
            val response = personalApiService.submitFinalSurvivorPension(requestId, body)
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun getFinalSurvivorPensionPDF(): PdfDownloadDTO {
        return try {
            val response = personalApiService.getFinalSurvivorPensionPDF()
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.body()
                )
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun saveSurvivorInfo(body: SaveSurvivorInfoRequest): String? {
        return try {
            val response = personalApiService.saveSurvivorInfo(body)
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }


}

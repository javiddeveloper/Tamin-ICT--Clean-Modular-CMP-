package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorRequestDTO
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.readPdfChannel
import com.tamin.taminhamrah.tools.safeCall

class PersonalRemoteDataSourceImpl(
    private val personalApiService: PersonalApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : PersonalRemoteDataSource {

    override suspend fun getPersonalInfo(): PersonalInfoDTO? =
        errorParser.safeCall("getPersonalInfo") {
            personalApiService.getPersonalInfo().extractData()
        }

    override suspend fun getDeceasedInfo(nationalId: String): DeceasedInfoDTO =
        errorParser.safeCall("getDeceasedInfo") {
            val data = personalApiService.getDeceasedInfo(nationalId).extractData()
            if (data.related == "0") {
                throw TaminErrorUriException(ErrorUri.RESOURCE_NOT_FOUND)
            }
            data
        }

    override suspend fun getAge(birthDate: Long): AgeDTO =
        errorParser.safeCall("getAge") {
            personalApiService.getAge(birthDate).extractData()
        }

    override suspend fun getDisabilityDependentInfo(query: ApiQueryParamDN): List<DisabilityDependentDTO> =
        errorParser.safeCall("getDisabilityDependentInfo") {
            personalApiService.getDisabilityDependentInfo(
                queryBuilder.buildQuery(query)
            ).extractData().list ?: emptyList()
        }

    override suspend fun getSurvivorList(deceasedNationalId: String): List<SurvivorDependentDTO> =
        errorParser.safeCall("getSurvivorList") {
            personalApiService.getSurvivorList(deceasedNationalId).extractData().list ?: emptyList()
        }

    override suspend fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String
    ): String? =
        errorParser.safeCall("checkGirlSurvivorConditions") {
            personalApiService.checkGirlSurvivorConditions(
                nationalCode = nationalCode,
                pensionerId = pensionerId
            ).extractMessage()
        }

    override suspend fun confirmSurvivorsList(query: ApiQueryParamDN): List<ConfirmSurvivorDTO> =
        errorParser.safeCall("confirmSurvivorsList") {
            personalApiService.confirmSurvivorsList(
                queryBuilder.buildQuery(query)
            ).extractData().list ?: emptyList()
        }

    override suspend fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionRequest
    ): String? =
        errorParser.safeCall("submitFinalSurvivorPension") {
            personalApiService.submitFinalSurvivorPension(requestId, body).extractMessage()
        }

    override suspend fun getFinalSurvivorPensionPDF(): PdfDownloadDTO =
        errorParser.safeCall("getFinalSurvivorPensionPDF") {
            val response = personalApiService.getFinalSurvivorPensionPDF()
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.readPdfChannel()
                )
            )
        }

    override suspend fun getGirlSurvivorReport(
        address: String,
        tel: String,
        postalCode: String,
        fatherName: String?,
        birthDate: Long?,
        insuranceId: String?,
        parentCode: String,
        pensionerId: String,
    ): PdfDownloadDTO =
        errorParser.safeCall("getGirlSurvivorReport") {
            val response = personalApiService.getGirlSurvivorReport(
                address = address,
                tel = tel,
                postalCode = postalCode,
                fatherName = fatherName,
                birthDate = birthDate,
                insuranceId = insuranceId,
                parentCode = parentCode,
                pensionerId = pensionerId,
            )
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.readPdfChannel()
                )
            )
        }

    override suspend fun confirmGirlSurvivor(body: ConfirmGirlSurvivorRequestDTO): String? =
        errorParser.safeCall("confirmGirlSurvivor") {
            personalApiService.confirmGirlSurvivor(body).extractMessage()
        }

    override suspend fun saveSurvivorInfo(body: SaveSurvivorInfoRequest): String? =
        errorParser.safeCall("saveSurvivorInfo") {
            personalApiService.saveSurvivorInfo(body).extractMessage()
        }

    override suspend fun putInsuredRegistrationDocList(
        personalId: String,
        body: List<InsuredDocDTO>
    ): String? =
        errorParser.safeCall("putInsuredRegistrationDocList") {
            personalApiService.putInsuredRegistrationDocList(personalId, body).extractData()
        }

    override suspend fun getInsuredRegistrationDocList(query: ApiQueryParamDN): List<InsuredDocDTO> =
        errorParser.safeCall("getInsuredRegistrationDocList") {
            personalApiService.getInsuredRegistrationDocList(
                queryBuilder.buildQuery(query)
            ).extractData().list ?: emptyList()
        }

    override suspend fun getRequestSummary(requestId: String): NewInsuredSummaryDTO? =
        errorParser.safeCall("getRequestSummary") {
            personalApiService.getRequestSummary(requestId).extractData()
        }
}


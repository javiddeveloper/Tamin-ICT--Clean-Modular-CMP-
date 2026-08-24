package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorRequestDTO
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface PersonalRemoteDataSource {
    suspend fun getPersonalInfo(): PersonalInfoDTO?
    suspend fun getDeceasedInfo(nationalId: String): DeceasedInfoDTO
    suspend fun getAge(birthDate: Long): AgeDTO
    suspend fun getDisabilityDependentInfo(query: ApiQueryParamDN): List<DisabilityDependentDTO>
    suspend fun confirmSurvivorsList(query: ApiQueryParamDN): List<ConfirmSurvivorDTO>
    suspend fun submitFinalSurvivorPension(requestId: Int, body: SubmitFinalSurvivorPensionRequest): String?
    suspend fun getFinalSurvivorPensionPDF(): PdfDownloadDTO
    suspend fun getGirlSurvivorReport(
        address: String,
        tel: String,
        postalCode: String,
        fatherName: String?,
        birthDate: Long?,
        insuranceId: String?,
        parentCode: String,
        pensionerId: String,
    ): PdfDownloadDTO
    suspend fun confirmGirlSurvivor(body: ConfirmGirlSurvivorRequestDTO): String?
    suspend fun saveSurvivorInfo(body: SaveSurvivorInfoRequest): String?
    suspend fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String): String?
    suspend fun putInsuredRegistrationDocList(personalId: String, body: List<InsuredDocDTO>): String?
    suspend fun getRequestSummary(requestId: String): NewInsuredSummaryDTO?
}

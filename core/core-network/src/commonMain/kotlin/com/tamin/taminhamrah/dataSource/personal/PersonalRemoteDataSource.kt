package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
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
    suspend fun getFinalSurvivorPensionPDF(token: String): PdfDownloadDTO
    suspend fun saveSurvivorInfo(body: SaveSurvivorInfoRequest): String?
    suspend fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String): String?
}

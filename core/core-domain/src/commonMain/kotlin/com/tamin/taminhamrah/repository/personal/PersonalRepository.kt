package com.tamin.taminhamrah.repository.personal

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalInfo(): Flow<PersonalInfoDN?>
    fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN>
    fun getAge(birthDate: Long): Flow<AgeDN>
    fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>>
    fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String): Flow<GirlSurvivorConditionDN>
    fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>>
    fun submitFinalSurvivorPension(requestId: Int, body: SubmitFinalSurvivorPensionDN): Flow<String?>
    fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?>
    fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN>
    suspend fun putInsuredRegistrationDocList(personalId: String, docs: List<InsuredDocDN>): String?
    suspend fun getRequestSummary(requestId: String): NewInsuredSummaryDN?
}

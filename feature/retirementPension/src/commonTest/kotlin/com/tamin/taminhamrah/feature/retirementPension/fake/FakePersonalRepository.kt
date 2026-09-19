package com.tamin.taminhamrah.feature.retirementPension.fake

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonalRepository : PersonalRepository {
    var personalInfoResult: PersonalInfoDN? = PersonalInfoDN(
        insuranceId = "12345678",
        branch = "0100",
        mobileNumber = "09123456789",
        provinceName = "تهران",
        personal = PersonalDN(
            firstName = "علی",
            lastName = "محمدی",
            fatherName = "حسن",
            nationalId = "0012345678",
            ssn = "11111111",
            genderDesc = "مرد",
            dateOfBirth = 0L,
        ),
    )

    override fun getPersonalInfo(refreshRemote: Boolean): Flow<PersonalInfoDN?> = flow {
        emit(personalInfoResult)
    }

    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> = flow {}
    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {}
    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> = flow { emit(emptyList()) }
    override fun getSurvivorList(deceasedNationalId: String): Flow<List<SurvivorDependentDN>> = flow { emit(emptyList()) }
    override fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String): Flow<GirlSurvivorConditionDN> = flow {}
    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> = flow { emit(emptyList()) }
    override fun submitFinalSurvivorPension(requestId: Int, body: SubmitFinalSurvivorPensionDN): Flow<String?> = flow { emit(null) }
    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> = flow { emit(null) }
    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> = flow {}
    override fun getGirlSurvivorReport(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> = flow {}
    override fun confirmGirlSurvivor(body: ConfirmGirlSurvivorDN): Flow<String?> = flow { emit(null) }
    override fun putInsuredRegistrationDocList(personalId: String, docs: List<InsuredDocDN>): Flow<String?> = flow { emit(null) }
    override fun getInsuredRegistrationDocList(personalId: String): Flow<List<InsuredDocDN>> = flow { emit(emptyList()) }
    override fun getRequestSummary(requestId: String): Flow<NewInsuredSummaryDN?> = flow { emit(null) }
}

package com.tamin.taminhamrah.feature.pensionSurvivor.fake

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
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

class FakePensionSurvivorPersonalRepository : PersonalRepository {
    var personalInfoResult: PersonalInfoDN? = null
    var ageResult: AgeDN? = null
    var deceasedInfoResult: DeceasedInfoDN? = null
    var survivorListResult: List<SurvivorDependentDN> = emptyList()
    var confirmSurvivorsListResult: List<ConfirmSurvivorDN> = emptyList()
    var saveSurvivorInfoResult: String? = "saved"
    var submitFinalSurvivorPensionResult: String? = "submitted"
    var pdfDownloadResult: PdfDownloadDN? = null

    var lastConfirmSurvivorsFilters: List<ApiFilterDN>? = null
    var lastSavedSurvivorInfoBody: SaveSurvivorInfoDN? = null
    var lastSubmitRequestId: Int? = null
    var lastSubmitBody: SubmitFinalSurvivorPensionDN? = null

    override fun getPersonalInfo(refreshRemote: Boolean): Flow<PersonalInfoDN?> = flow {
        emit(personalInfoResult)
    }

    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> = flow {
        deceasedInfoResult?.let { emit(it) }
    }

    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {
        ageResult?.let { emit(it) }
    }

    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> = flow {
        emit(emptyList())
    }

    override fun getSurvivorList(deceasedNationalId: String): Flow<List<SurvivorDependentDN>> = flow {
        emit(survivorListResult)
    }

    override fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String,
    ): Flow<GirlSurvivorConditionDN> = flow {
        error("Not needed in these tests")
    }

    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> = flow {
        lastConfirmSurvivorsFilters = filters
        emit(confirmSurvivorsListResult)
    }

    override fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionDN,
    ): Flow<String?> = flow {
        lastSubmitRequestId = requestId
        lastSubmitBody = body
        emit(submitFinalSurvivorPensionResult)
    }

    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> = flow {
        lastSavedSurvivorInfoBody = body
        emit(saveSurvivorInfoResult)
    }

    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> = flow {
        pdfDownloadResult?.let { emit(it) }
    }

    override fun getGirlSurvivorReport(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> = flow {
        error("Not needed in these tests")
    }

    override fun confirmGirlSurvivor(body: ConfirmGirlSurvivorDN): Flow<String?> = flow {
        error("Not needed in these tests")
    }

    override fun putInsuredRegistrationDocList(
        personalId: String,
        docs: List<InsuredDocDN>,
    ): Flow<String?> = flow {
        error("Not needed in these tests")
    }

    override fun getRequestSummary(requestId: String): Flow<NewInsuredSummaryDN?> = flow {
        emit(null)
    }
}

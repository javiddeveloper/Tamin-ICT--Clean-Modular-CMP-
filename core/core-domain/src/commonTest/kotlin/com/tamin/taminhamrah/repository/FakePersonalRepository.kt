package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonalRepository : PersonalRepository {
    var personalInfoResult: PersonalInfoDN? = null
    var ageResult: AgeDN? = null
    var deceasedInfoResult: DeceasedInfoDN? = null
    var submitFinalSurvivorPensionResult: String? = null
    var pdfDownloadResult: PdfDownloadDN? = null
    var girlSurvivorConditionResult: GirlSurvivorConditionDN? = null
    var girlSurvivorReportResult: PdfDownloadDN? = null
    var confirmGirlSurvivorResult: String? = null
    var saveSurvivorInfoResult: String? = null

    var shouldThrowError = false
    var disabilityDependentInfoResult: List<DisabilityDependentDN> = emptyList()
    var survivorListResult: List<SurvivorDependentDN> = emptyList()
    var confirmSurvivorsListResult: List<ConfirmSurvivorDN> = emptyList()
    var error: Throwable = RuntimeException("Personal Repository Error")

    override fun getPersonalInfo(refreshRemote: Boolean): Flow<PersonalInfoDN?> = flow {
        if (shouldThrowError) throw error
        emit(personalInfoResult)
    }

    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> = flow {
        if (shouldThrowError) throw error
        deceasedInfoResult?.let { emit(it) }
    }

    override fun getAge(birthDate: Long): Flow<AgeDN> = flow {
        if (shouldThrowError) throw error
        ageResult?.let { emit(it) }
    }

    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> =
        flow {
            if (shouldThrowError) throw error
            emit(disabilityDependentInfoResult)
        }

    override fun getSurvivorList(deceasedNationalId: String): Flow<List<SurvivorDependentDN>> = flow {
        if (shouldThrowError) throw error
        emit(survivorListResult)
    }

    override fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String
    ): Flow<GirlSurvivorConditionDN> = flow {
        if (shouldThrowError) throw error
        girlSurvivorConditionResult?.let { emit(it) }
    }

    override fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionDN
    ): Flow<String?> = flow {
        if (shouldThrowError) throw error
        emit(submitFinalSurvivorPensionResult)
    }

    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> = flow {
        if (shouldThrowError) throw error
        emit(confirmSurvivorsListResult)
    }

    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        pdfDownloadResult?.let { emit(it) }
    }

    override fun getGirlSurvivorReport(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        girlSurvivorReportResult?.let { emit(it) }
    }

    override fun confirmGirlSurvivor(body: ConfirmGirlSurvivorDN): Flow<String?> = flow {
        if (shouldThrowError) throw error
        emit(confirmGirlSurvivorResult)
    }

    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> = flow {
        if (shouldThrowError) throw error
        emit(saveSurvivorInfoResult)
    }

    var insuredRegistrationDocListResult: List<InsuredDocDN> = emptyList()
    override fun getInsuredRegistrationDocList(personalId: String): Flow<List<InsuredDocDN>> = flow {
        if (shouldThrowError) throw error
        emit(insuredRegistrationDocListResult)
    }

    var putInsuredRegistrationDocListResult: String? = "success"
    override fun putInsuredRegistrationDocList(
        personalId: String,
        docs: List<InsuredDocDN>
    ): Flow<String?> = flow {
        if (shouldThrowError) throw error
        emit(putInsuredRegistrationDocListResult)
}

    var requestSummaryResult: NewInsuredSummaryDN? = null
    override fun getRequestSummary(requestId: String): Flow<NewInsuredSummaryDN?> = flow {
        if (shouldThrowError) throw error
        emit(requestSummaryResult)
    }
}

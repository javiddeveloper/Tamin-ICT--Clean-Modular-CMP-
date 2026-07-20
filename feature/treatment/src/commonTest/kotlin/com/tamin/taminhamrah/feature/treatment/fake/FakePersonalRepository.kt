package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

/**
 * Only [getDisabilityDependentInfo] is exercised by the treatment tests: it is the source of the
 * spouse and children shown in the records patient filter. The rest return nothing.
 */
class FakePersonalRepository(
    private val family: List<DisabilityDependentDN> = emptyList(),
    private val shouldThrow: Boolean = false,
) : PersonalRepository {

    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> =
        flow {
            if (shouldThrow) throw IllegalStateException("subdominant failed")
            emit(family)
        }

    override fun getPersonalInfo(): Flow<PersonalInfoDN?> = emptyFlow()
    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> = emptyFlow()
    override fun getAge(birthDate: Long): Flow<AgeDN> = emptyFlow()
    override fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String,
    ): Flow<GirlSurvivorConditionDN> = emptyFlow()

    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> =
        emptyFlow()

    override fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionDN,
    ): Flow<String?> = emptyFlow()

    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> = emptyFlow()
    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> = emptyFlow()
}

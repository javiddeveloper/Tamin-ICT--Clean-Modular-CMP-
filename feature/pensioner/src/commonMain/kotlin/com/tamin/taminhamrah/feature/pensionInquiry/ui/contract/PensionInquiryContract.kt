package com.tamin.taminhamrah.feature.pensionInquiry.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.BeneficiaryPR
import com.tamin.taminhamrah.model.pension.EdictPensionerPR
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.model.pension.RecipientPR
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestPR
import com.tamin.taminhamrah.model.personal.AgePR
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import com.tamin.taminhamrah.model.personal.PersonalInfoPR
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorPR
import com.tamin.taminhamrah.model.request.ApiFilterDN

@Immutable
data class PensionInquiryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionList: List<PensionInquiryPR> = emptyList(),
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val recipients: List<RecipientPR> = emptyList(),
    val edictPensioner: EdictPensionerPR? = null,
    val personalInfo: PersonalInfoPR? = null,
    val beneficiaryList: List<BeneficiaryPR> = emptyList(),
    val deceasedInfo: DeceasedInfoPR? = null,
    val age: AgePR? = null,
    val disabilityDependentInfo: List<DisabilityDependentPR> = emptyList(),
    val confirmSurvivorsList: List<ConfirmSurvivorPR> = emptyList(),
    val payRoll: PayRollPR? = null,
    val disabilityPersonalInfo: DisabilityPersonalInfoPR? = null,
    val retirementRequestInfo: List<RetirementRequestPR> = emptyList(),
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class PensionListLoaded(val list: List<PensionInquiryPR>) : PartialState()
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState()
        data class BeneficiaryListLoaded(val list : List<BeneficiaryPR>) : PartialState()
        data class RecipientsLoaded(val list: List<RecipientPR>) : PartialState()
        data class EdictLoaded(val edict: EdictPensionerPR?) : PartialState()
        data class PersonalInfoLoaded(val personalInfo: PersonalInfoPR?) : PartialState()
        data class DeceasedInfoLoaded(val deceasedInfo: DeceasedInfoPR?) : PartialState()
        data class AgeLoaded(val age: AgePR?) : PartialState()
        data class UserAgeLoaded(val age: AgePR?) : PartialState()
        data class DisabilityDependentInfoLoaded(val list: List<DisabilityDependentPR>) : PartialState()
        data class ConfirmSurvivorsListLoaded(val list: List<ConfirmSurvivorPR>) : PartialState()
        data class PayRollLoaded(val payRoll: PayRollPR) : PartialState()
        data class DisabilityPersonalInfoLoaded(val disabilityPersonalInfo: DisabilityPersonalInfoPR?) : PartialState()
        data class RetirementRequestInfoLoaded(val list: List<RetirementRequestPR>) : PartialState()
    }
}

sealed class PensionInquiryIntent {
    data object LoadPensionInquiry : PensionInquiryIntent()
    data object LoadBeneficiaryList : PensionInquiryIntent()
    data object LoadPensionerIds : PensionInquiryIntent()
    data object LoadRecipients : PensionInquiryIntent()
    data object LoadPersonalInfo : PensionInquiryIntent()
    data object LoadDisabilityDependentInfo : PensionInquiryIntent()
    data object LoadConfirmSurvivorsList : PensionInquiryIntent()
    data object LoadDisabilityPersonalInfo : PensionInquiryIntent()
    data class LoadDeceasedInfo(val nationalId: String) : PensionInquiryIntent()
    data class LoadEdict(val pensionerId: String) : PensionInquiryIntent()
    data class LoadAge(val birthDate: Long) : PensionInquiryIntent()
    data object LoadUserAge : PensionInquiryIntent()
    data class LoadPensionerPayRoll(val filters: List<ApiFilterDN>) : PensionInquiryIntent()
    data object LoadRetirementRequestInfo : PensionInquiryIntent()
}

sealed class PensionInquiryEvent {
    data class ShowToast(val message: String) : PensionInquiryEvent()
}

package com.tamin.taminhamrah.feature.fractionContract.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.util.ValidationUtils

enum class FractionContractStep {
    Eligibility,
    Terms,
    UserInfo,
    Submit,
}

@Immutable
data class FractionContractState(
    val isLoading: Boolean = false,
    val currentStep: FractionContractStep = FractionContractStep.Eligibility,
    val registrationInfo: RegistrationInfoPR? = null,
    val eligibility: FractionEligibilityPR? = null,
    val isRulesConfirmed: Boolean = false,
    val userInfo: UserInfoFormPR = UserInfoFormPR(),
    val cities: List<CityPR> = emptyList(),
    val isCitiesLoading: Boolean = false,
    val isSavingContact: Boolean = false,
    val showGuideDialog: Boolean = false,
    val error: String? = null,
) {
    val isEligible: Boolean
        get() = eligibility?.eligibilityStatus in ELIGIBLE_STATUS_RANGE

    val isUserInfoComplete: Boolean
        get() = isUserInfoStepComplete(userInfo)

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class DataLoaded(
            val registrationInfo: RegistrationInfoPR,
            val eligibility: FractionEligibilityPR?,
            val userInfo: UserInfoFormPR,
        ) : PartialState()

        data class StepChanged(val step: FractionContractStep) : PartialState()
        data class RulesConfirmedChanged(val confirmed: Boolean) : PartialState()
        data class UserInfoChanged(val userInfo: UserInfoFormPR) : PartialState()
        data class CitiesLoading(val isLoading: Boolean) : PartialState()
        data class CitiesLoaded(val cities: List<CityPR>) : PartialState()
        data class SavingContact(val isSaving: Boolean) : PartialState()
        data class GuideDialogVisibility(val visible: Boolean) : PartialState()
        data class Error(val message: String) : PartialState()
    }

    companion object {
        val ELIGIBLE_STATUS_RANGE = 1..<5

        /** Legacy: `rulesAndRegulationsHtmlFile/rules_fraction_contract.pdf` */
        const val RULES_PDF_PATH = "files/rules_fraction_contract.pdf"
    }
}

sealed interface FractionContractIntent {
    data object InitData : FractionContractIntent
    data object OnBackClicked : FractionContractIntent
    data object OnGuideClicked : FractionContractIntent
    data object DismissGuideDialog : FractionContractIntent
    data object OnNextStepClicked : FractionContractIntent
    data object OnPreviousStepClicked : FractionContractIntent
    data class SetRulesConfirmed(val confirmed: Boolean) : FractionContractIntent
    data class UpdateUserInfo(val userInfo: UserInfoFormPR) : FractionContractIntent
}

sealed interface FractionContractEvent {
    data object NavigateBack : FractionContractEvent
    data class ShowToast(val message: String) : FractionContractEvent
}

internal fun isUserInfoStepComplete(userInfo: UserInfoFormPR): Boolean {
    // City picker can show a prefilled name from eligibility without a resolved cityCode.
    // SaveContact does not send cityId, so a non-blank name is enough to proceed.
    val zip = userInfo.zipCode.digitsOnly()
    val phone = userInfo.phoneNumber.digitsOnly()
    return userInfo.cityName.isNotBlank() &&
        userInfo.address.isNotBlank() &&
        zip.length == 10 &&
        ValidationUtils.isPostcodeValid(zip) &&
        ValidationUtils.isPhoneNumberValid(phone)
}

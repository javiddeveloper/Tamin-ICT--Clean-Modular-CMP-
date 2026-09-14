package com.tamin.taminhamrah.feature.fractionContract.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultPR
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
    val isFinalConfirmed: Boolean = false,
    val isSubmitting: Boolean = false,
    val submittedContract: FractionContractResultPR? = null,
    /** Legacy commitment uses today's Jalali date as start date. */
    val startDateLabel: String = "",
    /** City name used for change-detection after save (legacy `usersCity`). */
    val savedCityName: String = "",
    val showGuideDialog: Boolean = false,
    /** Legacy save-address success dialog; confirm advances to submit. */
    val showContactSavedDialog: Boolean = false,
    /** Legacy `onCheckContractCondition` hard-block dialog; dismiss navigates back. */
    val blockingErrorMessage: String? = null,
    val error: String? = null,
) {
    val isEligible: Boolean
        get() = eligibility?.eligibilityStatus in ELIGIBLE_STATUS_RANGE &&
            blockingErrorMessage == null

    val isUserInfoComplete: Boolean
        get() = isUserInfoStepComplete(userInfo)

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class DataLoaded(
            val registrationInfo: RegistrationInfoPR,
            val eligibility: FractionEligibilityPR?,
            val userInfo: UserInfoFormPR,
            val startDateLabel: String,
            val savedCityName: String,
        ) : PartialState()

        data class StepChanged(val step: FractionContractStep) : PartialState()
        data class RulesConfirmedChanged(val confirmed: Boolean) : PartialState()
        data class UserInfoChanged(val userInfo: UserInfoFormPR) : PartialState()
        data class CitiesLoading(val isLoading: Boolean) : PartialState()
        data class CitiesLoaded(val cities: List<CityPR>) : PartialState()
        data class SavingContact(val isSaving: Boolean) : PartialState()
        data class ContactSaved(
            val registrationInfo: RegistrationInfoPR,
            val savedCityName: String,
        ) : PartialState()
        data class SavedCityNameChanged(val savedCityName: String) : PartialState()
        data class ContactSavedDialogVisibility(val visible: Boolean) : PartialState()
        data class FinalConfirmedChanged(val confirmed: Boolean) : PartialState()
        data class Submitting(val isSubmitting: Boolean) : PartialState()
        data class ContractSubmitted(val result: FractionContractResultPR) : PartialState()
        data class GuideDialogVisibility(val visible: Boolean) : PartialState()
        data class BlockingError(val message: String) : PartialState()
        data class Error(val message: String) : PartialState()
    }

    companion object {
        val ELIGIBLE_STATUS_RANGE = 1..<5

        /** Legacy: `rulesAndRegulationsHtmlFile/rules_fraction_contract.pdf` */
        const val RULES_PDF_PATH = "files/rules_fraction_contract.pdf"

        /** Legacy confirmation text and summary always use 27%. */
        const val PREMIUM_RATE_DISPLAY = "27%"

        /**
         * Legacy `FractionRequestDataModel` default body (`premium = "this.premium"`).
         * Kept identical so the make-contract call matches production Android.
         */
        const val MAKE_CONTRACT_PREMIUM_BODY = "this.premium"

        /** [ContractPremiumType.FRACTION] / legacy `EnumInsuranceType.TYPE_FRACTION`. */
        const val PREMIUM_TYPE_CODE = "38"
    }
}

sealed interface FractionContractIntent {
    data object InitData : FractionContractIntent
    data object OnBackClicked : FractionContractIntent
    data object OnGuideClicked : FractionContractIntent
    data object DismissGuideDialog : FractionContractIntent
    data object DismissBlockingError : FractionContractIntent
    data object ConfirmContactSaved : FractionContractIntent
    data object OnNextStepClicked : FractionContractIntent
    data object OnPreviousStepClicked : FractionContractIntent
    data class SetRulesConfirmed(val confirmed: Boolean) : FractionContractIntent
    data class UpdateUserInfo(val userInfo: UserInfoFormPR) : FractionContractIntent
    data class SetFinalConfirmed(val confirmed: Boolean) : FractionContractIntent
    data object SubmitContract : FractionContractIntent
}

sealed interface FractionContractEvent {
    data object NavigateBack : FractionContractEvent
    data class ShowToast(val message: String) : FractionContractEvent
    data class ShowSubmitSuccess(
        val contractNumber: String,
        val contractDate: String,
    ) : FractionContractEvent
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

/**
 * Legacy `FractionContractFragment.onCheckContractCondition` hard-blocks before the stepper.
 * Returns a user-facing message, or null when the flow may continue.
 *
 * `isInsurance` must be explicitly `true` to proceed (legacy `if (!isInsurance)`).
 * Missing/null is treated as not primary insured — same as legacy Boolean default `false`.
 */
internal fun fractionEligibilityGateError(
    eligibility: FractionEligibilityPR?,
    notPrimaryInsuredMessage: String,
    under18Message: String,
    activeFractionMessage: String,
    unavailableMessage: String,
): String? {
    if (eligibility == null) return unavailableMessage
    if (eligibility.isInsurance != true) return notPrimaryInsuredMessage
    val ageYears = if (eligibility.newAge.length < 3) {
        0
    } else {
        eligibility.newAge.take(2).toIntOrNull() ?: 0
    }
    if (ageYears < 18) return under18Message
    if (eligibility.checkFractionMonthStatus != "1") {
        return eligibility.checkFractionMonthStatus.ifBlank { unavailableMessage }
    }
    if (eligibility.insuranceTypeCode == FractionContractState.PREMIUM_TYPE_CODE) {
        return activeFractionMessage
    }
    return null
}

package com.tamin.taminhamrah.feature.retirementPension.ui

import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentType
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionUiState
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementStep

/** Fixed-line numbers are eleven digits and start with the trunk `0`. */
internal const val PHONE_LENGTH = 11

/** Workshop codes are exactly ten digits. */
internal const val WORKSHOP_CODE_LENGTH = 10

/** Shorter than this and an address cannot name a street and a number. */
internal const val MIN_ADDRESS_LENGTH = 10

/**
 * The first thing wrong with the current step, or `null` when it is complete.
 *
 * A pure function of the state so the rules can be tested without a ViewModel, and so the reducer
 * can recompute them after every keystroke without touching composition. The order of the checks is
 * the order the design reports them in — only the first failure is ever shown.
 */
internal fun RetirementPensionUiState.stepError(): RetirementFormError? = when (step) {
    RetirementStep.Rules ->
        RetirementFormError.Consent.takeUnless { consentAccepted }

    RetirementStep.Authentication ->
        RetirementFormError.Authentication.takeUnless { otpVerified }

    RetirementStep.Identity -> identityError()

    RetirementStep.Workshop -> workshopError()

    // The design puts no validation on the history step: it only reports what the service says.
    RetirementStep.History -> null

    RetirementStep.IdentityDocuments -> RetirementFormError.IdentityDocuments.takeUnless {
        RetirementDocumentType.entries
            .filter(RetirementDocumentType::isIdentityDocument)
            .all { documents[it]?.isUploaded == true }
    }

    RetirementStep.QuitLetter -> RetirementFormError.QuitLetter.takeUnless {
        documents[RetirementDocumentType.QuitLetter]?.isUploaded == true
    }

    RetirementStep.Final ->
        RetirementFormError.FinalConfirm.takeUnless { finalConfirmed }
}

private fun RetirementPensionUiState.identityError(): RetirementFormError? {
    val trimmedAddress = address.trim()
    return when {
        phoneNumber.isEmpty() -> RetirementFormError.PhoneRequired
        phoneNumber.first() != '0' -> RetirementFormError.PhonePrefix
        phoneNumber.length < PHONE_LENGTH -> RetirementFormError.PhoneLength
        trimmedAddress.isEmpty() -> RetirementFormError.AddressRequired
        trimmedAddress.length < MIN_ADDRESS_LENGTH -> RetirementFormError.AddressShort
        !identityConfirmed -> RetirementFormError.IdentityConfirm
        else -> null
    }
}

private fun RetirementPensionUiState.workshopError(): RetirementFormError? = when {
    workshopName.isBlank() -> RetirementFormError.WorkshopName
    workshopCode.length != WORKSHOP_CODE_LENGTH -> RetirementFormError.WorkshopCode
    workshopAddress.trim().length < MIN_ADDRESS_LENGTH -> RetirementFormError.WorkshopAddress
    !workshopConfirmed -> RetirementFormError.WorkshopConfirm
    else -> null
}

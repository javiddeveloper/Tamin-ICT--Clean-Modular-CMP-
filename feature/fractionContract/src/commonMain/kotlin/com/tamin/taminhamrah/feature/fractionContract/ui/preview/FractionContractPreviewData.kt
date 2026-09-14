package com.tamin.taminhamrah.feature.fractionContract.ui.preview

import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR

internal object FractionContractPreviewData {
    val registrationInfo = RegistrationInfoPR(
        fullName = "رضا دریکوند",
        nationalId = "4060434061",
        birthDateFormatted = "1362/04/12",
        insuranceId = "0081631829",
        genderCode = "01",
        address = "تهران",
        zipCode = "1414657771",
        phoneNumber = "02166001234",
        mobileNumber = "09121234567",
        hasMobile = true,
        dateOfBirthEpoch = 427_939_200_000L,
    )

    val eligibleEligibility = FractionEligibilityPR(
        newAge = "430101",
        city = "تهران",
        provinceName = "تهران",
        provinceCode = "01",
        organizationAddress = "خیابان فاطمی، نبش خیابان رهی معیری، کدپستی ۵۷۷۷۱-۱۴۱۴۶",
        eligibilityStatus = 2,
        history = 120,
        isInsurance = true,
        checkFractionMonthStatus = "1",
        insuranceId = "0081631829",
        branchAddress = "غرب تهران – تهران، خیابان فاطمی، نبش خیابان رهی معیری، کدپستی ۵۷۷۷۱-۱۴۱۴۶",
    )

    val ineligibleEligibility = eligibleEligibility.copy(
        eligibilityStatus = 5,
        newAge = "550101",
    )

    val eligibleState = FractionContractState(
        registrationInfo = registrationInfo,
        eligibility = eligibleEligibility,
        currentStep = FractionContractStep.Eligibility,
    )

    val ineligibleState = FractionContractState(
        registrationInfo = registrationInfo,
        eligibility = ineligibleEligibility,
        currentStep = FractionContractStep.Eligibility,
    )

    val guideDialogState = eligibleState.copy(showGuideDialog = true)

    val termsStepState = eligibleState.copy(currentStep = FractionContractStep.Terms)

    val termsStepConfirmedState = termsStepState.copy(isRulesConfirmed = true)

    val userInfoStepState = eligibleState.copy(currentStep = FractionContractStep.UserInfo)

    val submitStepState = eligibleState.copy(currentStep = FractionContractStep.Submit)

    val loadingState = FractionContractState(isLoading = true)
}

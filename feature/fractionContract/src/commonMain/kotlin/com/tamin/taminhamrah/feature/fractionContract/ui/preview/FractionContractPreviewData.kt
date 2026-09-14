package com.tamin.taminhamrah.feature.fractionContract.ui.preview

import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR

internal object FractionContractPreviewData {
    val registrationInfo = RegistrationInfoPR(
        fullName = "رضا دریکوند",
        nationalId = "4060434061",
        birthDateFormatted = "1362/04/12",
        insuranceId = "0081631829",
        genderCode = "01",
        address = "تهران، خیابان فاطمی، کوچه رهی معیری، پلاک ۱۲",
        zipCode = "1414657771",
        phoneNumber = "02188974532",
        mobileNumber = "09143018372",
        hasMobile = true,
        dateOfBirthEpoch = 427_939_200_000L,
    )

    val cities = listOf(
        CityPR(cityCode = "021", cityName = "تهران", provinceCode = "01"),
        CityPR(cityCode = "051", cityName = "مشهد", provinceCode = "09"),
    )

    val userInfo = UserInfoFormPR(
        cityCode = "021",
        cityName = "تهران",
        address = registrationInfo.address,
        zipCode = registrationInfo.zipCode,
        phoneNumber = registrationInfo.phoneNumber,
        mobileNumber = registrationInfo.mobileNumber,
        showMobile = true,
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

    val userInfoStepState = eligibleState.copy(
        currentStep = FractionContractStep.UserInfo,
        userInfo = userInfo,
        cities = cities,
    )

    val submitStepState = eligibleState.copy(
        currentStep = FractionContractStep.Submit,
        startDateLabel = "1405/06/22",
    )

    val loadingState = FractionContractState(isLoading = true)
}

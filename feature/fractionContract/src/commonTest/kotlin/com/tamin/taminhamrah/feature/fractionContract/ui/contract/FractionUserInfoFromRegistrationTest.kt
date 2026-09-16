package com.tamin.taminhamrah.feature.fractionContract.ui.contract

import com.tamin.taminhamrah.feature.fractionContract.fake.FractionContractViewModelTestData
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.mapper.fractionContract.toPresentation
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.util.ValidationUtils
import kotlin.test.Test
import kotlin.test.assertTrue

class FractionUserInfoFromRegistrationTest {

    @Test
    fun initDataUserInfoShape_isComplete() {
        val registrationInfo = FractionContractViewModelTestData.registrationInfo.toPresentation()
        val eligibility = FractionContractViewModelTestData.eligibleEligibility.toPresentation()
        val userInfo = UserInfoFormPR.fromRegistration(registrationInfo).copy(
            cityCode = eligibility.cityCode,
            cityName = eligibility.city,
            zipCode = ValidationUtils.validatePostcode(registrationInfo.zipCode),
            phoneNumber = ValidationUtils.validateLandline(registrationInfo.phoneNumber),
        )

        assertTrue(isUserInfoStepComplete(userInfo), userInfo.toString())
    }
}

package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR

@Immutable
data class CityOptionPR(
    val code: String,
    val name: String,
)

@Immutable
data class UserInfoFormPR(
    val cityCode: String = "",
    val cityName: String = "",
    val address: String = "",
    val zipCode: String = "",
    val phoneNumber: String = "",
    val mobileNumber: String = "",
    val showMobile: Boolean = false,
) {
    val isValid: Boolean
        get() = cityCode.isNotBlank() &&
            cityName.isNotBlank() &&
            address.isNotBlank() &&
            zipCode.length >= 10 &&
            phoneNumber.isNotBlank()

    companion object {
        fun fromRegistration(info: RegistrationInfoPR): UserInfoFormPR = UserInfoFormPR(
            address = info.address,
            zipCode = info.zipCode,
            phoneNumber = info.phoneNumber,
            mobileNumber = info.mobileNumber,
            showMobile = info.hasMobile,
        )
    }
}

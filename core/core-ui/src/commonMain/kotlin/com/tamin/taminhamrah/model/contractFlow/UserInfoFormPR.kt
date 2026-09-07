package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UserInfoFormPR(
    val cityCode: String = "",
    val cityName: String = "",
    val address: String = "",
    val zipCode: String = "",
    val phoneNumber: String = "",
    val mobileNumber: String = "",
    val showMobile: Boolean = false,
) {
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

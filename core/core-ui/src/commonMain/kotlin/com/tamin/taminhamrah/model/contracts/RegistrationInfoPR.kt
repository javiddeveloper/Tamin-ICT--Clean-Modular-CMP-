package com.tamin.taminhamrah.model.contracts

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class RegistrationInfoPR(
    val fullName: String,
    val nationalId: String,
    val birthDateFormatted: String,
    val insuranceId: String,
    val genderTitle: String,
    val address: String,
    val zipCode: String,
    val phoneNumber: String,
    val mobileNumber: String,
    val hasMobile: Boolean,
)

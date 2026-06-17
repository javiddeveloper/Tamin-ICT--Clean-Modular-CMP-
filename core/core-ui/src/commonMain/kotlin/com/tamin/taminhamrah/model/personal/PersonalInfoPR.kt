package com.tamin.taminhamrah.model.personal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PersonalInfoPR(
    val insuranceId: String,
    val branch: String,
    val mobileNumber: String,
    val provinceName: String,
    val personal: PersonalPR?
)

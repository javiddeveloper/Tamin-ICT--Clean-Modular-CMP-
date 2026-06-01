package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class Gender(
    @SerialName("genderCode") val genderCode: String? = null,
    @SerialName("genderDesc") val genderDesc: String? = null,
    @SerialName("status") val status: Any? = null,
    @SerialName("statusDate") val statusDate: Any? = null,
)

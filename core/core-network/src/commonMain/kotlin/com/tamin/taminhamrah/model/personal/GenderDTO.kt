package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenderDTO(
    @SerialName("genderCode") val genderCode: String? = null,
    @SerialName("genderDesc") val genderDesc: String? = null,
    @SerialName("statusDate") val statusDate: String? = null,
    @SerialName("status") val status: String? = null,
)

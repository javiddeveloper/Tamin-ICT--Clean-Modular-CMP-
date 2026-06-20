package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenderDTO(
    @SerialName("genderCode") val genderCode: String?,
    @SerialName("genderDesc") val genderDesc: String?,
)

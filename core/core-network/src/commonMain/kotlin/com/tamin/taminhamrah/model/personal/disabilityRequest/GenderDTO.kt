package com.tamin.taminhamrah.model.personal.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenderDTO(
    @SerialName("genderCode") val genderCode: String?= null,
    @SerialName("genderDesc") val genderDesc: String?= null,
)

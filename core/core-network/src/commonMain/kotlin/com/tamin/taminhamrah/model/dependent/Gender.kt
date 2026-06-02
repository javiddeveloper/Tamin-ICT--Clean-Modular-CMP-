package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gender(
    @SerialName("genderCode") val genderCode: String? = null
)

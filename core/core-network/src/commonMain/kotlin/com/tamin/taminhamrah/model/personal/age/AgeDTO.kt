package com.tamin.taminhamrah.model.personal.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgeDTO(
    @SerialName("age") val age: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    )

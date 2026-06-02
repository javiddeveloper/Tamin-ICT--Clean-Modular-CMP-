package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubDominant(
    @SerialName("dateOfExpire") val dateOfExpire: String? = null
)

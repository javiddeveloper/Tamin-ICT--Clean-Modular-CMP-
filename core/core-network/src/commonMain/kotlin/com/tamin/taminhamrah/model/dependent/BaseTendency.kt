package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseTendency(
    @SerialName("tendencyCode") val tendencyCode: String? = null
)

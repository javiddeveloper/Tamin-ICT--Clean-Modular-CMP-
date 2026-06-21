package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseTendency(
    @SerialName("tendencyCode") val tendencyCode: String?,
    @SerialName("tendencyDescription") val tendencyDescription: String?,
)

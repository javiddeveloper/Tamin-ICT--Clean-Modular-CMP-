package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubRelationWithTamin(
    @SerialName("baseTendency") val baseTendency: BaseTendency? = null,
    @SerialName("relationDescription") val relationDescription: String? = null,
    @SerialName("status") val status: String? = null,
)

package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubRelationWithTamin(
    @SerialName("baseTendency")  val baseTendency: BaseTendency? = null
)

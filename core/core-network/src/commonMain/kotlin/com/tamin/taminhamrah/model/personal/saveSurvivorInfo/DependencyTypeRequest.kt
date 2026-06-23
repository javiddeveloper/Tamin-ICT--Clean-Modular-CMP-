package com.tamin.taminhamrah.model.personal.saveSurvivorInfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DependencyTypeRequest(
    @SerialName("code") val code: String? = null
)

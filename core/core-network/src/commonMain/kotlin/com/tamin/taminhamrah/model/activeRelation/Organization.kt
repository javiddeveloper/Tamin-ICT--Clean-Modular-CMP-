package com.tamin.taminhamrah.model.activeRelation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    @SerialName("actKey") val actKey: Int? = null,
    @SerialName("code") val code: String? = null,
    @SerialName("organizationName") val organizationName: String? = null,
    @SerialName("entityId") val entityId: String? = null,
    @SerialName("type") val type: String? = null,
)

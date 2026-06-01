package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class BailType(
    @SerialName("code") val code: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: String? = null,
)

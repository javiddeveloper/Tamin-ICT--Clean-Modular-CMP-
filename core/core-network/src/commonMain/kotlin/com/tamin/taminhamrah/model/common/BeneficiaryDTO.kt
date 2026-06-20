package com.tamin.taminhamrah.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficiaryDTO(
    @SerialName("bankCode") val bankCode: String? = null,
    @SerialName("bankName") val bankName: String? = null,
)

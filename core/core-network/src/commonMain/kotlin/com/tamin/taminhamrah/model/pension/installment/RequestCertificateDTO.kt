package com.tamin.taminhamrah.model.pension.installment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestCertificateDTO(
    @SerialName("refCode")
    val refCode: String? = null
)

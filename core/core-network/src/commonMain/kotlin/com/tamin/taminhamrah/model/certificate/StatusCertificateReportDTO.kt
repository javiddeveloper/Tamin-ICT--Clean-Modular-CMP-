package com.tamin.taminhamrah.model.certificate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusCertificateReportDTO(
    @SerialName("refCode") val refCode: String?
)

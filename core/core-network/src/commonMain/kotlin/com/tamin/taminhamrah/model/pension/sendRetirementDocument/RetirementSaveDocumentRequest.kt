package com.tamin.taminhamrah.model.pension.sendRetirementDocument

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetirementSaveDocumentRequest(
    @SerialName("pensionRequestDocList") val pensionRequestDocList: List<RetirementDocumentDTO>? = null,
    @SerialName("status") val status: String? = null
)

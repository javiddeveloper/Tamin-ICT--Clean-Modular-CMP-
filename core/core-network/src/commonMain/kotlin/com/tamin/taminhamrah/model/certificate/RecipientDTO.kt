package com.tamin.taminhamrah.model.certificate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipientDTO(
    @SerialName("recipientCode") val recipientCode: String?,
    @SerialName("recipientName") val recipientName: String?,
    @SerialName("operation") val operation: String?
)

package com.tamin.taminhamrah.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipientDTO(
   @SerialName("recipientCode") val recipientCode: String? = null,
   @SerialName("recipientName") val recipientName: String? = null,
)

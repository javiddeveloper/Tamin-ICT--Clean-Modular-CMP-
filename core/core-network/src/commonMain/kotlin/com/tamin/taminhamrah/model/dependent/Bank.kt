package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class Bank(
    @SerialName("bankCode") val bankCode: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: String? = null,
)

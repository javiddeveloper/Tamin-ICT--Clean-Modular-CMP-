package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class Accounttype(
    @SerialName("accountCode") val accountCode: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: Any? = null,
)

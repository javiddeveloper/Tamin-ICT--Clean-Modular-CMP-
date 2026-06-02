package com.tamin.taminhamrah.model.bankAccount

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BankAccountType(
    @SerialName("accountCode") var accountCode: String? = null,
    @SerialName("accountName") var accountName: String? = null,
    @SerialName("status") var status: String? = null
)

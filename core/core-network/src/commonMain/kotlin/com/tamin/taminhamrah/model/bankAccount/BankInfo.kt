package com.tamin.taminhamrah.model.bankAccount

import kotlinx.serialization.Serializable

@Serializable
data class BankInfo(
    var statusDate: String? = null,
    var bankCode: String? = null,
    var bankName: String? = null,
    var status: String? = null
)

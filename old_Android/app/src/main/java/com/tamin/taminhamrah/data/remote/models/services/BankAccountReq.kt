package com.tamin.taminhamrah.data.remote.models.services

data class BankAccountReq(
    var accountNumber: String? = null,
    var bank: String? = null,
    var accounttype: String? = null,
    var dateOfStart: String? = null,
)

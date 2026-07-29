package com.tamin.taminhamrah.data.remote.models.services

data class DeferredInstallmentReq
    (
    val bank: Bank? = null,
    val bankBranch: String? = null,
    val garanteeType: String? = null,
    val guaranteeAmount: Long? = 0,
    val installmentAmount: String? = null,
    val installmentCount: String? = null,
    val loanAmount: Long? = 0,
    val pensionerId: String? = null,
    val birthDate: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val nationalId: String? = null
)
data class Bank(
    val bankCode: String? = null
)

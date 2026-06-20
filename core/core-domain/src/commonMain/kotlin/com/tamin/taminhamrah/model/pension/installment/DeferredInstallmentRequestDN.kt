package com.tamin.taminhamrah.model.pension.installment

data class DeferredInstallmentRequestDN(
    val bank: BankDN? = null,
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

data class BankDN(
    val bankCode: String? = null
)

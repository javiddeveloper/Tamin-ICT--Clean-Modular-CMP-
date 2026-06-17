package com.tamin.taminhamrah.model.pension.installment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DeferredInstallmentRequestPR(
    val bank: BankPR? = null,
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

@Immutable
@Serializable
data class BankPR(
    val bankCode: String? = null
)

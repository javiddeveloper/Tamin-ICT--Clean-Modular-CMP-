package com.tamin.taminhamrah.model.pension.installment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeferredInstallmentRequest(
    @SerialName("bank") val bankDTO: BankDTO? = null,
    @SerialName("bankBranch") val bankBranch: String? = null,
    @SerialName("garanteeType") val garanteeType: String? = null,
    @SerialName("guaranteeAmount") val guaranteeAmount: Long? = 0,
    @SerialName("installmentAmount") val installmentAmount: String? = null,
    @SerialName("installmentCount") val installmentCount: String? = null,
    @SerialName("loanAmount") val loanAmount: Long? = 0,
    @SerialName("pensionerId") val pensionerId: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null
)
@Serializable
data class BankDTO(
    val bankCode: String? = null
)

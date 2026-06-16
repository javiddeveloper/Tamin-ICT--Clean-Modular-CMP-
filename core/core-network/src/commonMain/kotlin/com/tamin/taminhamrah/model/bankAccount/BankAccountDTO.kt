package com.tamin.taminhamrah.model.bankAccount

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BankAccountDTO(
    @SerialName("id")  val id: Long? = null,
    @SerialName("dateOfFinish")  val dateOfFinish: Long? = null,
    @SerialName("creationTime")  val creationTime: String? = null,
    @SerialName("lastModificationTime")  val lastModificationTime: String? = null,
    @SerialName("lastModifiedBy")  val lastModifiedBy: String? = null,
    @SerialName("accounttype")  val accountType: BankAccountType? = null,
    @SerialName("personal")  val personal: Int? = null,
    @SerialName("accountNumber")  val accountNumber: String? = null,
    @SerialName("isValidAccount")  val isValidAccount: Boolean? = null,
    @SerialName("confirmed")  val confirmed: Boolean? = null,
    @SerialName("organizationId")  val organizationId: String? = null,
    @SerialName("bank")  val bank: BankInfo? = null,
    @SerialName("createdBy")  val createdBy: String? = null,
    @SerialName("dateOfStart")  val dateOfStart: Long? = null,
)

package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class Account(
    @SerialName("id") val id: Int? = null,
    @SerialName("accountNumber") val accountNumber: String? = null,
    @SerialName("accountstatus") val accountStatus: Any? = null,
    @SerialName("accounttype") val accountType: Accounttype? = null,
    @SerialName("bank") val bank: Bank? = null,
    @SerialName("branch") val branch: Any? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("createdBy") val createdBy: Any? = null,
    @SerialName("createdUser") val createdUser: Any? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("dateOfFinish") val dateOfFinish: Any? = null,
    @SerialName("dateOfStart") val dateOfStart: Long? = null,
    @SerialName("deleted") val deleted: Any? = null,
    @SerialName("isValidAccount") val isValidAccount: Any? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: Any? = null,
    @SerialName("lastModifiedUser") val lastModifiedUser: Any? = null,
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("personal") val personal: Int? = null,
    @SerialName("shebaNumber") val shebaNumber: Any? = null,
)

package com.tamin.taminhamrah.model.bankAccount

data class BankAccountDN(
    val dateOfFinish: Long?,
    val creationTime: String?,
    val lastModificationTime: String?,
    val lastModifiedBy: String?,
    val accounType: AccountTypeDN?,
    val personal: Int?,
    val accountNumber: String?,
    val isValidAccount: Boolean?,
    val confirmed: Boolean?,
    val organizationId: String?,
    val bank: BankInfoDN?,
    val createdBy: String?,
    val dateOfStart: Long?,
    val id: Long?,
)

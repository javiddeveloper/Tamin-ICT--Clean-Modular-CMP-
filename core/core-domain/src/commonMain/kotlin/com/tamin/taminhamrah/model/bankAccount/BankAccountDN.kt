package com.tamin.taminhamrah.model.bankAccount

data class BankAccountDN(
    var dateOfFinish: Long? = null,
    var creationTime: String? = null,
    var lastModificationTime: String? = null,
    var lastModifiedBy: String? = null,
    var accounType: AccountTypeDN? = null,
    var personal: Int? = null,
    var accountNumber: String? = null,
    var isValidAccount: Boolean? = null,
    var confirmed: Boolean? = null,
    var organizationId: String? = null,
    var bank: BankInfoDN? = null,
    var createdBy: String? = null,
    var dateOfStart: Long? = null,
    var id: Long? = null,
)

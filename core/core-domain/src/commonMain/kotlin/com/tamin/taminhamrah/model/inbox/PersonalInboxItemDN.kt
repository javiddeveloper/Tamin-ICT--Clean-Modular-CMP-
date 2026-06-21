package com.tamin.taminhamrah.model.inbox

data class PersonalInboxItemDN(
    val id: Long,
    val nationalCode: String?,
    val mobileNumber: String?,
    val email: String?,
    val read: String?,
    val data: String?,
    val sentDate: Long?,
    val receiveDate: Long?,
    val seenDate: Long?,
    val seen: Boolean?,
    val hasImage: Boolean?,
    val hasText: Boolean?,
    val hasPdf: Boolean?,
    val updateable: Boolean?,
    val status: String?,
    val referenceId: String?,
    val pdf: String?,
    val type: InboxTypeDN?,
    val subType: InboxTypeDN?,
    val permission: InboxPermissionDN?,
)

data class InboxTypeDN(
    val typeDesc: String?,
    val typeCode: String?,
)

data class InboxPermissionDN(
    val password: Long?,
    val dateFrom: Long?,
    val dateTo: Long?,
)

data class PersonalInboxSizeDN(
    val usage: String?,
    val total: String?,
)

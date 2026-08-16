package com.tamin.taminhamrah.model.userRequest

data class UserRequestDN(
    val id: Long,
    val refCode: String?,
    val title: String?,
    val comment: String?,
    val creationTime: Long?,
    val createByName: String?,
    val status: UserRequestStatusDN?,
    val requestType: UserRequestTypeDN?,
    val referenceId: String?,
    val requestDetails: String? = null,
    val details: UserRequestDetailsDN? = null,
)

data class UserRequestStatusDN(
    val requestCode: String?,
    val requestDesc: String?,
)

data class UserRequestTypeDN(
    val id: Long?,
    val title: String?,
    val description: String?,
)

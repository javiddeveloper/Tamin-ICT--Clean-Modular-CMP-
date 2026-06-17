package com.tamin.taminhamrah.model.request

data class RequestDN(
    val id: Long,
    val refCode: String?,
    val title: String?,
    val comment: String?,
    val creationTime: Long?,
    val createByName: String?,
    val status: RequestStatusDN?,
    val requestType: RequestTypeDN?,
    val referenceId: String?,
)

data class RequestStatusDN(
    val requestCode: String?,
    val requestDesc: String?,
)

data class RequestTypeDN(
    val id: Long?,
    val title: String?,
    val description: String?,
)

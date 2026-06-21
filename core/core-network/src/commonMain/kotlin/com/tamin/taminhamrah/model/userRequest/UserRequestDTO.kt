package com.tamin.taminhamrah.model.userRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRequestDTO(
    @SerialName("id") val id: Long?,
    @SerialName("operation") val operation: String?,
    @SerialName("createdBy") val createdBy: String?,
    @SerialName("creationTime") val creationTime: Long?,
    @SerialName("lastModifiedBy") val lastModifiedBy: String?,
    @SerialName("lastModificationTime") val lastModificationTime: Long?,
    @SerialName("refCode") val refCode: String?,
    @SerialName("userName") val userName: String?,
    @SerialName("status") val status: UserRequestStatusDTO?,
    @SerialName("title") val title: String?,
    @SerialName("comment") val comment: String?,
    @SerialName("template") val template: String?,
    @SerialName("requestType") val requestType: UserRequestTypeDTO?,
    @SerialName("deliverCode") val deliverCode: String?,
    @SerialName("refrenceid") val referenceId: String?,
    @SerialName("requestDetails") val requestDetails: String?,
    @SerialName("requestChid") val requestChid: String?,
    @SerialName("fullName") val fullName: String?,
    @SerialName("createByName") val createByName: String?,
)

@Serializable
data class UserRequestStatusDTO(
    @SerialName("operation") val operation: String?,
    @SerialName("requestCode") val requestCode: String?,
    @SerialName("requestDesc") val requestDesc: String?,
)

@Serializable
data class UserRequestTypeDTO(
    @SerialName("operation") val operation: String? ,
    @SerialName("createdBy") val createdBy: String?,
    @SerialName("creationTime") val creationTime: Long?,
    @SerialName("lastModifiedBy") val lastModifiedBy: String?,
    @SerialName("lastModificationTime") val lastModificationTime: Long?,
    @SerialName("id") val id: Long? ,
    @SerialName("title") val title: String?,
    @SerialName("description") val description: String?,
)

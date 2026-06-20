package com.tamin.taminhamrah.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRequestDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("operation") val operation: String? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("refCode") val refCode: String? = null,
    @SerialName("userName") val userName: String? = null,
    @SerialName("status") val status: UserRequestStatusDTO? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("comment") val comment: String? = null,
    @SerialName("template") val template: String? = null,
    @SerialName("requestType") val requestType: UserRequestTypeDTO? = null,
    @SerialName("deliverCode") val deliverCode: String? = null,
    @SerialName("refrenceid") val referenceId: String? = null,
    @SerialName("requestDetails") val requestDetails: String? = null,
    @SerialName("requestChid") val requestChid: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("createByName") val createByName: String? = null,
)

@Serializable
data class UserRequestStatusDTO(
    @SerialName("operation") val operation: String? = null,
    @SerialName("requestCode") val requestCode: String? = null,
    @SerialName("requestDesc") val requestDesc: String? = null,
)

@Serializable
data class UserRequestTypeDTO(
    @SerialName("operation") val operation: String? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("id") val id: Long? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
)

package com.tamin.taminhamrah.model.inbox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PersonalInboxListDTO(
    @SerialName("total") val total: String?,
    @SerialName("list") val list: List<PersonalInboxItemDTO>?,
)

@Serializable
data class PersonalInboxItemDTO(
    @SerialName("id") val id: Long?,
    @SerialName("nationalCode") val nationalCode: String?,
    @SerialName("mobileNumber") val mobileNumber: String?,
    @SerialName("email") val email: String?,
    @SerialName("read") val read: String?,
    @SerialName("data") val data: JsonElement?,
    @SerialName("sentDate") val sentDate: Long?,
    @SerialName("receiveDate") val receiveDate: Long?,
    @SerialName("seenDate") val seenDate: Long?,
    @SerialName("seen") val seen: Boolean?,
    @SerialName("hasImage") val hasImage: Boolean?,
    @SerialName("hasText") val hasText: Boolean?,
    @SerialName("hasPDF") val hasPdf: Boolean?,
    @SerialName("updateable") val updateable: Boolean?,
    @SerialName("status") val status: String?,
    @SerialName("refrenceId") val referenceId: JsonElement?,
    @SerialName("pdf") val pdf: JsonElement?,
    @SerialName("type") val type: InboxTypeDTO?,
    @SerialName("subType") val subType: InboxTypeDTO?,
    @SerialName("permission") val permission: InboxPermissionDTO?,
)

@Serializable
data class InboxTypeDTO(
    @SerialName("typeDesc") val typeDesc: String?,
    @SerialName("typeCode") val typeCode: String?,
)

@Serializable
data class InboxPermissionDTO(
    @SerialName("password") val password: Long?,
    @SerialName("dateFrom") val dateFrom: Long?,
    @SerialName("dateTo") val dateTo: Long?,
    @SerialName("id") val id: Int?,
)

@Serializable
data class PersonalInboxSizeDTO(
    @SerialName("usage") val usage: String?,
    @SerialName("total") val total: String?,
)

@Serializable
data class InboxInquiryRequestDTO(
    @SerialName("operation") val operation: String,
    @SerialName("permission") val permission: InboxPermissionRequestDTO? = null
)

@Serializable
data class InboxPermissionRequestDTO(
    @SerialName("operation") val operation: String
)

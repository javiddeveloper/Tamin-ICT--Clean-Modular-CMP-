package com.tamin.taminhamrah.model.inbox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PersonalInboxListDTO(
    @SerialName("total") val total: String? = null,
    @SerialName("list") val list: List<PersonalInboxItemDTO>? = null,
)

@Serializable
data class PersonalInboxItemDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("mobileNumber") val mobileNumber: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("read") val read: String? = null,
    @SerialName("data") val data: JsonElement? = null,
    @SerialName("sentDate") val sentDate: Long? = null,
    @SerialName("receiveDate") val receiveDate: Long? = null,
    @SerialName("seenDate") val seenDate: Long? = null,
    @SerialName("seen") val seen: Boolean? = null,
    @SerialName("hasImage") val hasImage: Boolean? = null,
    @SerialName("hasText") val hasText: Boolean? = null,
    @SerialName("hasPDF") val hasPdf: Boolean? = null,
    @SerialName("updateable") val updateable: Boolean? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("refrenceId") val referenceId: JsonElement? = null,
    @SerialName("pdf") val pdf: JsonElement? = null,
    @SerialName("type") val type: InboxTypeDTO? = null,
    @SerialName("subType") val subType: InboxTypeDTO? = null,
    @SerialName("permission") val permission: InboxPermissionDTO? = null,
)

@Serializable
data class InboxTypeDTO(
    @SerialName("typeDesc") val typeDesc: String? = null,
    @SerialName("typeCode") val typeCode: String? = null,
)

@Serializable
data class InboxPermissionDTO(
    @SerialName("password") val password: Long? = null,
    @SerialName("dateFrom") val dateFrom: Long? = null,
    @SerialName("dateTo") val dateTo: Long? = null,
    @SerialName("id") val id: Int? = null,
)

@Serializable
data class PersonalInboxSizeDTO(
    @SerialName("usage") val usage: String? = null,
    @SerialName("total") val total: String? = null,
)

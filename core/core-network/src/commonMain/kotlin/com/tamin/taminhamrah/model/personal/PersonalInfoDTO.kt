package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfoDTO(
    @SerialName("request") val request: RequestDTO? ,
    @SerialName("creationTime") val creationTime: Long? ,
    @SerialName("endDate") val endDate: Long? ,
    @SerialName("lastModificationTime") val lastModificationTime: Long? ,
    @SerialName("confirmed") val confirmed: Boolean? ,
    @SerialName("branch") val branch: String? ,
    @SerialName("organizationId") val organizationId: String? ,
    @SerialName("insuranceId") val insuranceId: String? ,
    @SerialName("id") val id: Long? ,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? ,
    @SerialName("personal") val personal: PersonalDTO? ,
    @SerialName("deleted") val deleted: Boolean? ,
    @SerialName("createdBy") val createdBy: String? ,
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTaminDTO? ,
    @SerialName("provinceName") val provinceName: String? ,
    @SerialName("mobileNumber") val mobileNumber: String? ,
)

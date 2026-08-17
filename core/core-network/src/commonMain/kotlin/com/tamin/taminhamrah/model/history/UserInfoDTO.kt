package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDTO(
    @SerialName("serial1") val serial1: String?,
    @SerialName("militaryServiceCode") val militaryServiceCode: String?,
    @SerialName("fatherName") val fatherName: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("serial2") val serial2: String?,
    @SerialName("creationTime") val creationTime: Long?,
    @SerialName("lastModificationTime") val lastModificationTime: Long?,
    @SerialName("cityCode") val cityCode: String?,
    @SerialName("socialSecurityNumber") val socialSecurityNumber: String?,
    @SerialName("lastModifiedBy") val lastModifiedBy: String?,
    @SerialName("issueplaceName") val issueplaceName: String?,
    @SerialName("birthDate") val birthDate: String?,
    @SerialName("firstName") val firstName: String?,
    @SerialName("insuranceNumber") val insuranceNumber: String?,
    @SerialName("genderCode") val genderCode: String?,
    @SerialName("nationalID") val nationalID: String?,
    @SerialName("marriageCode") val marriageCode: String?,
    @SerialName("createdBy") val createdBy: String?,
    @SerialName("identityNumber") val identityNumber: String?,
    @SerialName("countryCode") val countryCode: String?,
    @SerialName("id") val id: String?,
    @SerialName("birthDateTimestamp") val birthDateTimestamp: Long?,
    @SerialName("issueplace") val issueplace: String?,
    @SerialName("nationCode") val nationCode: String?
)

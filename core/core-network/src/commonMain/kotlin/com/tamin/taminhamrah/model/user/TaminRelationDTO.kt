package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaminRelationDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("identityId") val identityId: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("otherDesc") val otherDesc: String? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("lastMonthWork") val lastMonthWork: String? = null,
    @SerialName("isuType") val isuType: String? = null,
    @SerialName("isuStatus") val isuStatus: String? = null,
    @SerialName("isuTypeDesc") val isuTypeDesc: String? = null,
    @SerialName("isuStatusDesc") val isuStatusDesc: String? = null,
    @SerialName("relationType") val relationType: String? = null,
    @SerialName("relationTypeDesc") val relationTypeDesc: String? = null,
    @SerialName("relationWithTaminId") val relationWithTaminId: String? = null,
    @SerialName("relationStartDate") val relationStartDate: String? = null,
    @SerialName("brhCode") val brhCode: String? = null,
    @SerialName("brhName") val brhName: String? = null,
    @SerialName("brhAdress") val brhAdress: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("tell") val tell: String? = null,
    @SerialName("workAddress") val workAddress: String? = null,
    @SerialName("workTel") val workTel: String? = null,
    @SerialName("employerMobile") val employerMobile: String? = null,
    @SerialName("employerName") val employerName: String? = null,
    @SerialName("bookletDate") val bookletDate: String? = null,
    @SerialName("idCityName") val idCityName: String? = null,
    @SerialName("parentRisuId") val parentRisuId: String? = null,
    @SerialName("parentNationalId") val parentNationalId: String? = null,
    @SerialName("pensionerId") val pensionerId: String? = null,
    @SerialName("parentLastName") val parentLastName: String? = null,
    @SerialName("parentFirstName") val parentFirstName: String? = null,
    @SerialName("parentFatherName") val parentFatherName: String? = null,
    @SerialName("parentIdNumber") val parentIdNumber: String? = null,
    @SerialName("parentBirthDate") val parentBirthDate: String? = null,
    @SerialName("parentIdCityName") val parentIdCityName: String? = null,
    @SerialName("dependenceType") val dependenceType: String? = null,
    @SerialName("noBooklet") val noBooklet: String? = null,
    @SerialName("haveDarman") val haveDarman: String? = null,
    @SerialName("isuCityCode") val isuCityCode: String? = null,
    @SerialName("isuCityName") val isuCityName: String? = null,
    @SerialName("idCityCode") val idCityCode: String? = null,
    @SerialName("parentDeathDate") val parentDeathDate: String? = null
) {
    fun fullName(): String {
        return "${firstName ?: ""} ${lastName ?: ""}".trim()
    }
}

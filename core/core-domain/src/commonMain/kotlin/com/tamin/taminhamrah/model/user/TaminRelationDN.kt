package com.tamin.taminhamrah.model.user

import kotlinx.serialization.Serializable

@Serializable
data class TaminRelationDN(
    val id: Int? = null,
    val nationalId: String? = null,
    val insuranceId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fatherName: String? = null,
    val identityId: String? = null,
    val birthDate: String? = null,
    val otherDesc: String? = null,
    val workshopId: String? = null,
    val workshopName: String? = null,
    val lastMonthWork: String? = null,
    val isuType: String? = null,
    val isuStatus: String? = null,
    val isuTypeDesc: String? = null,
    val isuStatusDesc: String? = null,
    val relationType: String? = null,
    val relationTypeDesc: String? = null,
    val relationWithTaminId: String? = null,
    val relationStartDate: String? = null,
    val brhCode: String? = null,
    val brhName: String? = null,
    val brhAdress: String? = null,
    val address: String? = null,
    val tell: String? = null,
    val workAddress: String? = null,
    val workTel: String? = null,
    val employerMobile: String? = null,
    val employerName: String? = null,
    val bookletDate: String? = null,
    val idCityName: String? = null,
    val parentRisuId: String? = null,
    val parentNationalId: String? = null,
    val pensionerId: String? = null,
    val parentLastName: String? = null,
    val parentFirstName: String? = null,
    val parentFatherName: String? = null,
    val parentIdNumber: String? = null,
    val parentBirthDate: String? = null,
    val parentIdCityName: String? = null,
    val dependenceType: String? = null,
    val noBooklet: String? = null,
    val haveDarman: String? = null,
    val isuCityCode: String? = null,
    val isuCityName: String? = null,
    val idCityCode: String? = null,
    val parentDeathDate: String? = null
) {
    val fullName: String
        get() = "${firstName ?: ""} ${lastName ?: ""}".trim()
}

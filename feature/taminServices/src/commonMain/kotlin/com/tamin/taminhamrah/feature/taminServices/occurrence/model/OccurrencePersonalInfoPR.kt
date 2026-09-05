package com.tamin.taminhamrah.feature.taminServices.occurrence.model

data class OccurrencePersonalInfoPR(
    val nationalCode: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fatherName: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val insuranceNumber: String = "",
    val branchCode: String = "",
    val nationality: String = "",
    val insuranceType: String = "",
) {
    val fullName: String get() = "$firstName $lastName"
}

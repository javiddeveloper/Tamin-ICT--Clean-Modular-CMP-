package com.tamin.taminhamrah.core.model.common

data class IdentityInfoDN(
    val cityOfBirthId: String?,
    val cityOfIssueId: String?,
    val countryId: String?,
    val dateOfBirth: Long?,
    val fatherName: String?,
    val firstName: String?,
    val gender: String?,
    val id: Int?,
    val idCardNumber: String?,
    val idCardSerial1: String?,
    val idCardSerial2: String?,
    val lastName: String?,
    val nationalId: String?,
    val ssn: String?
) {
    var cityOfBirthName: String? = null
    var cityOfIssueName: String? = null
}



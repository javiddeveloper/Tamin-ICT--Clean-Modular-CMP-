package com.tamin.taminhamrah.model.identity

import kotlinx.serialization.Serializable

@Serializable
data class IdentityInfoPR(
    val cityOfBirthId: String,
    val cityOfIssueId: String,
    val countryId: String,
    val dateOfBirth: Long,
    val dateOfBirthFormatted: String,
    val fatherName: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val gender: String,
    val genderDisplay: String,
    val id: Int,
    val idCardNumber: String,
    val idCardSerial: String,
    val nationalId: String,
    val ssn: String,
    val cityOfBirthName: String,
    val cityOfIssueName: String
)

package com.tamin.taminhamrah.model.identity

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
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
    val id: Int,
    val idCardNumber: String,
    val idCardSerial: String,
    val idCardSerial1: String = "",
    val idCardSerial2: String = "",
    val nationalId: String,
    val ssn: String,
    val cityOfBirthName: String,
    val cityOfIssueName: String
)

package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "identity_info")
data class IdentityInfoEntity(
    @PrimaryKey
    val id: Int?,
    val cityOfBirthId: String?,
    val cityOfIssueId: String?,
    val countryId: String?,
    val dateOfBirth: Long?,
    val fatherName: String?,
    val firstName: String?,
    val gender: String?,
    val idCardNumber: String?,
    val idCardSerial1: String?,
    val idCardSerial2: String?,
    val lastName: String?,
    val nationalId: String?,
    val ssn: String?,
)

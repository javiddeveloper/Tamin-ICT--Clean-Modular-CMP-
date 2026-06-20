package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_info")
data class PersonalInfoEntity(
    @PrimaryKey
    val insuranceId: String,
    val branch: String?,
    val mobileNumber: String?,
    val provinceName: String?,
    val firstName: String?,
    val lastName: String?,
    val fatherName: String?,
    val nationalId: String?,
    val ssn: String?,
    val genderDesc: String?,
    val dateOfBirth: Long?
)

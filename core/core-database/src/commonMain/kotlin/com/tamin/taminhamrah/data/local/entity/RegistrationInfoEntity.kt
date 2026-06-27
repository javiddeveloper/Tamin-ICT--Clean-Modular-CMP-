package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registration_info")
data class RegistrationInfoEntity(
    @PrimaryKey
    val id: Int = SINGLE_ROW_ID,
    val insuranceId: String?,
    val insuranceIdValidity: Boolean,
    val mobileNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val dateOfBirth: Long?,
    val genderCode: String?,
    val genderDesc: String?,
    val ssn: String?,
    val contactAddress: String?,
    val contactZipCode: String?,
    val contactMobile: String?,
    val contactPhoneNumber: String?,
) {
    companion object {
        const val SINGLE_ROW_ID = 1
    }
}

package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model

import androidx.compose.runtime.Immutable

/** Presentation model for the funeral-allowance ("کمک هزینه مراسم ترحیم") info screen. */
data class FuneralAllowanceInfoPR(
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val insuranceNumber: String,
    val bankAccount: String,
    val bankName: String,
    val mobileNumber: String,
    val branchName: String,
    val branchCode: String,
    val nationalCode: String,
    val deceasedNationalId: String,
    val requestHelpType: String,
    val hasBankAccountIssue: Boolean,
    val registeredRequest: RegisteredFuneralRequestPR?,
)

data class RegisteredFuneralRequestPR(
    val requestId: Long,
    val deceasedNationalId: String,
    val deathDate: String,        // formatted Persian date, or "" when unknown
    val requestDate: String,      // formatted Persian date, or "" when unknown
    val statusName: String,
)

data class DeceasedValidationPR(
    val deceasedFullName: String,
    val relationship: String,
    val isEligible: Boolean,
    val message: String,
    val dependentStatus: String,
    val deathDate: String,
)

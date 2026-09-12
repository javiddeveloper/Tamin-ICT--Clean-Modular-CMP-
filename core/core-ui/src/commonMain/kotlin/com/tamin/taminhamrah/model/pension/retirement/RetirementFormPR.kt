package com.tamin.taminhamrah.model.pension.retirement

import androidx.compose.runtime.Immutable

/**
 * The read-only blocks of the retirement-pension request, already formatted for display.
 *
 * Held as separate small models rather than one wide one so a step composable takes only the block
 * it draws: typing in the address must not recompose the workshop card.
 *
 * Every field is a formatted `String` — the digits are converted at the presentation edge, so
 * nothing here is arithmetic anymore.
 */
@Immutable
data class RetirementInsuredPR(
    val fullName: String,
    val insuranceNumber: String,
    val nationalCode: String,
    /** Age in Persian digits, as separate parts: the surrounding sentence is a string resource. */
    val ageYears: String,
    val ageMonths: String,
    val branchName: String,
)

@Immutable
data class RetirementIdentityPR(
    val fatherName: String,
    val idNumber: String,
    val gender: String,
    val birthDate: String,
    val issuePlace: String,
    val mobileNumber: String,
    /** Raw epoch birthdate, carried through to the create-request body. */
    val birthDateEpoch: Long,
    /** Raw gender code (`"01"` male), which is what the server wants back. */
    val genderCode: String,
    val firstName: String,
    val lastName: String,
    val nationalCode: String,
)

@Immutable
data class RetirementBranchInfoPR(
    val branchName: String,
    val branchCode: String,
)

@Immutable
data class RetirementHistoryPR(
    /** Grouped, Persian-digit rial figures ready to print. */
    val averageWage: String,
    val estimatedPension: String,
    val years: String,
    val months: String,
    val days: String,
    val totalDays: String,
    /** Raw year/month counts, for the one-line summary on the final step. */
    val yearsValue: Int,
    val monthsValue: Int,
)

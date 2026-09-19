package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/**
 * Presentation models for خدمات غیرحضوری کارفرما (Employer → Online Services).
 *
 * Everything a label shows is already formatted — Persian digits, Jalali dates separated, missing
 * values dashed — so a composable never calls a formatter per frame. Raw identity fields
 * ([WorkshopWithoutContractPR.workshopId] / `branchCode`) are kept unformatted because they travel
 * to the next screen and into query parameters.
 */

/** The کارفرما identity block on stepper step 2. */
@Immutable
data class EmployerContactInfoPR(
    val fullName: String = "",
    val nationalCode: String = "",
    /** The employer's *currently registered* mobile, shown against the requested one. */
    val currentMobile: String = "",
    /** The employer's *currently registered* email, shown against the requested one. */
    val currentEmail: String = "",
)

/** One کارگاه بدون قرارداد card on stepper step 2. */
@Immutable
data class WorkshopWithoutContractPR(
    val workshopId: String = "",
    val branchCode: String = "",
    val hasIdentity: Boolean = false,
    val name: String = "",
    /** [workshopId] as the card prints it. */
    val codeLabel: String = "",
    val nationalId: String = "",
    val postalCode: String = "",
    val tel: String = "",
    val address: String = "",
    val branchOfficeName: String = "",
)

/** One پیمانکار / contract row of a workshop. */
@Immutable
data class WorkshopContractRowPR(
    val contractRow: String = "",
    val fullName: String = "",
    val nationalCode: String = "",
    val mobile: String = "",
    val email: String = "",
    val tel: String = "",
    val postalCode: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val workshopName: String = "",
    val workshopCodeLabel: String = "",
)

/** One registered employer-agreement of a workshop (management side). */
@Immutable
data class EmployerAgreementByWorkshopPR(
    val workshopId: String = "",
    val branchCode: String = "",
    val paymentSequence: String = "",
    val workshopName: String = "",
    val workshopCodeLabel: String = "",
    val address: String = "",
    val startDate: String = "",
    val commitmentDate: String = "",
    val mobile: String = "",
    val email: String = "",
)

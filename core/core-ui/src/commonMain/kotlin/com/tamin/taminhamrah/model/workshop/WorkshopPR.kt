package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/**
 * One کارگاه card on the کارگاه‌های کارفرما list.
 *
 * Everything a label shows is already formatted — Jalali dates separated, digits Persian, missing
 * values dashed — so the card composes text without calling a formatter per frame. The two raw
 * identity fields are kept as the service sent them because they travel to the next screen and
 * into query parameters, where Persian digits would be wrong.
 */
@Immutable
data class WorkshopPR(
    val workshopId: String = "",
    val branchCode: String = "",
    val hasIdentity: Boolean = false,
    val name: String = "",
    /** [workshopId] as the card prints it. */
    val codeLabel: String = "",
    val status: WorkshopActivityStatus = WorkshopActivityStatus.INACTIVE,
    val statusLabel: String = "",
    val employerType: String = "",
    val startDate: String = "",
    val activityType: String = "",
    val branchOfficeCode: String = "",
    val branchOfficeName: String = "",
    val registerDate: String = "",
    val approveDate: String = "",
)

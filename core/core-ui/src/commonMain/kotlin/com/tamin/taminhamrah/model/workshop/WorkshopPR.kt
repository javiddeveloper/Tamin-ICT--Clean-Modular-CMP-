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
    /**
     * `01` حقیقی / `02` حقوقی, raw. Travels to the payment call as `nationalType`, so it is the
     * code and not [employerType], which is the same fact worded for a label.
     */
    val characterCode: String = "",
    /** The حقوقی workshop's national id, raw; blank for a حقیقی one. Sent as `nationalId`. */
    val legalNationalId: String = "",
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

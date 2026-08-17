package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/**
 * One debt on the رسیدگی به بدهی ماده ۱۶ list.
 *
 * The row's [status] decides which actions its sheet offers, and [executiveNotifyDate] is what the
 * one-day filing deadline is measured from — kept raw as well as formatted for that reason.
 */
@Immutable
data class Article16DebtPR(
    val debitNumber: String = "",
    val debitNumberLabel: String = "",
    val amount: String = "",
    val remainingAmount: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val agreementRow: String = "",
    val executiveNotifyDate: String = "",
    val executiveNotifyDateLabel: String = "",
    val status: Article16RequestStatus = Article16RequestStatus.NONE,
    /** Addresses the filed request's PDF and the کارشناس message; null while none was filed. */
    val seqNo: Long? = null,
)

/** The read-only workshop panel on step 1 of the ماده ۱۶ request. */
@Immutable
data class Article16WorkshopInfoPR(
    val workshopId: String = "",
    val workshopName: String = "",
    val employerName: String = "",
    val character: String = "",
    val address: String = "",
)

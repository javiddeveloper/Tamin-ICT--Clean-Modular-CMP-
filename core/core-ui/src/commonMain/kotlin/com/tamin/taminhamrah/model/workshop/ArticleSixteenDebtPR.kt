package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource

/**
 * One debt on the رسیدگی به بدهی ماده ۱۶ list.
 *
 * The row's [status] decides which actions its card offers, and [executiveNotifyDate] is what the
 * one-year filing deadline is measured from — kept raw as well as formatted for that reason.
 */
@Immutable
data class ArticleSixteenDebtPR(
    val debitNumber: String = "",
    val debitNumberLabel: String = "",
    val amount: String = "",
    val remainingAmount: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val agreementRow: String = "",
    val executiveNotifyDate: String = "",
    val executiveNotifyDateLabel: String = "",
    val status: ArticleSixteenRequestStatus = ArticleSixteenRequestStatus.NONE,
    /** Addresses the filed request's PDF and the کارشناس message; null while none was filed. */
    val seqNo: Long? = null,
    /** نوع رسیدگی — ماده ۴۲ / ماده ۴۳ / ماده ۴۴، derived from [kindDoc]. */
    val proceedingType: StringResource? = null,
)

/** The read-only workshop panel on step 1 of the ماده ۱۶ request. */
@Immutable
data class ArticleSixteenWorkshopInfoPR(
    val workshopId: String = "",
    val workshopName: String = "",
    val branchCode: String = "",
    val employerName: String = "",
    val character: String = "",
    val address: String = "",
)

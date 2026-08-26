package com.tamin.taminhamrah.feature.taminServices.workersPayment.model

import androidx.compose.runtime.Immutable

/** Presentation model for one payable construction-worker premium/penalty row. */
@Immutable
data class WorkersPaymentInfoPR(
    val payable: Boolean,
    val monthTitle: String,
    val year: String,
    val professionalTitle: String,
    val professional: String,
    val rate: String,
    val days: String,
    val fromDatePersian: String,
    val toDatePersian: String,
    val amount: Long,
    val amountFines: Long,
    val totalPayable: Long,
    val payDay: Long,
    val payableDes: String,
    val fishStatus: String,
    val maharatStatus: String,
    val bazresiStatus: String,
    val kargarStatus: String,
    val type: String,
    // Raw fields still needed to build the pay request:
    val fromDateToDate: String,
)

package com.tamin.taminhamrah.feature.taminServices.workersPayment.model

import androidx.compose.runtime.Immutable

/** Presentation model for one payable construction-worker premium/penalty row. */
@Immutable
data class WorkersPaymentInfoPR(
    val pay: Boolean,
    val payable: Boolean,
    val month: String,
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
    val salary: Long,
    val payDay: Long,
    val payableDes: String,
    val paymentDate: String?,
    val fishStatus: String,
    val maharatStatus: String,
    val bazresiStatus: String,
    val kargarStatus: String,
    val type: String,
    // Raw field still needed to build the pay request:
    val fromDateToDate: String,
) {
    /** Three mutually exclusive display states the month card renders. */
    val status: Status
        get() = when {
            pay -> Status.PAID
            payable -> Status.PAYABLE
            else -> Status.OVERDUE
        }

    enum class Status { PAID, PAYABLE, OVERDUE }
}

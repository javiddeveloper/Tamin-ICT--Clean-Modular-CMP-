package com.tamin.taminhamrah.model.contractAffair

/**
 * One row of مشاهده پرداخت‌ها, from
 * `special-insured-services/freelance-payment-history-head-with-contractNumber/{contractNumber}`.
 *
 * The backend answers with positional arrays rather than objects; the index each field is read
 * from (mirrored from `old_android`'s `ServiceRepository.getContractsPaymentsListFreelance`) is
 * noted beside it.
 */
data class ContractPaymentHistoryItemDN(
    val nationalId: String?,        // [1]
    val insuranceId: String?,       // [2]
    val debtNumber: String?,        // [3]
    val startTermPayment: String?,  // [5]
    val endTermPayment: String?,    // [6]
    val totalDebt: Double?,         // [7]
    val paymentDeadline: String?,   // [8]
    val amountPayment: Double?,     // [9]
    val datePayment: String?,       // [10]
    val statusContract: String?,    // [11]
    val statusRecipient: String?,   // [12]
)

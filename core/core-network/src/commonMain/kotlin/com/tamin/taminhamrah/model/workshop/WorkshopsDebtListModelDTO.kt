package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `debit-objection/management-workshop-debit/{workshopId}/{branchId}` — the debts a
 * ماده ۱۶ request can be filed against.
 *
 * Amounts are read as `Long`: the old app modelled them as `Int`, which overflows on a workshop
 * debt above ~۲۱۴ کرور ریال. Every one of them is echoed back verbatim when the request is
 * submitted, so the same width is used in [Article16SaveRequestDTO].
 */
@Serializable
data class WorkshopsDebtListModelDTO(
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("debitAmount") val debitAmount: Long? = null,
    @SerialName("debitRemain") val debitRemain: Long? = null,
    @SerialName("debitStartDate") val debitStartDate: String? = null,
    @SerialName("debitEndDate") val debitEndDate: String? = null,
    @SerialName("debitStepCode") val debitStepCode: String? = null,
    @SerialName("debitStatCode") val debitStatCode: String? = null,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String? = null,
    @SerialName("bimehAmount") val insuranceAmount: Long? = null,
    @SerialName("bikariAmount") val unemploymentAmount: Long? = null,
    @SerialName("jarimehAmount") val fineAmount: Long? = null,
    @SerialName("sayerAmount") val otherAmount: Long? = null,
    @SerialName("peymanSequence") val agreementRow: String? = null,
    @SerialName("mastCustomerCode") val customerCode: String? = null,
    @SerialName("mastCustomerTypeCode") val customerTypeCode: String? = null,
    /** Selects the ماده ۴۲/۴۳/۴۴ wording on the request form: `1` / `2` / `3`. */
    @SerialName("kindDoc") val kindDoc: String? = null,
    @SerialName("docNoEjra") val executiveNumber: String? = null,
    @SerialName("docDateEjra") val executiveDate: String? = null,
    /** تاریخ ابلاغ اجراییه — the date the one-day filing deadline is measured from. */
    @SerialName("docDateEblaghEjra") val executiveNotifyDate: String? = null,
    @SerialName("docNoEkhtar") val warningNumber: String? = null,
    @SerialName("docDateEkhtar") val warningDate: String? = null,
    @SerialName("docDateEblaghEkhtar") val warningNotifyDate: String? = null,
    @SerialName("docNoBadvi") val primaryVoteNumber: String? = null,
    @SerialName("docDateBadvi") val primaryVoteDate: String? = null,
    @SerialName("docNoTajdid") val renewalNumber: String? = null,
    @SerialName("docDateTajdid") val renewalDate: String? = null,
    @SerialName("stepCat") val stepCategory: String? = null,
    @SerialName("rowNum") val rowNumber: Int? = null,
    /** The objection sequence, and the key `debit-objection-reports/comitte/{seqNumber}` takes. */
    @SerialName("seqNo") val seqNo: Long? = null,
    /** Request state: `1` ثبت، `7` نقص مدارک، `8` رد، `9` تایید. Absent means no request yet. */
    @SerialName("status") val status: String? = null,
)

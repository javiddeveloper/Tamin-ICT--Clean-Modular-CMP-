package com.tamin.taminhamrah.model.workshop

/** A موضوع کار a درخواست مفاصاحساب can be filed under. */
data class SettlementSubjectDN(
    /** `01`–`30`; the last part of the request's id, and what decides its conditions. */
    val code: String,
    val description: String,
)

/**
 * A درخواست مفاصاحساب, as the form collected it.
 *
 * Amounts and codes are ASCII digits and dates are Gregorian ISO calendar days (`2024-03-20`). How any
 * of it is spelled on the wire — the `TT`-joined id, the fixed time on every date, a field whose JSON
 * type depends on the subject — is the data layer's business.
 */
data class SettlementRequestDN(
    /** The پیمانکار's کد کارگاه. */
    val workshopId: String,
    val contractRow: String,
    /** The پیمان's own branch. */
    val branchCode: String,
    val contractSequence: String,
    val letterNumber: String,
    val letterDate: String,
    val startDate: String,
    val endDate: String,
    val hasSubcontractor: Boolean,
    val amount: Long,
    val currencyAmount: Long,
    val currencyAmountInRial: Long,
    val documents: List<SettlementDocumentDN>,
    val subjectCode: String,
    val subjectOwner: String,
    val subjectText1: String,
    val subjectText2: String,
    val subjectAmount1: String,
    /** Blank when the subject has no second amount. */
    val subjectAmount2: String,
    val subjectAmount3: String,
    val subjectAmount4: String,
    /** The conditions' own image, or blank. */
    val subjectImageGuid: String,
)

/** One uploaded document: the id its upload returned, its category code, and which route took it. */
data class SettlementDocumentDN(
    val documentId: String,
    val categoryCode: String,
    val isPdf: Boolean,
)

/**
 * A مفاصاحساب ماده ۳۸ certificate on file for a پیمان.
 *
 * [number] and [date] are blank when the service listed the certificate but its detail carried no
 * such column; [date] is compact Jalali (`14020103`), separated at the presentation edge.
 */
data class SettlementCertificateDN(
    val serial: String,
    val number: String,
    val date: String,
)

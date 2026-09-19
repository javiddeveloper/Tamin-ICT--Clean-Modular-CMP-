package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One پیمان of `requestissuanceinvoices38/assignersContracts-request-issuance-invoices38` — a
 * contract the signed-in employer is the **واگذارنده** of.
 *
 * The envelope is enormous (the old app's `ContractInfo` declares sixty columns, most of them
 * `Any?`); only what the four screens draw is modeled and `ignoreUnknownKeys` drops the rest.
 *
 * Two parties, and they are not interchangeable:
 * - [assigner] is *you* — «اطلاعات واگذارنده (کارگاه شما)» on جزئیات پیمان. The design's mock
 *   hardcodes this block; it is a real field on this response.
 * - [employer] is the **پیمانکار**, and is what every card, every search filter and the
 *   computational-base call address. `workshop` carries the same workshop in a fatter shape and is
 *   deliberately not modeled — one projection of one concept is enough.
 */
@Serializable
data class AssignerContractDTO(
    /** ردیف پیمان. */
    @SerialName("contractRow") val contractRow: String? = null,
    /**
     * The contract's sequence within its ردیف, and the fourth key the bases endpoint needs.
     *
     * Absent here means the bases call cannot be addressed at all — see `ComputationalBaseQuery`.
     */
    @SerialName("contractSequence") val contractSequence: String? = null,
    @SerialName("contractNumber") val contractNumber: String? = null,
    /** Compact Jalali (`14010210`), separated at the presentation edge. */
    @SerialName("contractDate") val contractDate: String? = null,
    /**
     * When the پیمان ends, compact Jalali like [contractDate] — what splits the list into جاری and
     * خاتمه‌یافته. The old app's model for this endpoint declares it but never draws it, so no live
     * payload has yet confirmed it arrives populated.
     */
    @SerialName("contractEndDate") val contractEndDate: String? = null,
    /** موضوع پیمان, as prose. The numeric `contractSubjectCode` is a different column. */
    @SerialName("contractSubject") val contractSubject: String? = null,
    /**
     * The پیمان's own شعبه. The old app addresses a درخواست مفاصاحساب with this one rather than the
     * پیمانکار's, so it is read on its own even where the two agree.
     */
    @SerialName("branch") val branch: AssignerBranchDTO? = null,
    /** واگذارنده — the signed-in employer's own workshop. */
    @SerialName("assigner") val assigner: AssignerPartyDTO? = null,
    /** پیمانکار — the counterparty, and the workshop every drill-down is keyed on. */
    @SerialName("employer") val employer: AssignerPartyDTO? = null,
)

/**
 * One side of a پیمان, as this endpoint projects it.
 *
 * Flat and five fields wide on both sides, which is why one type serves واگذارنده and پیمانکار
 * alike. The service sends no mobile or email on either party — the design's contact cells have no
 * column behind them here, so the card draws that block only from what actually arrives.
 */
@Serializable
data class AssignerPartyDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    /** کد ملی / شناسهٔ ملی — a national code for a real employer, a legal id for a company. */
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("branch") val branch: AssignerBranchDTO? = null,
)

/**
 * The شعبه a party belongs to.
 *
 * [code] is the `brchCode` the computational-base endpoint expects — the old app reads it from
 * exactly here — and [organizationName] is what جزئیات پیمان prints.
 */
@Serializable
data class AssignerBranchDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("organizationName") val organizationName: String? = null,
)

/**
 * One مبنای محاسباتی of `requestissuanceinvoices38/det-request-issuance-invoices38`.
 *
 * The wire names are lower-case and abbreviated — `letno`, `letdate`, `cntamount`, `senddate` —
 * and every one of them is the contract. A formatter that "tidies" `cntamount` to `cntAmount`
 * silently stops the field binding and the amount renders as a dash.
 */
@Serializable
data class ComputationalBaseDTO(
    /** شمارهٔ سند — the letter number the record is filed under. */
    @SerialName("letno") val letterNumber: String? = null,
    /** Epoch millis. When the واگذارنده sent the bases in. */
    @SerialName("senddate") val sendDate: Long? = null,
    /** مبلغ ناخالص کارکرد، rials. */
    @SerialName("cntamount") val amount: Long? = null,
    /**
     * Epoch millis — the start of the work the base declares. The old app prints it as «تاریخ شروع
     * قرارداد» on the base's letter section; together with [endDate] it is the base's period.
     */
    @SerialName("startDate") val startDate: Long? = null,
    /** Epoch millis — «تاریخ خاتمه عملیات اجرایی پیمان» on the same section. */
    @SerialName("endDate") val endDate: Long? = null,
    /** The پیمان this belongs to — read for the row's own identity, not re-displayed. */
    @SerialName("contract") val contract: ComputationalBaseContractDTO? = null,
    /** The attached documents. Empty is a real answer: a base can be filed without any. */
    @SerialName("dataDetail") val documents: List<ComputationalBaseDocumentDTO>? = null,
    /**
     * Where the base stands in the workflow, `"01"`–`"17"` — the old app's `status`, which it shows
     * in bold on the base's detail.
     */
    @SerialName("status") val status: String? = null,
    /** شمارهٔ برگهٔ پرداخت بدهی قطعی — `ordno1`, once one is issued. */
    @SerialName("ordno1") val finalOrderNumber: String? = null,
    /** شمارهٔ برگهٔ پرداخت بدهی برآوردی — `ordno2`, once one is issued. */
    @SerialName("ordno2") val estimatedOrderNumber: String? = null,
)

/** Just enough of the nested پیمان to key a base row. */
@Serializable
data class ComputationalBaseContractDTO(
    @SerialName("contractRow") val contractRow: String? = null,
    @SerialName("contractSequence") val contractSequence: String? = null,
)

/**
 * One attached document.
 *
 * [documentType] decides which of the two download routes serves it — `"1"` is an image fetched
 * from `upload-image/{id}/0/0` as base64, anything else is a PDF streamed from
 * `getPdf-request-issuance-invoices38/{id}`. [documentCode] decides what it is *called*: the old
 * app groups the same four codes under four fixed headings and never shows a document name,
 * because the service does not send one.
 */
@Serializable
data class ComputationalBaseDocumentDTO(
    @SerialName("documentId") val documentId: String? = null,
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("documentCode") val documentCode: String? = null,
)

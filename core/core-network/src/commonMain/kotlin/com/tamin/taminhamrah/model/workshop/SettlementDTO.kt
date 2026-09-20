package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/*
 * درخواست مفاصاحساب — the wire half of واگذارندگان's settlement request, exactly as the old app's
 * `MafasaHesabRequestModel` sends it.
 *
 * The names are that model's own: lower case, abbreviated, and inconsistent (`letdate` beside
 * `startDate`, `cntamountcurrencyToR`, `natcodecontract`). Every one of them is the contract — a
 * formatter that tidies one leaves the body looking fine while the service stops reading the field.
 */

/** One row of `contractSubject-request-issuance-invoices38` — a موضوع کار a request is filed under. */
@Serializable
data class SettlementSubjectDTO(
    /** Two digits, `01`–`30`. Decides which conditions the request carries. */
    @SerialName("code") val code: String? = null,
    @SerialName("description") val description: String? = null,
)

/**
 * The body of `update-request-issuance-invoices38/{id}`.
 *
 * No property has a default, on purpose: the client's `Json` does not encode defaults, and the old
 * app's Gson sent every one of these — its `""` and `"0"` included. A default here would silently drop
 * the field from the body. [subjectAmount2] is the one exception, because Gson left it out while null.
 */
@Serializable
data class SettlementRequestDTO(
    /** مبلغ ریالی ناخالص کارکرد, digits. */
    @SerialName("cntamount") val amount: String,
    /** مبلغ ارزی, digits. */
    @SerialName("cntamountcurrency") val currencyAmount: String,
    /** معادل ریالی مبلغ ارزی, digits. */
    @SerialName("cntamountcurrencyToR") val currencyAmountInRial: String,
    /** [amount] plus [currencyAmountInRial] — a JSON number, unlike the strings it sums. */
    @SerialName("cntamounttotal") val totalAmount: Double,
    /** `2024-03-19T19:30:00.000Z`; see `SettlementRequestDN.toDto` for the fixed time. */
    @SerialName("letdate") val letterDate: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String,
    @SerialName("letno") val letterNumber: String,
    @SerialName("dataDetail") val documents: List<SettlementDocumentDTO>,
    /** Whether any attached document is an image, whatever it was filed as. */
    @SerialName("hasLetImage") val hasLetterImage: Boolean,
    /** The پیمانکار's کد کارگاه. */
    @SerialName("natcodecontract") val contractorWorkshopId: String,
    /** `"1"` yes, `"0"` no. */
    @SerialName("subcontractor") val subcontractor: String,
    @SerialName("contractsubjectcode") val subjectCode: String,
    @SerialName("subjectOwner") val subjectOwner: String,
    @SerialName("subjectamount1") val subjectAmount1: String,
    /**
     * A JSON **number** for subjects 04–07, where the old app stored the computed remainder as a
     * `Long`; a string for 11 and 29, where it is typed; absent for every other subject.
     */
    @SerialName("subjectamount2") val subjectAmount2: JsonElement? = null,
    @SerialName("subjectamount3") val subjectAmount3: String,
    @SerialName("subjectamount4") val subjectAmount4: String,
    /** The guid `upload-image` returned for the conditions' own image, or blank. */
    @SerialName("subjectimage") val subjectImage: String,
    @SerialName("subjecttext1") val subjectText1: String,
    @SerialName("subjecttext2") val subjectText2: String,
)

/** One attached document: the id its upload answered with, what it is, and which route took it. */
@Serializable
data class SettlementDocumentDTO(
    @SerialName("documentId") val documentId: String,
    /** The same four codes جزئیات مبنا groups documents under. */
    @SerialName("documentCode") val documentCode: String,
    /** `"1"` an image from `upload-image`, `"2"` a PDF from `persistPdf-request-issuance-invoices38`. */
    @SerialName("documentType") val documentType: String,
)

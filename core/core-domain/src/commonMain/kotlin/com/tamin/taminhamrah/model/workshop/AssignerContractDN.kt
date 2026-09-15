package com.tamin.taminhamrah.model.workshop

/**
 * One پیمان the signed-in employer is the واگذارنده of.
 *
 * Strings are non-null and empty when the service omitted them; the display fallback is applied
 * once, at the presentation edge.
 *
 * [assigner] and [employer] are the two sides, and mixing them puts the wrong workshop on every
 * card: [assigner] is the signed-in employer's own کارگاه, [employer] is the پیمانکار — which is
 * the one the list shows and the one all four drill-down keys come from.
 */
data class AssignerContractDN(
    val contractRow: String = "",
    /**
     * The fourth key the bases call needs, alongside the پیمانکار's workshop, branch and ردیف.
     *
     * Blank when the service omitted it, which is why `ComputationalBaseQuery` refuses to be built
     * rather than sending three keys out of four and getting somebody else's bases back.
     */
    val contractSequence: String = "",
    /**
     * The پیمان's own branch — the third key of a درخواست مفاصاحساب's id. The service's top-level
     * `branch`, which is what the old app reads there, or the پیمانکار's when it sent none.
     */
    val branchCode: String = "",
    val contractNumber: String = "",
    /** Compact Jalali, `14010210`. */
    val contractDate: String = "",
    /** Compact Jalali, `14030601`; blank when the service sent none. */
    val contractEndDate: String = "",
    val contractSubject: String = "",
    val assigner: AssignerPartyDN = AssignerPartyDN(),
    val employer: AssignerPartyDN = AssignerPartyDN(),
)

/** One side of a پیمان — واگذارنده or پیمانکار, the same five columns either way. */
data class AssignerPartyDN(
    val workshopId: String = "",
    val workshopName: String = "",
    val nationalId: String = "",
    val address: String = "",
    /** The `brchCode` the bases endpoint is keyed on, when this party is the پیمانکار. */
    val branchCode: String = "",
    val branchName: String = "",
)

/** One مبنای محاسباتی filed under a پیمان. */
data class ComputationalBaseDN(
    /** شمارهٔ سند — `letno` on the wire. */
    val letterNumber: String = "",
    /** Epoch millis; zero when the service sent none. */
    val sendDate: Long? = null,
    /** مبلغ ناخالص کارکرد, rials. Null and zero are different answers — see the mapper. */
    val amount: Long? = null,
    /** Epoch millis; the period the base declares starts here. Null when the service sent none. */
    val startDate: Long? = null,
    /** Epoch millis; the period ends here. Null when the service sent none. */
    val endDate: Long? = null,
    val documents: List<BaseDocumentDN> = emptyList(),
)

/**
 * One document attached to a مبنای محاسباتی.
 *
 * [kind] is behavior — it decides which of the two endpoints serves the file — so it is settled
 * here. [categoryCode] is copy, and stays a raw code until core-ui turns it into a heading.
 */
data class BaseDocumentDN(
    val documentId: String = "",
    val kind: BaseDocumentKind = BaseDocumentKind.PDF,
    val categoryCode: String = "",
)

/**
 * Which viewer and which endpoint a document goes to.
 *
 * The old app branches on `documentType` in exactly two places and treats `"1"` as the image case
 * and everything else as the PDF case, so the fallback is PDF rather than "unknown" — there is no
 * third viewer to fall through to.
 */
enum class BaseDocumentKind {
    /** `documentType == "1"` — base64 from `upload-image/{id}/0/0`. */
    IMAGE,

    /** Anything else — raw bytes from `getPdf-request-issuance-invoices38/{id}`. */
    PDF;

    companion object {
        fun fromType(type: String?): BaseDocumentKind = if (type == IMAGE_TYPE) IMAGE else PDF

        private const val IMAGE_TYPE = "1"
    }
}

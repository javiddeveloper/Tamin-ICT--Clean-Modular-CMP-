package com.tamin.taminhamrah.model.workshop

/** The read-only workshop panel on step 1 of the ماده ۱۶ request. */
data class Article16WorkshopInfoDN(
    val workshopId: String = "",
    val workshopName: String = "",
    val branchCode: String = "",
    val employerName: String = "",
    val character: String = "",
    val address: String = "",
)

/** What the کارشناس wrote back on a نقص مدارک request, plus the documents already on file. */
data class Article16RequestInfoDN(
    val defectDescription: String = "",
    val documents: List<ObjectionDocumentDN> = emptyList(),
)

/**
 * Everything the ماده ۱۶ request submits: the debt row it was opened on, the workshop identity and
 * the uploaded documents.
 */
data class Article16SaveRequestDN(
    val workshopId: String,
    val branchCode: String,
    val debt: WorkshopsDebtListModelDN,
    val documents: List<ObjectionDocumentDN>,
)

/** Result of filing a ماده ۱۶ request; [referenceCode] is what the success message quotes. */
data class Article16SaveResultDN(
    val referenceCode: String = "",
)

/** The upload ceiling step 3 enforces — the add control disappears at this many files. */
const val ARTICLE16_MAX_DOCUMENTS = 10

/** How long after تاریخ ابلاغ اجراییه a ماده ۱۶ request may still be filed, in Jalali days. */
const val ARTICLE16_FILING_WINDOW_DAYS = 1

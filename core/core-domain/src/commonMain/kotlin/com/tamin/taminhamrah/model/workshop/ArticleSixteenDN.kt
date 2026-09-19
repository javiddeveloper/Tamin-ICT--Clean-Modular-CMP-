package com.tamin.taminhamrah.model.workshop

/** The read-only workshop panel on step 1 of the ماده ۱۶ request. */
data class ArticleSixteenWorkshopInfoDN(
    val workshopId: String = "",
    val workshopName: String = "",
    val branchCode: String = "",
    val employerName: String = "",
    val character: String = "",
    val address: String = "",
)

/** What the کارشناس wrote back on a نقص مدارک request, plus the documents already on file. */
data class ArticleSixteenRequestInfoDN(
    val defectDescription: String = "",
    val documents: List<ObjectionDocumentDN> = emptyList(),
)

/**
 * Everything the ماده ۱۶ request submits: the debt row it was opened on, the workshop identity and
 * the uploaded documents.
 */
data class ArticleSixteenSaveRequestDN(
    val workshopId: String,
    val branchCode: String,
    val debt: WorkshopsDebtListModelDN,
    val documents: List<ObjectionDocumentDN>,
)

/** Result of filing a ماده ۱۶ request; [referenceCode] is what the success message quotes. */
data class ArticleSixteenSaveResultDN(
    val referenceCode: String = "",
)

/** The upload ceiling step 3 enforces — the add control disappears at this many files. */
const val ARTICLE_SIXTEEN_MAX_DOCUMENTS = 10

/**
 * How long after تاریخ ابلاغ اجراییه a ماده ۱۶ request may still be filed, in days: one year.
 *
 * The old app refuses with «بیش از یک سال از تاریخ ابلاغیه اجراییه»; the design's note reads «یک
 * روز», which would refuse nearly every real debt. (The old app's own arithmetic, whole years `> 1`,
 * lets a debt through until day 730 — its message, not that slip, is the rule kept here.)
 */
const val ARTICLE_SIXTEEN_FILING_WINDOW_DAYS = 365

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
 * How long after تاریخ ابلاغ اجراییه a ماده ۱۶ request may still be filed, counted exactly as the
 * old app counts it: whole years of [ARTICLE_SIXTEEN_DAYS_PER_YEAR] days, truncated, and refused
 * only once that count *exceeds* this — so day 729 is the last one accepted, although the message
 * it refuses with reads «بیش از یک سال».
 *
 * The design's note says «یک روز», which would refuse nearly every real debt; it is not the rule.
 */
const val ARTICLE_SIXTEEN_FILING_WINDOW_YEARS = 1

/** The old app's year for [ARTICLE_SIXTEEN_FILING_WINDOW_YEARS]: a flat 365 days, leap or not. */
const val ARTICLE_SIXTEEN_DAYS_PER_YEAR = 365

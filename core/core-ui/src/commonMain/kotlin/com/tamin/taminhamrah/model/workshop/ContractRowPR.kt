package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/**
 * One ردیف پیمان card.
 *
 * Both sources — the تعهدنامه‌دار agreement row and the lean بدون تعهدنامه row — land here, because
 * the design draws them on one card and only the *presence* of the contact block differs. Which
 * fields a given row can fill is decided by the endpoint it came from, so nothing on this screen
 * has to ask which tab it is on to know what to draw.
 *
 * Everything is pre-formatted: Persian digits applied, Jalali dates separated, values the service
 * omitted already dashed. The card composes text without calling a formatter per frame.
 */
@Immutable
data class ContractRowPR(
    /** Raw ASCII workshop id — the identity, not the label. Blank rows cannot be keyed on. */
    val workshopId: String = "",
    val branchCode: String = "",
    val name: String = "",
    /** ردیف پیمان as the badge prints it. */
    val rowLabel: String = "",
    /** [workshopId] in Persian digits, for the شمارهٔ کارگاه tile. */
    val workshopCodeLabel: String = "",
    /** تاریخ تعهد, `۱۴۰۳/۰۲/۱۰`. */
    val commitmentDate: String = "",
    /**
     * The three columns only the تعهدنامه‌دار endpoint sends.
     *
     * [mobile] and [email] carry the design's em dash when the service omitted them — the tile is
     * drawn either way, so an empty one would read as a rendering fault. [address] is left blank
     * instead, because its tile is dropped entirely when there is no address: a full-width dash is
     * a bigger claim about missing data than the design makes.
     */
    val mobile: String = "",
    val email: String = "",
    val address: String = "",
)

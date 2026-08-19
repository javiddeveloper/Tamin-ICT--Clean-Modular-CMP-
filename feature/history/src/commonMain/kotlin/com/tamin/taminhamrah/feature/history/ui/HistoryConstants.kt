package com.tamin.taminhamrah.feature.history.ui

/**
 * Every fixed value «کلیه سوابق» reasons with, in one table.
 *
 * Scattered as private consts per file, the same fact was declared several times over and could
 * drift apart in a way nothing would catch: a Jalali year was `MONTHS` in one file and
 * `MONTHS_IN_YEAR` in another, a month was 30 days in two places, and a full year was 365 days in
 * two more — while the chart measured its bars against 366. Those are not four decisions, they are
 * one, and they belong in one place.
 *
 * Measurements live in [HistoryDimens]; user-facing wording lives in the string resources. This
 * holds only the numbers and the literals the logic itself depends on.
 */
object HistoryConstants {

    // ── The calendar ──────────────────────────────────────────────────────────

    /** Months in a Jalali year — the length every day-count list is normalized to. */
    const val MONTHS_IN_YEAR = 12

    /** Months to a season, and so the four the year is read in. */
    const val MONTHS_PER_SEASON = 3
    const val SEASONS = 4

    /**
     * The service counts a month as 30 days when it reports a career length.
     *
     * A convention, not a calendar fact, and kept deliberately: the figure has to agree with what
     * the rest of تأمین prints for the same person.
     */
    const val DAYS_IN_MONTH = 30

    /** Below this many insured days a year is short, which the design marks. */
    const val FULL_YEAR_DAYS = 365

    /**
     * What a year bar's height is measured against.
     *
     * A leap year rather than [FULL_YEAR_DAYS], so a complete year never overflows its track.
     */
    const val DAYS_IN_LEAP_YEAR = 366f

    // ── The wire ──────────────────────────────────────────────────────────────

    /**
     * How an optional-insurance row names its type.
     *
     * Spelled with the Arabic yeh the service actually sends, not the Persian one — this is matched
     * against the wire, and "correcting" it would stop it matching.
     */
    const val OPTIONAL_TYPE_PREFIX = "اختياري"

    // ── Presentation ──────────────────────────────────────────────────────────

    /** Above this many years the design narrows the bars and drops their labels for an axis. */
    const val DENSE_BAR_THRESHOLD = 12

    /** A month with nothing recorded, which the sheet prints rather than leaving blank. */
    const val EMPTY_MONTH = "00"

    /** A workshop row that carries no number of its own. */
    const val NO_CODE = "—"

    /** Between a month, its year and its days. */
    const val SEPARATOR = " · "

    const val SENTENCE_END = "."

    /** `toRialAmount` appends the unit; the places that print it themselves strip this first. */
    const val RIAL_SUFFIX = " ریال"

    /** Stands in for the number while «%s روز» is resolved once instead of per bar. */
    const val PLACEHOLDER_DAYS = "#"

    /** How far down the hero's gradient its middle colour sits. */
    const val HERO_MID_STOP = 0.58f

    /** The unit beside an amount is quieter than the amount. */
    const val RIAL_ALPHA = 0.7f

    // ── Lazy list keys ────────────────────────────────────────────────────────

    const val ALL_CHIP_KEY = "all"
    const val HERO_KEY = "hero"
    const val CHART_KEY = "chart"
    const val SECTIONS_KEY = "sections"
    const val EMPTY_STATE_KEY = "empty"
    const val SKELETON_KEY = "skeleton"
    const val SHEET_HEADER_KEY = "header"
    const val WORKSHOPS_TITLE_KEY = "workshops_title"
    const val WAGES_UNAVAILABLE_KEY = "wages_unavailable"
    const val NO_WORKSHOP_KEY = "no_workshop"
    const val CLOSE_KEY = "close"
}

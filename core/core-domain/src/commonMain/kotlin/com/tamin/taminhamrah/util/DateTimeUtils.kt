package com.tamin.taminhamrah.util

private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
const val DEFAULT_WEEK_PERIOD_DAYS = 7
const val DEFAULT_WEEK_PERIOD_MILLIS = DEFAULT_WEEK_PERIOD_DAYS * MILLIS_IN_DAY
const val DEFAULT_MONTH_PERIOD_DAYS = 30
const val DEFAULT_MONTH_PERIOD_MILLIS = DEFAULT_MONTH_PERIOD_DAYS * MILLIS_IN_DAY
const val DEFAULT_SIX_MONTH_PERIOD_DAYS = 180
const val DEFAULT_SIX_MONTH_PERIOD_MILLIS = DEFAULT_SIX_MONTH_PERIOD_DAYS * MILLIS_IN_DAY
const val DEFAULT_YEAR_PERIOD_DAYS = 365
const val DEFAULT_YEAR_PERIOD_MILLIS = DEFAULT_YEAR_PERIOD_DAYS * MILLIS_IN_DAY

/**
 * Returns the timestamp of one week ago in milliseconds as a string.
 */
fun getOneWeekAgoTimestamp(): String {
    return (currentTimeMillis() - DEFAULT_WEEK_PERIOD_MILLIS).toString()
}

/**
 * Returns the current timestamp in milliseconds as a string.
 */
fun getCurrentTimestamp(): String {
    return currentTimeMillis().toString()
}

/**
 * Returns the timestamp of six months ago in milliseconds as a string.
 *
 * Approximated as [DEFAULT_SIX_MONTH_PERIOD_DAYS] days — the patient-history endpoint takes a plain
 * millisecond range, so calendar-exact months buy nothing here.
 */
fun getSixMonthsAgoTimestamp(): String {
    return (currentTimeMillis() - DEFAULT_SIX_MONTH_PERIOD_MILLIS).toString()
}

/** Returns the timestamp of one month ago in milliseconds as a string. */
fun getOneMonthAgoTimestamp(): String {
    return (currentTimeMillis() - DEFAULT_MONTH_PERIOD_MILLIS).toString()
}

/** Returns the timestamp of one year ago in milliseconds as a string. */
fun getOneYearAgoTimestamp(): String {
    return (currentTimeMillis() - DEFAULT_YEAR_PERIOD_MILLIS).toString()
}

/** Start of the epoch — used by the «از ابتدا» filter to request the full history. */
fun getBeginningTimestamp(): String = "0"

package com.tamin.taminhamrah.util

private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
const val DEFAULT_WEEK_PERIOD_DAYS = 7
const val DEFAULT_WEEK_PERIOD_MILLIS = DEFAULT_WEEK_PERIOD_DAYS * MILLIS_IN_DAY

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

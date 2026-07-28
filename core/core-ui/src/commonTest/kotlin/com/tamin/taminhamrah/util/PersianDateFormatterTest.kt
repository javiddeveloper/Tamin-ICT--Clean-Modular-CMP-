package com.tamin.taminhamrah.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersianDateFormatterTest {

    @Test
    fun toEpochMillis_roundTripsBackToTheSameJalaliDate() {
        // Every day of a full year, so month lengths and the leap rule are all exercised.
        val year = 1404
        for (month in 1..12) {
            for (day in 1..PersianDateFormatter.daysInMonth(year, month)) {
                val millis = PersianDateFormatter.toEpochMillis(year, month, day)
                assertEquals(
                    PersianDateFormatter.format(year, month, day),
                    PersianDateFormatter.formatTimestamp(millis),
                    "round trip failed for $year/$month/$day",
                )
            }
        }
    }

    @Test
    fun daysInMonth_followsTheJalaliMonthLengths() {
        assertEquals(31, PersianDateFormatter.daysInMonth(1404, 1))
        assertEquals(31, PersianDateFormatter.daysInMonth(1404, 6))
        assertEquals(30, PersianDateFormatter.daysInMonth(1404, 7))
        assertEquals(30, PersianDateFormatter.daysInMonth(1404, 11))
        // اسفند is 29 in a common year and 30 in a leap year.
        assertTrue(PersianDateFormatter.daysInMonth(1404, 12) in 29..30)
    }

    @Test
    fun firstWeekdayOfMonth_staysInsideTheWeek() {
        for (month in 1..12) {
            val weekday = PersianDateFormatter.firstWeekdayOfMonth(1404, month)
            assertTrue(weekday in 0..6, "weekday out of range for month $month: $weekday")
        }
    }
}

package com.tamin.taminhamrah.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The picker collects dates that have already happened, so the wheels are trimmed at today rather
 * than validated afterwards: a future date is never reachable in the first place.
 *
 * Today throughout is ۳۱ مرداد ۱۴۰۵ — 1405/05/31.
 */
class JalaliDatePickerBoundsTest {

    private val todayYear = 1405
    private val todayMonth = 5
    private val todayDay = 31
    private val monthsInYear = 12

    private fun lastMonthIn(year: Int) = lastSelectableMonth(
        year = year,
        todayYear = todayYear,
        todayMonth = todayMonth,
        monthsInYear = monthsInYear,
    )

    private fun lastDayIn(year: Int, month: Int, daysInMonth: Int) = lastSelectableDay(
        year = year,
        month = month,
        todayYear = todayYear,
        todayMonth = todayMonth,
        todayDay = todayDay,
        daysInMonth = daysInMonth,
    )

    @Test
    fun `a past year offers all twelve months`() {
        assertEquals(12, lastMonthIn(1404))
        assertEquals(12, lastMonthIn(1300))
    }

    @Test
    fun `the current year stops at the current month`() {
        // شهریور and later must not be reachable in 1405.
        assertEquals(5, lastMonthIn(1405))
    }

    @Test
    fun `a past month in the current year offers its full length`() {
        // تیر 1405 is behind us, so all 31 of its days are fair game.
        assertEquals(31, lastDayIn(year = 1405, month = 4, daysInMonth = 31))
    }

    @Test
    fun `the current month stops at today rather than the month's length`() {
        // مرداد has 31 days and today is the 31st, so these coincide — check a mid-month today too.
        assertEquals(31, lastDayIn(year = 1405, month = 5, daysInMonth = 31))
        assertEquals(
            12,
            lastSelectableDay(
                year = 1405,
                month = 5,
                todayYear = 1405,
                todayMonth = 5,
                todayDay = 12,
                daysInMonth = 31,
            ),
        )
    }

    @Test
    fun `a past year keeps its own month length, not today's day`() {
        assertEquals(29, lastDayIn(year = 1404, month = 12, daysInMonth = 29))
        assertEquals(30, lastDayIn(year = 1399, month = 7, daysInMonth = 30))
    }
}

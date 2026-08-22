package com.tamin.taminhamrah.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Absolute anchors for the Jalali conversion.
 *
 * The two directions were previously tuned against each other — a date survived a round trip, but
 * both were a day off in the same direction, so `today()` reported tomorrow. Round-trip tests
 * cannot catch that; only known Gregorian/Jalali pairs can, which is what this file holds.
 *
 * Nowruz anchors: 1403 began 2024-03-20 (1403 is a leap year), 1404 began 2025-03-21, and 1405
 * began 2026-03-21.
 */
class JalaliConversionTest {

    private val knownPairs = listOf(
        // Gregorian (y, m, d) to Jalali (y, m, d)
        Triple(2024, 3, 20) to Triple(1403, 1, 1),
        Triple(2025, 3, 21) to Triple(1404, 1, 1),
        Triple(2026, 3, 21) to Triple(1405, 1, 1),
        // The last day of مرداد 1405 — five 31-day months after Nowruz.
        Triple(2026, 8, 22) to Triple(1405, 5, 31),
        // The day after it, to pin the month rollover.
        Triple(2026, 8, 23) to Triple(1405, 6, 1),
    )

    @Test
    fun `gregorian dates convert to the right Jalali date`() {
        knownPairs.forEach { (gregorian, jalali) ->
            val (gy, gm, gd) = gregorian
            assertEquals(
                jalali,
                PersianDateFormatter.gregorianToJalali(gy, gm, gd),
                "gregorianToJalali($gy-$gm-$gd)",
            )
        }
    }

    @Test
    fun `jalali dates convert back to the right Gregorian date`() {
        knownPairs.forEach { (gregorian, jalali) ->
            val (jy, jm, jd) = jalali
            assertEquals(
                gregorian,
                PersianDateFormatter.jalaliToGregorian(jy, jm, jd),
                "jalaliToGregorian($jy-$jm-$jd)",
            )
        }
    }

    @Test
    fun `the last day of Esfand rolls over to Nowruz`() {
        // 1404 is a common year, so اسفند has 29 days and 1404-12-29 is the eve of Nowruz 1405.
        assertEquals(Triple(2026, 3, 20), PersianDateFormatter.jalaliToGregorian(1404, 12, 29))
        assertEquals(Triple(1404, 12, 29), PersianDateFormatter.gregorianToJalali(2026, 3, 20))
    }
}

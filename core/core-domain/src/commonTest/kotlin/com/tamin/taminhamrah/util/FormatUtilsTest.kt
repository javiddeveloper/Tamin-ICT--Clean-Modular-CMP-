package com.tamin.taminhamrah.util

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatUtilsTest {

    @Test
    fun testFormatDecimalTwoPlaces() {
        val number = 12.34567
        assertEquals("12.35", number.formatDecimal(2))
    }

    @Test
    fun testFormatDecimalZeroPlaces() {
        val number = 12.34567
        assertEquals("12", number.formatDecimal(0))
    }
}

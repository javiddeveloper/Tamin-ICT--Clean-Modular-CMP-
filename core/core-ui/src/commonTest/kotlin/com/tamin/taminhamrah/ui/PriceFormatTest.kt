package com.tamin.taminhamrah.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PriceFormatTest {

    @Test
    fun `groups digits in threes using persian numerals`() {
        assertEquals("۶۵٬۹۱۰", 65_910L.toPriceFormat())
        assertEquals("۲۱۹٬۷۰۰", 219_700L.toPriceFormat())
        assertEquals("۱٬۲۳۴٬۵۶۷", "1234567".toPriceFormat())
    }

    @Test
    fun `handles group boundaries and zero`() {
        assertEquals("۰", 0L.toPriceFormat())
        assertEquals("۱۰۰", 100L.toPriceFormat())
        assertEquals("۱٬۰۰۰", 1_000L.toPriceFormat())
        assertEquals("۰", "".toPriceFormat())
    }

    @Test
    fun `keeps the sign outside the grouped digits`() {
        assertEquals("-۱٬۲۳۴", (-1_234L).toPriceFormat())
    }

    @Test
    fun `does not emit a separator directly after the minus sign`() {
        // Regression: grouping the sign along with the digits used to yield "-٬۱۲۳"
        // whenever the digit count was a multiple of three.
        assertEquals("-۱۲۳", (-123L).toPriceFormat())
        assertEquals("-۱۲۳٬۴۵۶", (-123_456L).toPriceFormat())
    }

    @Test
    fun `does not overflow on the smallest long`() {
        assertEquals("-۹٬۲۲۳٬۳۷۲٬۰۳۶٬۸۵۴٬۷۷۵٬۸۰۸", Long.MIN_VALUE.toPriceFormat())
    }

    @Test
    fun `drops the fractional part of a double`() {
        assertEquals("۱٬۲۳۴", 1_234.87.toPriceFormat())
    }

    @Test
    fun `rejects a string holding non-digit characters`() {
        assertFailsWith<IllegalArgumentException> { "12a4".toPriceFormat() }
        assertFailsWith<IllegalArgumentException> { "1,234".toPriceFormat() }
    }
}

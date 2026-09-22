package com.tamin.taminhamrah.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphanumericFilterTest {

    @Test
    fun `alphanumericOnly folds digits and uppercases latin letters`() {
        assertEquals("2P11253499", "2P11253499".alphanumericOnly())
        assertEquals("2P11253499", "2p11253499".alphanumericOnly())
        assertEquals("2P11253499", "۲p ۱۱۲۵-۳۴۹۹".alphanumericOnly())
        assertEquals("ABC123XYZ", "abc-123_xyz".alphanumericOnly())
    }
}

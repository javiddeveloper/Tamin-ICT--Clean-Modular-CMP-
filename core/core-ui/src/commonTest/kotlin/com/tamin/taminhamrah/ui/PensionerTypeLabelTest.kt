package com.tamin.taminhamrah.ui

import taminx.core.core_ui.Res
import taminx.core.core_ui.pension_status_type_retirement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PensionerTypeLabelTest {

    @Test
    fun `maps retirement code 101 to the retirement string resource`() {
        assertEquals(Res.string.pension_status_type_retirement, "101".toPensionerTypeLabel())
    }

    @Test
    fun `leaves unknown codes and blank values unmapped`() {
        assertNull("بازنشستگی".toPensionerTypeLabel())
        assertNull("102".toPensionerTypeLabel())
        assertNull("".toPensionerTypeLabel())
        assertNull(null.toPensionerTypeLabel())
    }
}

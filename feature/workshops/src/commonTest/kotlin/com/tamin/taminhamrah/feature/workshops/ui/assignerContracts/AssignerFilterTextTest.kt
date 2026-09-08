package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.buildAssignerFilterText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The applied-search chip.
 *
 * Two of the three codes are optional, so the chip has to read as a sentence with any combination
 * of them present — «کد کارگاه ۹۰۲۸ · ردیف ۱», never «کد کارگاه ۹۰۲۸ ·  · ردیف ۱» or a line with a
 * separator hanging off the end. The join is a pure function precisely so it can be checked without
 * a resource loader; the labels arrive already resolved.
 */
class AssignerFilterTextTest {

    @Test
    fun `all three parts join with the designs separator`() {
        assertEquals(
            "کد کارگاه ۹۰۲۸ · کد شعبه ۰۲۱۰ · ردیف ۱",
            buildAssignerFilterText("کد کارگاه ۹۰۲۸", "کد شعبه ۰۲۱۰", "ردیف ۱"),
        )
    }

    @Test
    fun `an absent branch leaves no gap`() {
        assertEquals(
            "کد کارگاه ۹۰۲۸ · ردیف ۱",
            buildAssignerFilterText("کد کارگاه ۹۰۲۸", branch = null, row = "ردیف ۱"),
        )
    }

    @Test
    fun `an absent row leaves no trailing separator`() {
        assertEquals(
            "کد کارگاه ۹۰۲۸ · کد شعبه ۰۲۱۰",
            buildAssignerFilterText("کد کارگاه ۹۰۲۸", branch = "کد شعبه ۰۲۱۰", row = null),
        )
    }

    @Test
    fun `only the required part reads on its own`() {
        assertEquals(
            "کد کارگاه ۹۰۲۸",
            buildAssignerFilterText("کد کارگاه ۹۰۲۸", branch = null, row = null),
        )
    }

    /** Blank is treated as absent, so a resolved-but-empty label cannot leave a dangling separator. */
    @Test
    fun `blank parts are dropped like missing ones`() {
        assertEquals(
            "کد کارگاه ۹۰۲۸",
            buildAssignerFilterText("کد کارگاه ۹۰۲۸", branch = "   ", row = ""),
        )
    }
}

package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.StakeHolderRole
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Presentation mapping for ذینفعان rows.
 *
 * `stackType` is a numeric code on the wire ("1".."4"); it reaches the screen as a [StakeHolderRole]
 * whose wording lives in strings.xml, matching the old app's own mapping.
 */
class WorkshopStackHolderUiMapperTest {

    @Test
    fun `stackType codes map to their roles`() {
        assertEquals(StakeHolderRole.BOARD_MEMBER, holder(stackType = "1").toPresentation().role)
        assertEquals(StakeHolderRole.SIGNATORY, holder(stackType = "2").toPresentation().role)
        assertEquals(StakeHolderRole.CEO, holder(stackType = "3").toPresentation().role)
        assertEquals(StakeHolderRole.REPRESENTATIVE, holder(stackType = "4").toPresentation().role)
        // Padding the service leaves on a code does not hide the role.
        assertEquals(StakeHolderRole.CEO, holder(stackType = " 3 ").toPresentation().role)
    }

    /** The old app prints a code outside its table as it came, rather than hiding it. */
    @Test
    fun `an unknown stackType has no role and keeps its own text`() {
        val mapped = holder(stackType = "کارفرما").toPresentation()

        assertNull(mapped.role)
        assertEquals("کارفرما", mapped.stackType)
    }

    @Test
    fun `a blank stackType has no role and renders as a dash`() {
        val blank = holder(stackType = "").toPresentation()
        val spaces = holder(stackType = "   ").toPresentation()

        assertNull(blank.role)
        assertEquals("-", blank.stackType)
        assertNull(spaces.role)
        assertEquals("-", spaces.stackType)
    }

    /** Four codes, each its own role — a copy-pasted code would silently shadow another. */
    @Test
    fun `every role has its own code`() {
        assertEquals(StakeHolderRole.entries.size, StakeHolderRole.entries.map { it.code }.toSet().size)
    }

    private fun holder(stackType: String) = WorkshopStackHolderDN(
        stackId = 1,
        nationalId = "0024567891",
        firstName = "محمد",
        lastName = "محمدی",
        stackType = stackType,
    )
}

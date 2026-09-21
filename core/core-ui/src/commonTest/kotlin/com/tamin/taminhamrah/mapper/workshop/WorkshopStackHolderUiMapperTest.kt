package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Presentation mapping for ذینفعان rows.
 *
 * `stackType` is a numeric code on the wire ("1".."4"); it must reach the presentation
 * layer as a Persian role label, matching the legacy Android reference mapping.
 */
class WorkshopStackHolderUiMapperTest {

    @Test
    fun `stackType codes map to Persian role titles`() {
        assertEquals("اعضای هیئت مدیره", holder(stackType = "1").toPresentation().stackType)
        assertEquals("صاحبان امضا", holder(stackType = "2").toPresentation().stackType)
        assertEquals("مدیرعامل", holder(stackType = "3").toPresentation().stackType)
        assertEquals("نماینده", holder(stackType = "4").toPresentation().stackType)
    }

    @Test
    fun `unknown or unlisted stackType passes through`() {
        assertEquals("کارفرما", holder(stackType = "کارفرما").toPresentation().stackType)
    }

    @Test
    fun `empty or blank stackType renders as dash`() {
        assertEquals("-", holder(stackType = "").toPresentation().stackType)
        assertEquals("-", holder(stackType = "   ").toPresentation().stackType)
    }

    private fun holder(stackType: String) = WorkshopStackHolderDN(
        stackId = 1,
        nationalId = "0024567891",
        firstName = "محمد",
        lastName = "محمدی",
        stackType = stackType,
    )
}

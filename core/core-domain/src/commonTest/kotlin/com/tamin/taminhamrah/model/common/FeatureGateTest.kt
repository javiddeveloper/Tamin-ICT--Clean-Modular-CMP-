package com.tamin.taminhamrah.model.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * The one gate every screen that reads a flag off the menu shares — profile, treatment, home and
 * services all turn a [FeatureStatus] into a tap decision through this rather than repeating their
 * own `when`.
 */
class FeatureGateTest {

    @Test
    fun enabledOpensPlainly() {
        assertIs<FeatureGate.Open>(FeatureStatus.Enabled.toGate())
    }

    @Test
    fun disabledBlocksWithTheServersMessage() {
        val gate = FeatureStatus.Disabled("سرویس غیرفعال است").toGate()
        val blocked = assertIs<FeatureGate.Blocked>(gate)
        assertEquals("سرویس غیرفعال است", blocked.message)
    }

    @Test
    fun temporaryDisabledBlocksTheSameWayAsDisabled() {
        val gate = FeatureStatus.TemporaryDisabled("موقتاً در دسترس نیست").toGate()
        val blocked = assertIs<FeatureGate.Blocked>(gate)
        assertEquals("موقتاً در دسترس نیست", blocked.message)
    }

    @Test
    fun enabledWithErrorOpensAndCarriesTheWarning() {
        val gate = FeatureStatus.EnabledWithError("توجه").toGate()
        val warned = assertIs<FeatureGate.OpenWithWarning>(gate)
        assertEquals("توجه", warned.message)
    }

    @Test
    fun webViewOpensTheUrl() {
        val gate = FeatureStatus.WebView("https://tamin.ir").toGate()
        val web = assertIs<FeatureGate.OpenWeb>(gate)
        assertEquals("https://tamin.ir", web.url)
    }

    @Test
    fun serverMessageIsNullForTheStatesThatHaveNothingToSay() {
        assertNull(FeatureStatus.Enabled.serverMessage)
        assertNull(FeatureStatus.WebView("https://tamin.ir").serverMessage)
    }

    @Test
    fun serverMessageSurfacesForEveryOtherState() {
        assertEquals("a", FeatureStatus.Disabled("a").serverMessage)
        assertEquals("b", FeatureStatus.TemporaryDisabled("b").serverMessage)
        assertEquals("c", FeatureStatus.EnabledWithError("c").serverMessage)
    }
}

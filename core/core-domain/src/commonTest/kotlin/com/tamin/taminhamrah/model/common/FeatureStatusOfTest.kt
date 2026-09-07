package com.tamin.taminhamrah.model.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The rule the home campaigns carousel filters on: a card is shown only when tapping it would open
 * something. The dangerous case is a flag the server did not return at all — that reads as
 * `Disabled(null)`, which has no message to explain itself with, so a card left visible for it
 * would swallow the tap silently.
 */
class FeatureStatusOfTest {

    private fun menuRow(
        flag: FeatureFlag,
        status: MenuServiceStatusDN? = MenuServiceStatusDN.ACTIVE,
        active: Boolean? = true,
        url: String? = null,
        message: String? = null,
    ) = MainServiceDN(
        id = flag.id,
        active = active,
        status = status,
        url = url,
        message = message,
    )

    @Test
    fun aFlagMissingFromTheMenuIsDisabledWithNothingToSay() {
        val status = emptyList<MainServiceDN>().featureStatusOf(FeatureFlag.HOUSEWIFE_INSURANCE)

        assertEquals(FeatureStatus.Disabled(null), status)
        assertFalse(status.opensSomething)
    }

    @Test
    fun activeEnabledWithErrorAndWebViewAllOpenSomething() {
        val menu = listOf(
            menuRow(FeatureFlag.HOUSEWIFE_INSURANCE, MenuServiceStatusDN.ACTIVE),
            menuRow(
                FeatureFlag.FREELANCE_INSURANCE,
                MenuServiceStatusDN.ENABLED_WITH_ERROR,
                message = "warning",
            ),
            menuRow(
                FeatureFlag.STUDENT_INSURANCE,
                MenuServiceStatusDN.WEB_VIEW,
                url = "https://eservices.tamin.ir",
            ),
        )

        assertTrue(menu.featureStatusOf(FeatureFlag.HOUSEWIFE_INSURANCE).opensSomething)
        assertTrue(menu.featureStatusOf(FeatureFlag.FREELANCE_INSURANCE).opensSomething)
        assertTrue(menu.featureStatusOf(FeatureFlag.STUDENT_INSURANCE).opensSomething)
    }

    @Test
    fun everyBlockedShapeOpensNothing() {
        val blocked = listOf(
            menuRow(FeatureFlag.HOUSEWIFE_INSURANCE, active = false, message = "not eligible"),
            menuRow(FeatureFlag.HOUSEWIFE_INSURANCE, MenuServiceStatusDN.DISABLED),
            menuRow(FeatureFlag.HOUSEWIFE_INSURANCE, MenuServiceStatusDN.TEMPORARY_DISABLED),
            menuRow(FeatureFlag.HOUSEWIFE_INSURANCE, MenuServiceStatusDN.COMPLETELY_DISABLED),
        )

        blocked.forEach { row ->
            assertFalse(
                listOf(row).featureStatusOf(FeatureFlag.HOUSEWIFE_INSURANCE).opensSomething,
                "expected ${row.active} / ${row.status} to be treated as closed",
            )
        }
    }

    @Test
    fun anInactiveFlagStillCarriesItsServerMessage() {
        val menu = listOf(
            menuRow(
                FeatureFlag.FREELANCE_INSURANCE,
                MenuServiceStatusDN.TEMPORARY_DISABLED,
                message = "سرویس موقتاً در دسترس نیست",
            ),
        )

        assertEquals(
            FeatureStatus.TemporaryDisabled("سرویس موقتاً در دسترس نیست"),
            menu.featureStatusOf(FeatureFlag.FREELANCE_INSURANCE),
        )
    }

    @Test
    fun aWebViewRowWithNoUrlFallsBackToEnabledRatherThanCrashing() {
        val menu = listOf(menuRow(FeatureFlag.STUDENT_INSURANCE, MenuServiceStatusDN.WEB_VIEW))

        assertEquals(FeatureStatus.Enabled, menu.featureStatusOf(FeatureFlag.STUDENT_INSURANCE))
    }
}

package com.tamin.taminhamrah.data.menu

import com.tamin.taminhamrah.data.feature.FakeRepositoryForFeatureManager
import com.tamin.taminhamrah.data.feature.FeatureManagerImpl
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.commonSource.mockMenuData
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.useCases.common.DedicatedScreenFlags
import com.tamin.taminhamrah.useCases.common.GetVisibleServicesUseCase
import com.tamin.taminhamrah.useCases.common.ServiceCatalogAudience
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Runs the *real* `mockMenuData` (mapped exactly the way `CommonRepositoryImpl` maps it) through
 * the actual use cases every screen calls, for every single service — not a hand-picked fixture —
 * so a `FeatureFlag` the mock forgot to carry a row for, or a service the "خدمات" filter forgot to
 * exclude, shows up here as a failing assertion instead of a screen that quietly disables itself in
 * the running app.
 */
class RealMenuFeatureCoverageTest {

    private val realMenu = mockMenuData.map { it.toDomain() }

    private fun featureManager(): FeatureManagerImpl {
        val repository = FakeRepositoryForFeatureManager()
        repository.mainMenuResult = realMenu
        return FeatureManagerImpl(repository)
    }

    /**
     * `FeatureManagerImpl.getFeatureStatus()` must agree, flag by flag, with what the real menu's
     * own row actually says (`featureStatusOf()`, the same helper the home screen uses against an
     * already-loaded menu) — not a fallback that happens to look the same for most rows. A flag with
     * no row at all resolves `Disabled(null)`, which is correct for it, not a coverage gap.
     */
    @Test
    fun `every FeatureFlag resolves the real menu's own status, not a fallback`() = runTest {
        val manager = featureManager()

        FeatureFlag.entries.forEach { flag ->
            val status = manager.getFeatureStatus(flag).first()
            val expected = realMenu.featureStatusOf(flag)
            assertEquals(expected, status, "expected $flag to resolve $expected (the real menu's own row), was $status")
        }
    }

    /**
     * The services tab's filter, run against the real menu: every flag with a dedicated screen in
     * profile or the treatment hub must be gone, and — just as important — nothing *else* is
     * accidentally swept away with them.
     */
    @Test
    fun `the services tab excludes exactly the dedicated-screen flags, nothing more or less`() = runTest {
        val repository = FakeRepositoryForFeatureManager()
        repository.mainMenuResult = realMenu
        val useCase = GetVisibleServicesUseCase(repository)

        val visible = useCase(ServiceCatalogAudience.TAMIN_SERVICES_TAB, "1.0.0", false).first()
        val visibleIds = visible.mapNotNull { it.id }.mapNotNull { FeatureFlag.fromId(it) }.toSet()

        DedicatedScreenFlags.all.forEach { flag ->
            assertFalse(flag in visibleIds, "expected $flag to be excluded from the services tab, but it was still there")
        }

        val expectedVisible = FeatureFlag.entries.toSet() - DedicatedScreenFlags.all - KnownAliasesWithNoOwnMockRow
        assertEquals(expectedVisible, visibleIds)
    }

    private companion object {
        /**
         * Kept in sync with `MockMenuDataCoverageTest` (core-network) by hand — different modules'
         * test source sets aren't visible to each other. Pre-existing, documented gaps
         * (`docs/vault/Feature-Flags.md` §5f): legacy/alias ids nothing currently routes to on their
         * own, with no row in `mockMenuData` at all.
         */
        val KnownAliasesWithNoOwnMockRow = setOf(
            FeatureFlag.OBJECTION_INSURANCE_HISTORY_LEGACY,
            FeatureFlag.PRESCRIPTION_PENSIONER,
        )
    }
}

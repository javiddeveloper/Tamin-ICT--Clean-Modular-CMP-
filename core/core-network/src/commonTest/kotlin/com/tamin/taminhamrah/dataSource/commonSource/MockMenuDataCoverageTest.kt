package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * A [FeatureFlag] whose id has no row in [mockMenuData] silently resolves to `Disabled(null)`
 * everywhere `FeatureManager` reads it (`MainServiceDN?.toFeatureStatus()` treats "not in the menu"
 * as switched off) — exactly the class of bug that shipped the "Provisional ids" block (3001-3008)
 * bare before a follow-up commit added their mock rows. This is the guard against it happening again
 * for any flag, present or future.
 */
class MockMenuDataCoverageTest {

    @Test
    fun `every FeatureFlag has a matching row in mockMenuData, except the documented legacy aliases`() {
        val menuIds = mockMenuData.mapNotNull { it.id }.toSet()
        val missing = FeatureFlag.entries.filter { it.id !in menuIds && it !in KnownAliasesWithNoOwnMockRow }

        assertTrue(missing.isEmpty(), "these flags have no mockMenuData row and will resolve Disabled(null): $missing")
    }

    @Test
    fun `mockMenuData carries no duplicate ids`() {
        val ids = mockMenuData.mapNotNull { it.id }
        val duplicates = ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys

        assertTrue(duplicates.isEmpty(), "duplicate ids in mockMenuData: $duplicates")
    }

    private companion object {
        /**
         * Pre-existing, documented gaps (`docs/vault/Feature-Flags.md` §5f) — legacy/alias ids for a
         * feature a sibling id already carries a mock row for, not a flag that resolves to nothing:
         * `CALCULATE_WAGE_PENSION_109` shares `FeatureNavigation`'s branch with `CALCULATE_WAGE_PENSION`
         * (id 23, which has a row); `OBJECTION_INSURANCE_HISTORY_45` and `PRESCRIPTION_102` are
         * likewise alternate ids nothing currently routes to on their own. Real, but not this test's
         * job to fix — flag it here instead of quietly excluding it from coverage.
         */
        val KnownAliasesWithNoOwnMockRow = setOf(
            FeatureFlag.CALCULATE_WAGE_PENSION_109,
            FeatureFlag.OBJECTION_INSURANCE_HISTORY_45,
            FeatureFlag.PRESCRIPTION_102,
        )
    }
}

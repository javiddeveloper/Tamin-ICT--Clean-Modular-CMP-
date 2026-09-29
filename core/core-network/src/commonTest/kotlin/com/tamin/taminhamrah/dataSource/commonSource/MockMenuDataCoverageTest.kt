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
         * Pre-existing, documented gaps (`docs/vault/Feature-Flags.md` §5f) — legacy/alias ids
         * nothing currently routes to on their own, with no row in `mockMenuData` at all.
         */
        val KnownAliasesWithNoOwnMockRow = setOf(
            FeatureFlag.OBJECTION_INSURANCE_HISTORY_LEGACY,
            FeatureFlag.PRESCRIPTION_PENSIONER,
        )
    }
}

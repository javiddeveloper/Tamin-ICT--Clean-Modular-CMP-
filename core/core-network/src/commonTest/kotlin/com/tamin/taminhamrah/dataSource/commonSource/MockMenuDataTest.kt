package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The invariants `docs/vault/Feature-Flags.md` documents for the id scheme, checked against the
 * two places that actually have to agree: every row [mockMenuData] serves needs a matching
 * [FeatureFlag], and it needs exactly one — [FeatureFlag.fromId] returns the *first* match, so a
 * repeated id would silently steal a row from another flag instead of failing loudly.
 */
class MockMenuDataTest {

    @Test
    fun everyMockMenuRowIdIsUnique() {
        val duplicates = mockMenuData
            .groupBy { it.id }
            .filterValues { it.size > 1 }
            .mapValues { (_, rows) -> rows.map { it.name } }

        assertTrue(duplicates.isEmpty(), "These ids appear more than once in mockMenuData: $duplicates")
    }

    @Test
    fun everyMockMenuRowHasAMatchingFeatureFlag() {
        val unmatched = mockMenuData.filter { FeatureFlag.fromId(it.id) == null }

        assertTrue(
            unmatched.isEmpty(),
            "These mockMenuData rows have no FeatureFlag for their id: ${unmatched.map { it.id to it.name }}",
        )
    }
}

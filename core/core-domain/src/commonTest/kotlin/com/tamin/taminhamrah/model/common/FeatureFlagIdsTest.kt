package com.tamin.taminhamrah.model.common

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * `FeatureFlag.id` is a plain `Int` property, not something the compiler keeps unique — two entries
 * can share a value by mistake, and [FeatureFlag.Companion.fromId] would then silently resolve to
 * whichever one `entries.find` happens to hit first, mis-routing the other everywhere ids are looked
 * up (the menu, deep links, agent actions). Nothing else in the codebase enforces this.
 */
class FeatureFlagIdsTest {

    @Test
    fun everyFeatureFlagHasAUniqueId() {
        val duplicates = FeatureFlag.entries
            .groupBy { it.id }
            .filterValues { it.size > 1 }
            .mapValues { (_, flags) -> flags.map { it.name } }

        assertTrue(duplicates.isEmpty(), "These ids are shared by more than one FeatureFlag: $duplicates")
    }
}

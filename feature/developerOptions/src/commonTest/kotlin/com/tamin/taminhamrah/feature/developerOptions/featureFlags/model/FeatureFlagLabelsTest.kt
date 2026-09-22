package com.tamin.taminhamrah.feature.developerOptions.featureFlags.model

import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureFlagLabelsTest {

    @Test
    fun everyFlagHasANonBlankLabel() {
        FeatureFlag.entries.forEach { flag ->
            assertTrue(flag.persianLabel().isNotBlank(), "expected $flag to have a label")
        }
    }

    /**
     * `menu.json`'s own `name` for these two ids is a generic placeholder, not what the feature
     * actually is — `docs/vault/Feature-Flags.md` §5a documents `STACK_HOLDER_LIST`'s real meaning,
     * and `HomeServiceMembership` documents `OCCURRENCE`'s. The label must follow those, not the
     * sample menu.
     */
    @Test
    fun theTwoDocumentedMenuJsonMismatchesFollowTheRealMeaning() {
        assertEquals("معرفی نماینده اشخاص حقوقی", FeatureFlag.STACK_HOLDER_LIST.persianLabel())
        assertEquals("اعلام حادثه", FeatureFlag.OCCURRENCE.persianLabel())
    }
}

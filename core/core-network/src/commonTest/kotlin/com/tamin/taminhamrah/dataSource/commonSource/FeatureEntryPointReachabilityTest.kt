package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.toFeatureFlag
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The third invariant [MockMenuDataTest] doesn't check: every [FeatureFlag] a live entry point
 * — a deep link, an assistant action, or the home screen's history-summary card — can open must
 * still have a row in [mockMenuData]. A flag with no row reads as [FeatureFlag.Disabled] with no
 * message (`toFeatureStatus`), so the caller silently does nothing instead of opening or explaining
 * itself — see MR !247 review, item 6, for the incident this test would have caught.
 */
class FeatureEntryPointReachabilityTest {

    @Test
    fun everyEntryPointFlagHasAMenuRow() {
        val menuIds = mockMenuData.map { it.id }.toSet()

        val deepLinkTargets = DeepLinkKey.entries.map { "DeepLinkKey.${it.name}" to it.flag }
        val agentActionTargets = AgentActionKey.entries.mapNotNull { key ->
            key.toFeatureFlag()?.let { "AgentActionKey.${key.name}" to it }
        }
        // HomeViewModel.handleIntent: HomeIntent.OnHistorySummaryClick.
        val homeScreenTargets = listOf("HomeIntent.OnHistorySummaryClick" to FeatureFlag.COMBINED_RECORD)

        val unreachable = (deepLinkTargets + agentActionTargets + homeScreenTargets)
            .filter { (_, flag) -> flag.id !in menuIds }

        assertTrue(
            unreachable.isEmpty(),
            "These entry points target a FeatureFlag with no row in mockMenuData: $unreachable",
        )
    }
}

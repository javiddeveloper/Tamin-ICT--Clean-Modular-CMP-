package com.tamin.taminhamrah.feature.agent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.agent.ui.AgentScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

/**
 * Dedicated route for the Agent module.
 */
@Serializable
data object AgentRoute

/**
 * Helper method to navigate to the Agent screen.
 *
 * `launchSingleTop` because the entry point is a button that stays tappable while the opening
 * transition runs — without it a double tap stacks two assistants on the back stack.
 */
fun NavController.navigateToAgent() {
    navigate(AgentRoute) { launchSingleTop = true }
}

/**
 * Registers the Agent screen in the NavGraph.
 *
 * The assistant never navigates to another feature itself: every link it shows goes through
 * `LocalDeepLinkHandler`, so the host's deep link gate (and its feature flag check) applies.
 */
fun NavGraphBuilder.agentScreen(onNavigateBack: () -> Unit = {}) {
    composableWithFadeTransitions<AgentRoute> {
        AgentScreen(onNavigateBack = onNavigateBack)
    }
}

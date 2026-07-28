package com.tamin.taminhamrah.feature.agent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.agent.ui.AgentScreen
import kotlinx.serialization.Serializable

/**
 * Dedicated route for the Agent module.
 */
@Serializable
data object AgentRoute

/**
 * Helper method to navigate to the Agent screen.
 */
fun NavController.navigateToAgent() {
    navigate(AgentRoute)
}

/**
 * Stable ids for screens the assistant can hand the user off to.
 *
 * The agent module must not depend on other feature modules, so it emits one of
 * these ids in a [com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent.DeepLink]
 * bubble and the host (nav graph) maps it to the real route. Only add an id here
 * once the destination screen actually exists.
 */
object AgentDestination {
    const val DISABILITY_PENSION = "disability_pension"
    const val DEFERRED_INSTALLMENT = "deferred_installment"
    const val CONTRACTS = "contracts"
    const val WORKSHOPS = "workshops"
    const val PRESCRIPTION = "prescription"
}

/**
 * Registers the Agent screen in the NavGraph.
 *
 * @param onNavigateToDestination Maps an [AgentDestination] id to a real route.
 */
fun NavGraphBuilder.agentScreen(
    onNavigateToDestination: (String) -> Unit = {}
) {
    composable<AgentRoute> {
        AgentScreen(onNavigateToDestination = onNavigateToDestination)
    }
}

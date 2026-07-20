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
 * Registers the Agent screen in the NavGraph.
 */
fun NavGraphBuilder.agentScreen() {
    composable<AgentRoute> {
        AgentScreen()
    }
}

package com.tamin.taminhamrah.feature.agent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.agent.ui.AgentScreen
import kotlinx.serialization.Serializable

/**
 * مسیر اختصاصی مربوط به ماژول Agent
 */
@Serializable
data object AgentRoute

/**
 * متد کمکی برای هدایت به صفحه Agent
 */
fun NavController.navigateToAgent() {
    navigate(AgentRoute)
}

/**
 * ثبت صفحه Agent در NavGraph
 */
fun NavGraphBuilder.agentScreen() {
    composable<AgentRoute> {
        AgentScreen()
    }
}

package com.tamin.taminhamrah.feature.security

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.security.ui.SecurityScreen
import kotlinx.serialization.Serializable

@Serializable
object SecurityRoute

fun NavGraphBuilder.securityScreen(
    onNavigateBack: () -> Unit
) {
    composable<SecurityRoute> {
        SecurityScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

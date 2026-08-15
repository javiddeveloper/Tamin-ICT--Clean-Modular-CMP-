package com.tamin.taminhamrah.feature.developerOptions

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.developerOptions.ui.DeveloperOptionsScreen
import kotlinx.serialization.Serializable

@Serializable
object DeveloperOptionsRoute

fun NavGraphBuilder.developerOptionsScreen(
    onNavigateBack: () -> Unit
) {
    composable<DeveloperOptionsRoute> {
        DeveloperOptionsScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

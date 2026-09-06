package com.tamin.taminhamrah.feature.developerOptions

import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.developerOptions.ui.DeveloperOptionsScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
object DeveloperOptionsRoute

fun NavGraphBuilder.developerOptionsScreen(
    onNavigateBack: () -> Unit
) {
    composableWithFadeTransitions<DeveloperOptionsRoute> {
        DeveloperOptionsScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

package com.tamin.taminhamrah.feature.healthProfile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.healthProfile.ui.HealthProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object HealthProfileRoute

fun NavGraphBuilder.healthProfileScreen(
    onBack: () -> Unit
) {
    composable<HealthProfileRoute> {
        HealthProfileScreen(
            onBackClicked = onBack
        )
    }
}

fun NavController.navigateToHealthProfile() {
    navigate(HealthProfileRoute)
}

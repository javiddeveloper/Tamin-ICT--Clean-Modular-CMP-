package com.tamin.taminhamrah.feature.healthProfile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.healthProfile.ui.HealthProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class HealthProfileRoute(val nationalCode: String = "")

fun NavGraphBuilder.healthProfileScreen(
    onBack: () -> Unit
) {
    composableWithFadeTransitions<HealthProfileRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<HealthProfileRoute>()
        HealthProfileScreen(
            nationalCode = route.nationalCode,
            onBackClicked = onBack
        )
    }
}

fun NavController.navigateToHealthProfile(nationalCode: String = "") {
    navigate(HealthProfileRoute(nationalCode))
}


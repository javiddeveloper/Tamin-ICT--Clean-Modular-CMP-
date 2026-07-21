package com.tamin.taminhamrah.feature.treatment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentScreen
import com.tamin.taminhamrah.feature.treatment.ui.healthProfile.HealthProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object TreatmentRoute

@Serializable
data class HealthProfileRoute(val nationalCode: String)

fun NavController.navigateToTreatment(navOptions: NavOptions? = null) {
    navigate(TreatmentRoute, navOptions)
}

fun NavController.navigateToTreatment(builder: NavOptionsBuilder.() -> Unit) {
    navigate(TreatmentRoute, builder)
}

fun NavGraphBuilder.treatmentScreen(navController: NavController) {
    composable<TreatmentRoute> {
        TreatmentScreen(
            onOpenHealthProfile = { nationalCode ->
                navController.navigate(HealthProfileRoute(nationalCode))
            },
        )
    }

    composable<HealthProfileRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<HealthProfileRoute>()
        HealthProfileScreen(
            nationalCode = route.nationalCode,
            onBack = { navController.popBackStack() },
        )
    }
}

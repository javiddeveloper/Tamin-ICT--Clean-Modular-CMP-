package com.tamin.taminhamrah.feature.treatment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentScreen
import kotlinx.serialization.Serializable

/**
 * Destinations of the treatment ("درمان") tab.
 *
 * Every sub-flow is its own destination inside [TreatmentRoute.Graph], so the system back button
 * unwinds through the nav back stack rather than through screen-local state. To add a sub-flow:
 * declare a route here, add a `composable<...>` to [treatmentGraph], and hand the hub a callback
 * that navigates to it.
 */
@Serializable
sealed interface TreatmentRoute {
    /** Parent graph; used to tell whether any treatment screen is on top. */
    @Serializable
    data object Graph : TreatmentRoute

    /** The dashboard hub. */
    @Serializable
    data object Main : TreatmentRoute
}

fun NavController.navigateToTreatment(navOptions: NavOptions? = null) {
    navigate(TreatmentRoute.Main, navOptions)
}

fun NavController.navigateToTreatment(builder: NavOptionsBuilder.() -> Unit) {
    navigate(TreatmentRoute.Main, builder)
}

fun NavGraphBuilder.treatmentGraph(
    navController: NavController,
    onNavigateToHealthProfile: (nationalCode: String) -> Unit,
    onBack: () -> Unit,
) {
    navigation<TreatmentRoute.Graph>(startDestination = TreatmentRoute.Main) {
        composable<TreatmentRoute.Main> {
            TreatmentScreen(
                onOpenHealthProfile = onNavigateToHealthProfile,
            )
        }
    }
}


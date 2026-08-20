package com.tamin.taminhamrah.feature.historyobjection

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.historyobjection.ui.HistoryObjectionScreen
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.HistoryObjectionStepperScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object HistoryObjectionRoute

/** [requestNumber] null means create a new record; non-null means edit an existing draft. */
@Serializable
data class HistoryObjectionStepperRoute(val requestNumber: String? = null)

fun NavGraphBuilder.historyObjectionScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<HistoryObjectionRoute> {
        HistoryObjectionScreen(
            onNavigateBack = onBack,
            onNavigateToAddNew = { navController.navigateToHistoryObjectionStepper() },
            onNavigateToEdit = { requestNumber -> navController.navigateToHistoryObjectionStepper(requestNumber) },
        )
    }
}

fun NavGraphBuilder.historyObjectionStepperScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<HistoryObjectionStepperRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<HistoryObjectionStepperRoute>()
        HistoryObjectionStepperScreen(
            requestNumber = route.requestNumber,
            onNavigateBack = onBack,
        )
    }
}

fun NavController.navigateToHistoryObjection() {
    navigate(HistoryObjectionRoute)
}

fun NavController.navigateToHistoryObjectionStepper(requestNumber: String? = null) {
    navigate(HistoryObjectionStepperRoute(requestNumber))
}

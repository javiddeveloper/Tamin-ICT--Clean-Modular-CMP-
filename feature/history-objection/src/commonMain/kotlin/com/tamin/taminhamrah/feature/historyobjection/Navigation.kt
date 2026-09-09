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

/**
 * [requestNumber] null means create a new record; non-null means edit an existing draft.
 * [rowIndex] disambiguates which row to edit when several not-exist declarations share the same
 * [requestNumber] — required for the same reason `deletenotexist` needs both (see
 * docs/vault/History-Objection.md).
 */
@Serializable
data class HistoryObjectionStepperRoute(val requestNumber: String? = null, val rowIndex: String? = null)

fun NavGraphBuilder.historyObjectionScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<HistoryObjectionRoute> {
        HistoryObjectionScreen(
            onNavigateBack = onBack,
            onNavigateToAddNew = { navController.navigateToHistoryObjectionStepper() },
            onNavigateToEdit = { requestNumber, rowIndex ->
                navController.navigateToHistoryObjectionStepper(requestNumber, rowIndex)
            },
        )
    }
}

fun NavGraphBuilder.historyObjectionStepperScreen(
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    composableWithFadeTransitions<HistoryObjectionStepperRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<HistoryObjectionStepperRoute>()
        HistoryObjectionStepperScreen(
            requestNumber = route.requestNumber,
            rowIndex = route.rowIndex,
            onNavigateBack = onBack,
            onNavigateHome = onNavigateHome,
        )
    }
}

fun NavController.navigateToHistoryObjection() {
    navigate(HistoryObjectionRoute)
}

fun NavController.navigateToHistoryObjectionStepper(requestNumber: String? = null, rowIndex: String? = null) {
    navigate(HistoryObjectionStepperRoute(requestNumber, rowIndex))
}

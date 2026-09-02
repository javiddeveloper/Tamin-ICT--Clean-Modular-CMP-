package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsRoute
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.ContractRowsScreen
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object WorkshopsListRoute

/**
 * Every destination the list launches into carries the workshop identity in the route itself.
 *
 * The old app passed it through bundle keys, and one screen read a key nobody wrote, so it
 * silently never loaded. A typed route makes that particular failure impossible.
 */
@Serializable
data class PaymentSheetsRoute(val workshopId: String, val branchCode: String, val workshopName: String = "")

/**
 * ردیف‌های پیمان, which is the one destination here that is also a services-grid entry.
 *
 * Both halves of the identity default to blank, because the grid knows no workshop — the screen
 * then asks for one. The drill-down from جزئیات کارگاه fills them in and the list loads at once.
 */
@Serializable
data class ContractRowsRoute(val workshopId: String = "", val branchCode: String = "")

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsListRoute)
}

/** The `FeatureFlag.CONTRACT_INFO` entry — no workshop yet, so the screen opens its picker. */
fun NavController.navigateToContractRows() {
    navigate(ContractRowsRoute())
}

fun NavGraphBuilder.workshopsScreen(
    navController: NavController,
    @Suppress("UNUSED_PARAMETER") onOpenUrl: (String) -> Unit,
) {
    composableWithFadeTransitions<WorkshopsListRoute> {
        WorkshopsRoute(
            onBack = { navController.popBackStack() },
            onOpenAction = { action, workshopId, branchCode, workshopName ->
                navController.navigate(action.route(workshopId, branchCode, workshopName))
            },
        )
    }

    composableWithFadeTransitions<ContractRowsRoute> { entry ->
        val route = entry.toRoute<ContractRowsRoute>()
        ContractRowsScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            onBack = { navController.popBackStack() },
        )
    }

    composableWithFadeTransitions<PaymentSheetsRoute> { entry ->
        val route = entry.toRoute<PaymentSheetsRoute>()
        PaymentSheetsScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
        )
    }
}

/**
 * Where each menu entry goes.
 *
 * One `when` over the enum, so adding an action is a compile error here until it has a
 * destination, rather than a menu row that quietly does nothing.
 */
private fun WorkshopAction.route(
    workshopId: String,
    branchCode: String,
    workshopName: String,
): Any = when (this) {
    WorkshopAction.PAYMENT_SHEETS -> PaymentSheetsRoute(workshopId, branchCode, workshopName)
    WorkshopAction.CONTRACT_ROWS -> ContractRowsRoute(workshopId, branchCode)
}

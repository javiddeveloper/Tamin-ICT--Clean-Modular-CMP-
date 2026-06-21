package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsScreen
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import kotlinx.serialization.Serializable

@Serializable
data object WorkshopsRoute

@Serializable
data class PaymentSheetsRoute(
    val workshopId: String,
    val branchCode: String
)

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsRoute)
}

fun NavGraphBuilder.workshopsScreen(navController: NavController) {

    composable<WorkshopsRoute> {
        WorkshopsScreen(
            navigateToPaymentSheets = { workshopId, branchCode ->
                navController.navigate(PaymentSheetsRoute(workshopId, branchCode))
            }
        )
    }

    composable<PaymentSheetsRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PaymentSheetsRoute>()
        PaymentSheetsScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }
}

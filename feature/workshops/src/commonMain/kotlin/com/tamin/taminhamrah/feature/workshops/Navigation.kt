package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsScreen
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryScreen
import kotlinx.serialization.Serializable

@Serializable
data object WorkshopsRoute

@Serializable
data class PaymentSheetsRoute(
    val workshopId: String,
    val branchCode: String
)

@Serializable
data class WorkshopDebitRoute(
    val workshopId: String,
    val branchCode: String
)

@Serializable
data class WorkshopDebtInquiryRoute(
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
            },
            navigateToWorkshopDebit = { workshopId, branchCode ->
                navController.navigate(WorkshopDebitRoute(workshopId, branchCode))
            },
            navigateToWorkshopDebtInquiry = { workshopId, branchCode ->
                navController.navigate(WorkshopDebtInquiryRoute(workshopId, branchCode))
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

    composable<WorkshopDebitRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopDebitRoute>()
        WorkshopDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composable<WorkshopDebtInquiryRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopDebtInquiryRoute>()
        WorkshopDebtInquiryScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }
}

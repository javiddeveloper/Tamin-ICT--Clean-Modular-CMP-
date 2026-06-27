package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsScreen
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopMembers.WorkshopMembersScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders.WorkshopStackholdersScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersScreen
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

@Serializable
data class ObjectionableDebitRoute(
    val workshopId: String,
    val branchCode: String
)

@Serializable
data class ManagementDebitRoute(
    val workshopId: String,
    val branchCode: String
)

@Serializable
data class WorkshopMembersRoute(
    val workshopId: String,
    val branchCode: String
)

@Serializable
data class WorkshopStackholdersRoute(
    val workshopId: String,
    val branchCode: String
)

@Serializable
data class WorkshopRecentlyAddedMembersRoute(
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
            },
            navigateToObjectionableDebit = { workshopId, branchCode ->
                navController.navigate(ObjectionableDebitRoute(workshopId, branchCode))
            },
            navigateToManagementDebit = { workshopId, branchCode ->
                navController.navigate(ManagementDebitRoute(workshopId, branchCode))
            },
            navigateToWorkshopMembers = { workshopId, branchCode ->
                navController.navigate(WorkshopMembersRoute(workshopId, branchCode))
            },
            navigateToWorkshopStackholders = { workshopId, branchCode ->
                navController.navigate(WorkshopStackholdersRoute(workshopId, branchCode))
            },
            navigateToWorkshopRecentlyAddedMembers = { workshopId, branchCode ->
                navController.navigate(WorkshopRecentlyAddedMembersRoute(workshopId, branchCode))
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

    composable<ObjectionableDebitRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ObjectionableDebitRoute>()
        ObjectionableDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composable<ManagementDebitRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ManagementDebitRoute>()
        ManagementDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composable<WorkshopMembersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopMembersRoute>()
        WorkshopMembersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composable<WorkshopStackholdersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopStackholdersRoute>()
        WorkshopStackholdersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composable<WorkshopRecentlyAddedMembersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopRecentlyAddedMembersRoute>()
        WorkshopRecentlyAddedMembersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }
}

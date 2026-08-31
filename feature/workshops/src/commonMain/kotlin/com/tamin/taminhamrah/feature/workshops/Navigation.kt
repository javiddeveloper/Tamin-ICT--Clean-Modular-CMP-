package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsRoute
import com.tamin.taminhamrah.feature.workshops.ui.demandDocuments.DemandDocumentsScreen
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersScreen
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

@Serializable
data class WorkshopDebitRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String = "",
)

/**
 * مطالبات is keyed on the debt, not the workshop: it is opened from a row of گردش حساب بدهی
 * rather than from the کارگاه menu, so it is the only destination here that does not start from
 * a workshop identity.
 */
@Serializable
data class DemandDocumentsRoute(
    val debitNumber: String,
    val branchCode: String,
    val workshopName: String = "",
)

@Serializable
data class WorkshopDebtInquiryRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String = "",
)

@Serializable
data class ObjectionableDebitRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String = "",
)

@Serializable
data class WorkshopRecentlyAddedMembersRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String = "",
)

@Serializable
data class ManagementDebitRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String = "",
)

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsListRoute)
}

/**
 * @param onOpenUrl leaves the app: the debt payment page is hosted outside it.
 */
fun NavGraphBuilder.workshopsScreen(
    navController: NavController,
    onOpenUrl: (String) -> Unit,
) {
    composableWithFadeTransitions<WorkshopsListRoute> {
        WorkshopsRoute(
            onBack = { navController.popBackStack() },
            onOpenAction = { action, workshopId, branchCode, workshopName ->
                navController.navigate(action.route(workshopId, branchCode, workshopName))
            },
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

    composableWithFadeTransitions<WorkshopDebitRoute> { entry ->
        val route = entry.toRoute<WorkshopDebitRoute>()
        WorkshopDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
            onOpenDocuments = { debitNumber, branchCode ->
                navController.navigate(
                    DemandDocumentsRoute(debitNumber, branchCode, route.workshopName),
                )
            },
            onOpenUrl = onOpenUrl,
        )
    }

    composableWithFadeTransitions<DemandDocumentsRoute> { entry ->
        val route = entry.toRoute<DemandDocumentsRoute>()
        DemandDocumentsScreen(
            debitNumber = route.debitNumber,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
        )
    }

    composableWithFadeTransitions<WorkshopDebtInquiryRoute> { entry ->
        val route = entry.toRoute<WorkshopDebtInquiryRoute>()
        WorkshopDebtInquiryScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
        )
    }
    composableWithFadeTransitions<ObjectionableDebitRoute> { entry ->
        val route = entry.toRoute<ObjectionableDebitRoute>()
        ObjectionableDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
        )
    }
    composableWithFadeTransitions<WorkshopRecentlyAddedMembersRoute> { entry ->
        val route = entry.toRoute<WorkshopRecentlyAddedMembersRoute>()
        WorkshopRecentlyAddedMembersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
        )
    }
    composableWithFadeTransitions<ManagementDebitRoute> { entry ->
        val route = entry.toRoute<ManagementDebitRoute>()
        ManagementDebitScreen(
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
    WorkshopAction.DEBIT_TURNOVER -> WorkshopDebitRoute(workshopId, branchCode, workshopName)
    WorkshopAction.DEBT_INQUIRY ->
        WorkshopDebtInquiryRoute(workshopId, branchCode, workshopName)
    WorkshopAction.OBJECTION ->
        ObjectionableDebitRoute(workshopId, branchCode, workshopName)
    WorkshopAction.NEW_MEMBER ->
        WorkshopRecentlyAddedMembersRoute(workshopId, branchCode, workshopName)
    WorkshopAction.ARTICLE_SIXTEEN ->
        ManagementDebitRoute(workshopId, branchCode, workshopName)
}

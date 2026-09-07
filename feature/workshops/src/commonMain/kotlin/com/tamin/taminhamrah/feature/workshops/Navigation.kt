package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsRoute
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.ContractRowsScreen
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp.LegalRepresentativeOtpScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.LegalRepresentativeListScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add.AddLegalRepresentativeScreen
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
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
data object LegalRepresentativeWorkshopsRoute

@Serializable
data class LegalRepresentativeOtpRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String,
    val branchName: String,
    val special: Boolean,
)

@Serializable
data class LegalRepresentativeListRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String,
    val branchName: String,
    val ticket: String,
    val special: Boolean,
)

@Serializable
data class AddLegalRepresentativeRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String,
    val branchName: String,
    val isEditMode: Boolean,
    val nationalCode: String,
    val hasElectronicNotification: Boolean,
    val hasInternetList: Boolean,
    val hasInsuredRegistration: Boolean,
    val special: Boolean,
)

/**
 * ردیف‌های پیمان, which is the one destination here that is also a services-grid entry.
 *
 * Both halves of the identity default to blank, because the grid knows no workshop — the screen
 * then asks for one. The drill-down from جزئیات کارگاه fills them in and the list loads at once.
 */
@Serializable
data class ContractRowsRoute(val workshopId: String = "", val branchCode: String = "")

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

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsListRoute)
}

/** The `FeatureFlag.CONTRACT_INFO` entry — no workshop yet, so the screen opens its picker. */
fun NavController.navigateToContractRows() {
    navigate(ContractRowsRoute())
}

fun NavController.navigateToLegalRepresentativeWorkshops() {
    navigate(LegalRepresentativeWorkshopsRoute)
}

/**
 * کارگاه‌های کارفرما: the list, and the جزئیات screen it opens.
 *
 * Each service the detail menu offers arrives as its own task, bringing its route, its destination
 * and its row in [com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction] together. The
 * `when` that maps an action to its route is introduced with the first of them, so that from then
 * on the compiler refuses an action with nowhere to go.
 */
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

    // ─── Legal representative introduction (معرفی نماینده اشخاص حقوقی) ────────────────

    composableWithFadeTransitions<LegalRepresentativeWorkshopsRoute> {
        LegalRepresentativeWorkshopsScreen(
            onBackClicked = { navController.popBackStack() },
            onOpenWorkshop = { workshop: LegalRepresentativeWorkshopPR ->
                navController.navigate(
                    LegalRepresentativeOtpRoute(
                        workshopId = workshop.workshopId,
                        branchCode = workshop.branchCode,
                        workshopName = workshop.workshopName,
                        branchName = workshop.branchName ?: workshop.branchCode,
                        special = workshop.special,
                    )
                )
            },
        )
    }

    composableWithFadeTransitions<LegalRepresentativeOtpRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<LegalRepresentativeOtpRoute>()
        LegalRepresentativeOtpScreen(
            workshopName = route.workshopName,
            workshopSubtitle = "کد کارگاه ${route.workshopId} · شعبهٔ ${route.branchName}",
            onBackClicked = { navController.popBackStack() },
            onVerified = { ticket ->
                navController.navigate(
                    LegalRepresentativeListRoute(
                        workshopId = route.workshopId,
                        branchCode = route.branchCode,
                        workshopName = route.workshopName,
                        branchName = route.branchName,
                        ticket = ticket,
                        special = route.special,
                    )
                ) {
                    popUpTo<LegalRepresentativeOtpRoute> { inclusive = true }
                }
            },
        )
    }

    composableWithFadeTransitions<LegalRepresentativeListRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<LegalRepresentativeListRoute>()
        LegalRepresentativeListScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            workshopSubtitle = "کد کارگاه ${route.workshopId} · شعبهٔ ${route.branchName}",
            ticket = route.ticket,
            onBackClicked = { navController.popBackStack() },
            onAddClicked = {
                navController.navigate(
                    AddLegalRepresentativeRoute(
                        workshopId = route.workshopId,
                        branchCode = route.branchCode,
                        workshopName = route.workshopName,
                        branchName = route.branchName,
                        isEditMode = false,
                        nationalCode = "",
                        hasElectronicNotification = false,
                        hasInternetList = false,
                        hasInsuredRegistration = false,
                        special = route.special,
                    )
                )
            },
            onEditClicked = { representative: LegalRepresentativePR ->
                navController.navigate(
                    AddLegalRepresentativeRoute(
                        workshopId = route.workshopId,
                        branchCode = route.branchCode,
                        workshopName = route.workshopName,
                        branchName = route.branchName,
                        isEditMode = true,
                        nationalCode = representative.nationalId,
                        hasElectronicNotification = representative.hasElectronicNotification,
                        hasInternetList = representative.hasInternetList,
                        hasInsuredRegistration = representative.hasInsuredRegistration,
                        special = route.special,
                    )
                )
            },
        )
    }

    composableWithFadeTransitions<AddLegalRepresentativeRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<AddLegalRepresentativeRoute>()
        AddLegalRepresentativeScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            workshopSubtitle = "کد کارگاه ${route.workshopId} · شعبهٔ ${route.branchName}",
            isEditMode = route.isEditMode,
            nationalCode = route.nationalCode,
            hasElectronicNotification = route.hasElectronicNotification,
            hasInternetList = route.hasInternetList,
            hasInsuredRegistration = route.hasInsuredRegistration,
            special = route.special,
            onBackClicked = { navController.popBackStack() },
            onSubmitted = { navController.popBackStack() },
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
    WorkshopAction.DEBT_INQUIRY ->
        WorkshopDebtInquiryRoute(workshopId, branchCode, workshopName)
    WorkshopAction.OBJECTION ->
        ObjectionableDebitRoute(workshopId, branchCode, workshopName)
}

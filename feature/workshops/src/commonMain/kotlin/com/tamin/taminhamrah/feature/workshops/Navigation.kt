package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
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
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp.LegalRepresentativeOtpScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.LegalRepresentativeListScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add.AddLegalRepresentativeScreen
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
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

@Serializable
data object LegalRepresentativeWorkshopsRoute

@Serializable
data class LegalRepresentativeOtpRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String,
    val branchName: String,
)

@Serializable
data class LegalRepresentativeListRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String,
    val branchName: String,
    val ticket: String,
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
)

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsRoute)
}

fun NavController.navigateToLegalRepresentativeWorkshops() {
    navigate(LegalRepresentativeWorkshopsRoute)
}

fun NavGraphBuilder.workshopsScreen(navController: NavController) {

    composableWithFadeTransitions<WorkshopsRoute> {
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

    composableWithFadeTransitions<PaymentSheetsRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PaymentSheetsRoute>()
        PaymentSheetsScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<WorkshopDebitRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopDebitRoute>()
        WorkshopDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<WorkshopDebtInquiryRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopDebtInquiryRoute>()
        WorkshopDebtInquiryScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<ObjectionableDebitRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ObjectionableDebitRoute>()
        ObjectionableDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<ManagementDebitRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ManagementDebitRoute>()
        ManagementDebitScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<WorkshopMembersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopMembersRoute>()
        WorkshopMembersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<WorkshopStackholdersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopStackholdersRoute>()
        WorkshopStackholdersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
        )
    }

    composableWithFadeTransitions<WorkshopRecentlyAddedMembersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkshopRecentlyAddedMembersRoute>()
        WorkshopRecentlyAddedMembersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode
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
            onBackClicked = { navController.popBackStack() },
            onSubmitted = { navController.popBackStack() },
        )
    }
}

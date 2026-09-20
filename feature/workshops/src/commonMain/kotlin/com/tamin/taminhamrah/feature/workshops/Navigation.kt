package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsRoute
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractDetailScreen
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractsScreen
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.ComputationalBaseDetailScreen
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.ComputationalBasesScreen
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestScreen
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.demandDocuments.DemandDocumentsScreen
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.ContractRowsScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add.AddLegalRepresentativeScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.LegalRepresentativeListScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp.LegalRepresentativeOtpScreen
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsScreen
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopMembers.WorkshopMembersScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersScreen
import com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders.WorkshopStackholdersScreen
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.ui.sharedViewModel
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
    /** `01` حقیقی / `02` حقوقی. Travels only so the payment body can carry `nationalType`. */
    val characterCode: String = "",
    /** The حقوقی workshop's national id, blank for a حقیقی one. Sent as `nationalId`. */
    val legalNationalId: String = "",
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

/**
 * The four واگذارندگان destinations, as one graph.
 *
 * The graph is what `sharedViewModel` scopes the ViewModel to, so all four resolve one instance and
 * the list's rows are still in hand two screens deep.
 */
@Serializable
data object AssignerContractsGraph

/**
 * واگذارندگان, the second services-grid entry that is also a کارگاه drill-down.
 *
 * Both halves default to blank because the grid knows no workshop — the screen then asks for one
 * through its search sheet. The drill-down from جزئیات کارگاه fills them in and the list loads
 * at once.
 */
@Serializable
data class AssignerContractsRoute(val workshopId: String = "", val branchCode: String = "")

/**
 * جزئیات پیمان.
 *
 * Carries the two keys that identify a پیمان within the list, and the screen finds it there — the
 * same shape the other two drill-downs use, so no destination depends on a selection having been
 * written to state before it composed.
 */
@Serializable
data class AssignerContractDetailRoute(
    val contractRow: String,
    val contractSequence: String,
)

/**
 * درخواست مفاصاحساب for one پیمان, found in the list by the same two keys جزئیات پیمان is — so the form
 * never depends on a selection having been written to state before it composed.
 */
@Serializable
data class SettlementRequestRoute(
    val contractRow: String,
    val contractSequence: String,
)

/**
 * مبانی محاسباتی of one پیمان.
 *
 * Carries all four identity keys rather than reading them off shared state, so the screen refetches
 * correctly after process death and can never be opened against a پیمان it was not given — the
 * service answers a partial set with another contract's bases. [workshopName] and [rowLabel] are
 * the design's own subtitle line, and carrying them keeps this destination self-sufficient.
 */
@Serializable
data class ComputationalBasesRoute(
    val workshopId: String,
    val branchCode: String,
    val contractRow: String,
    val contractSequence: String,
    val workshopName: String = "",
    val rowLabel: String = "",
)

/**
 * جزئیات مبنا.
 *
 * Carries the شمارهٔ سند the row was drawn with, and the screen finds its مبنا among the bases
 * already in state — so it never depends on a "currently selected" field having been updated before
 * this destination composed.
 */
@Serializable
data class ComputationalBaseDetailRoute(val letterNumber: String)

/** مبانی محاسباتی of this پیمان. Offered only when all four keys are present, so never a partial set. */
private fun AssignerContractPR.basesRoute() = ComputationalBasesRoute(
    workshopId = card.workshopId,
    branchCode = card.branchCode,
    contractRow = contractRow,
    contractSequence = contractSequence,
    workshopName = card.name,
    rowLabel = card.rowLabel,
)

private fun AssignerContractPR.settlementRoute() = SettlementRequestRoute(
    contractRow = contractRow,
    contractSequence = contractSequence,
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

@Serializable
data class WorkshopMembersRoute(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String = "",
)

@Serializable
data class WorkshopStackholdersRoute(
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

/** The `FeatureFlag.CONTRACT_INFO` entry — no workshop yet, so the screen opens its picker. */
fun NavController.navigateToContractRows() {
    navigate(ContractRowsRoute())
}

/**
 * The `FeatureFlag.ASSIGNER_CONTRACT` entry — no workshop yet, so the screen opens its search.
 *
 * Navigates to the graph's start destination rather than to the graph, so the same call serves the
 * drill-down with its two codes filled in.
 */
fun NavController.navigateToAssignerContracts() {
    navigate(AssignerContractsRoute())
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
    onStartPayment: (PaymentRequestDN) -> Unit,
) {
    composableWithFadeTransitions<WorkshopsListRoute> {
        WorkshopsRoute(
            onBack = { navController.popBackStack() },
            onOpenAction = { navController.navigate(it.route()) },
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

    // ─── واگذارندگان ─────────────────────────────────────────────────────────────────
    //
    // Four destinations, one graph, one ViewModel. They are a single flow: the list response
    // already carries what جزئیات پیمان draws, so the drill-down costs no request — which only
    // works if all four resolve the *same* instance. `sharedViewModel` scopes it to this graph.
    //
    // The stack is real navigation rather than a nested state on one screen, so the system back
    // gesture walks it exactly as the design's own back button does (basedetail → base, base and
    // detail → list, list → out) with no back handler anywhere.
    navigation<AssignerContractsGraph>(startDestination = AssignerContractsRoute()) {
        composableWithFadeTransitions<AssignerContractsRoute> { entry ->
            val route = entry.toRoute<AssignerContractsRoute>()
            val viewModel = entry.sharedViewModel<AssignerContractsViewModel>(navController)
            AssignerContractsScreen(
                viewModel = viewModel,
                workshopId = route.workshopId,
                branchCode = route.branchCode,
                onBack = { navController.popBackStack() },
                onOpenDetail = { contract ->
                    navController.navigate(
                        AssignerContractDetailRoute(
                            contractRow = contract.contractRow,
                            contractSequence = contract.contractSequence,
                        )
                    )
                },
                // The action is disabled unless all four keys are present, so this cannot address a
                // partial set.
                onOpenBases = { navController.navigate(it.basesRoute()) },
                // Disabled on the card unless the four keys of the request id are present.
                onRequestSettlement = { navController.navigate(it.settlementRoute()) },
            )
        }

        composableWithFadeTransitions<AssignerContractDetailRoute> { entry ->
            val route = entry.toRoute<AssignerContractDetailRoute>()
            AssignerContractDetailScreen(
                viewModel = entry.sharedViewModel(navController),
                contractRow = route.contractRow,
                contractSequence = route.contractSequence,
                onBack = { navController.popBackStack() },
                onOpenBases = { navController.navigate(it.basesRoute()) },
                onRequestSettlement = { navController.navigate(it.settlementRoute()) },
            )
        }

        // The form has a ViewModel of its own; the list's is shared in only to find the پیمان.
        composableWithFadeTransitions<SettlementRequestRoute> { entry ->
            val route = entry.toRoute<SettlementRequestRoute>()
            SettlementRequestScreen(
                listViewModel = entry.sharedViewModel(navController),
                contractRow = route.contractRow,
                contractSequence = route.contractSequence,
                onBack = { navController.popBackStack() },
                // Straight to the پیمان‌ها, whether the form was opened from the list or from جزئیات.
                onDone = { navController.popBackStack<AssignerContractsRoute>(inclusive = false) },
            )
        }

        composableWithFadeTransitions<ComputationalBasesRoute> { entry ->
            val route = entry.toRoute<ComputationalBasesRoute>()
            ComputationalBasesScreen(
                viewModel = entry.sharedViewModel(navController),
                workshopId = route.workshopId,
                branchCode = route.branchCode,
                contractRow = route.contractRow,
                contractSequence = route.contractSequence,
                workshopName = route.workshopName,
                rowLabel = route.rowLabel,
                onBack = { navController.popBackStack() },
                onOpenBaseDetail = { navController.navigate(ComputationalBaseDetailRoute(it)) },
            )
        }

        composableWithFadeTransitions<ComputationalBaseDetailRoute> { entry ->
            ComputationalBaseDetailScreen(
                viewModel = entry.sharedViewModel(navController),
                letterNumber = entry.toRoute<ComputationalBaseDetailRoute>().letterNumber,
                onBack = { navController.popBackStack() },
            )
        }
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
            characterCode = route.characterCode,
            legalNationalId = route.legalNationalId,
            onBack = { navController.popBackStack() },
            onOpenDocuments = { debitNumber, branchCode ->
                navController.navigate(
                    DemandDocumentsRoute(debitNumber, branchCode, route.workshopName),
                )
            },
            onStartPayment = onStartPayment,
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
    composableWithFadeTransitions<WorkshopMembersRoute> { entry ->
        val route = entry.toRoute<WorkshopMembersRoute>()
        WorkshopMembersScreen(
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            workshopName = route.workshopName,
            onBack = { navController.popBackStack() },
        )
    }
    composableWithFadeTransitions<WorkshopStackholdersRoute> { entry ->
        val route = entry.toRoute<WorkshopStackholdersRoute>()
        WorkshopStackholdersScreen(
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
private fun WorkshopsEvent.Navigate.route(): Any = when (action) {
    WorkshopAction.PAYMENT_SHEETS -> PaymentSheetsRoute(workshopId, branchCode, workshopName)
    WorkshopAction.DEBIT_TURNOVER -> WorkshopDebitRoute(
        workshopId = workshopId,
        branchCode = branchCode,
        workshopName = workshopName,
        // گردش حساب بدهی is the only destination that pays, and paying needs the workshop's
        // character and legal id — which live on the list row and nowhere downstream.
        characterCode = characterCode,
        legalNationalId = legalNationalId,
    )

    WorkshopAction.CONTRACT_ROWS -> ContractRowsRoute(workshopId, branchCode)
    WorkshopAction.ASSIGNER_CONTRACTS -> AssignerContractsRoute(workshopId, branchCode)
    WorkshopAction.DEBT_INQUIRY ->
        WorkshopDebtInquiryRoute(workshopId, branchCode, workshopName)
    WorkshopAction.OBJECTION ->
        ObjectionableDebitRoute(workshopId, branchCode, workshopName)
    WorkshopAction.NEW_MEMBER ->
        WorkshopRecentlyAddedMembersRoute(workshopId, branchCode, workshopName)
    WorkshopAction.ARTICLE_SIXTEEN ->
        ManagementDebitRoute(workshopId, branchCode, workshopName)
    WorkshopAction.MEMBERS ->
        WorkshopMembersRoute(workshopId, branchCode, workshopName)
    WorkshopAction.STACKHOLDERS ->
        WorkshopStackholdersRoute(workshopId, branchCode, workshopName)
    WorkshopAction.ARTICLE_SIXTEEN ->
        ManagementDebitRoute(workshopId, branchCode, workshopName)
}

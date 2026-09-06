package com.tamin.taminhamrah.feature.contracts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.contracts.flow.ContractType
import com.tamin.taminhamrah.feature.contracts.flow.ui.ContractFlowScreen
import com.tamin.taminhamrah.feature.contracts.flow.ui.ContractFlowViewModel
import com.tamin.taminhamrah.feature.contracts.ui.ContractsScreen
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Serializable
data object ContractsRoute

@Serializable
data class ContractFlowRoute(val type: ContractType)

fun NavController.navigateToContracts() {
    navigate(ContractsRoute)
}

fun NavController.navigateToContractFlow(type: ContractType) {
    navigate(ContractFlowRoute(type))
}

fun NavController.navigateToStudentContract() = navigateToContractFlow(ContractType.STUDENT)

fun NavController.navigateToFreelanceContract() = navigateToContractFlow(ContractType.FREELANCE)

fun NavController.navigateToHousewifeContract() = navigateToContractFlow(ContractType.HOUSEWIFE)

fun NavController.navigateToOptionalContract() = navigateToContractFlow(ContractType.OPTIONAL)

fun NavGraphBuilder.contractsScreen(
    onBack: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    composableWithFadeTransitions<ContractsRoute> {
        ContractsScreen(
            onBackClicked = onBack,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
        )
    }
}

fun NavGraphBuilder.contractFlowScreen(
    onBack: () -> Unit,
    onShowRules: () -> Unit = {},
    onPaymentRequested: (contractNumber: String, amount: Long) -> Unit = { _, _ -> },
) {
    composableWithFadeTransitions<ContractFlowRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ContractFlowRoute>()
        ContractFlowScreen(
            onBack = onBack,
            onShowRules = onShowRules,
            onPaymentRequested = onPaymentRequested,
            viewModel = koinViewModel<ContractFlowViewModel>(named(route.type.koinQualifier)),
        )
    }
}

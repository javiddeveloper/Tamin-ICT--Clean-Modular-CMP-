package com.tamin.taminhamrah.feature.contracts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.contracts.ui.ContractsScreen
import com.tamin.taminhamrah.feature.contracts.ui.affairs.ContractAffairsRoute
import com.tamin.taminhamrah.feature.contracts.ui.affairs.ContractAffairsViewModel
import com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.ContractPaymentHistoryRoute
import com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.ContractPaymentHistoryViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.serialization.Serializable

import com.tamin.taminhamrah.model.common.FeatureFlag

@Serializable
data object ContractsRoute

@Serializable
data object ContractAffairsRoute

/** سوابق پرداخت — opened from «مشاهدهٔ پرداخت‌ها» in the امور قرارداد sheet. */
@Serializable
data class ContractPaymentHistoryRoute(
    val contractNumber: String,
    val insuranceType: String,
)

fun NavController.navigateToContracts() {
    navigate(ContractsRoute)
}

fun NavController.navigateToContractAffairs() {
    navigate(ContractAffairsRoute)
}

fun NavController.navigateToContractPaymentHistory(
    contractNumber: String,
    insuranceType: String,
) {
    navigate(ContractPaymentHistoryRoute(contractNumber, insuranceType))
}

fun NavGraphBuilder.contractsScreen(
    onBack: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit
) {
    composableWithFadeTransitions<ContractsRoute> {
        ContractsScreen(
            onBackClicked = onBack,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl
        )
    }
}

fun NavGraphBuilder.contractAffairsScreen(
    onBack: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onNavigateToPaymentHistory: (contractNumber: String, insuranceType: String) -> Unit,
) {
    composableWithFadeTransitions<ContractAffairsRoute> {
        val viewModel: ContractAffairsViewModel = koinViewModel()
        ContractAffairsRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
            onNavigateToPaymentHistory = onNavigateToPaymentHistory,
        )
    }
}

fun NavGraphBuilder.contractPaymentHistoryScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<ContractPaymentHistoryRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ContractPaymentHistoryRoute>()
        val viewModel: ContractPaymentHistoryViewModel = koinViewModel()
        ContractPaymentHistoryRoute(
            viewModel = viewModel,
            contractNumber = route.contractNumber,
            insuranceType = route.insuranceType,
            onBackClicked = onBack,
        )
    }
}

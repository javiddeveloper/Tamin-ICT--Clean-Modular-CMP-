package com.tamin.taminhamrah.feature.contracts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.contracts.ui.ContractsScreen
import com.tamin.taminhamrah.feature.contracts.ui.affairs.ContractAffairsRoute
import com.tamin.taminhamrah.feature.contracts.ui.affairs.ContractAffairsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.serialization.Serializable

import com.tamin.taminhamrah.model.common.FeatureFlag

@Serializable
data object ContractsRoute

@Serializable
data object ContractAffairsRoute

fun NavController.navigateToContracts() {
    navigate(ContractsRoute)
}

fun NavController.navigateToContractAffairs() {
    navigate(ContractAffairsRoute)
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
) {
    composableWithFadeTransitions<ContractAffairsRoute> {
        val viewModel: ContractAffairsViewModel = koinViewModel()
        ContractAffairsRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
        )
    }
}

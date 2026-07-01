package com.tamin.taminhamrah.feature.contracts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.contracts.ui.ContractsScreen
import kotlinx.serialization.Serializable

import com.tamin.taminhamrah.model.common.FeatureFlag

@Serializable
data object ContractsRoute

fun NavController.navigateToContracts() {
    navigate(ContractsRoute)
}

fun NavGraphBuilder.contractsScreen(
    onBack: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit
) {
    composable<ContractsRoute> {
        ContractsScreen(
            onBackClicked = onBack,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl
        )
    }
}

package com.tamin.taminhamrah.feature.optionalContract

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowQualifiers
import com.tamin.taminhamrah.feature.contractFlow.ui.ContractFlowScreen
import com.tamin.taminhamrah.feature.contractFlow.ui.ContractFlowViewModel
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Serializable
data object OptionalContractRoute

fun NavController.navigateToOptionalContract() {
    navigate(OptionalContractRoute)
}

fun NavGraphBuilder.optionalContractScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<OptionalContractRoute> {
        ContractFlowScreen(
            onBack = onBack,
            viewModel = koinViewModel<ContractFlowViewModel>(named(ContractFlowQualifiers.OPTIONAL)),
        )
    }
}

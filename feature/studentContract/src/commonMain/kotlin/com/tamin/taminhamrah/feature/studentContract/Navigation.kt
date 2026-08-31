package com.tamin.taminhamrah.feature.studentContract

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
data object StudentContractRoute

fun NavController.navigateToStudentContract() {
    navigate(StudentContractRoute)
}

fun NavGraphBuilder.studentContractScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<StudentContractRoute> {
        ContractFlowScreen(
            onBack = onBack,
            viewModel = koinViewModel<ContractFlowViewModel>(named(ContractFlowQualifiers.STUDENT)),
        )
    }
}

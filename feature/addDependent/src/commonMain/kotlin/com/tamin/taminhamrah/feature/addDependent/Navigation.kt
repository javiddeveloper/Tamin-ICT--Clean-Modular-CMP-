package com.tamin.taminhamrah.feature.addDependent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.addDependent.ui.AddDependentRoute
import com.tamin.taminhamrah.feature.addDependent.ui.AddDependentViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object AddDependentRoute

fun NavGraphBuilder.addDependentGraph(
    navController: NavController,
    onBack: () -> Unit
) {
    composable<AddDependentRoute> {
        val viewModel = koinViewModel<AddDependentViewModel>()
        AddDependentRoute(
            viewModel = viewModel,
            onBackClicked = onBack
        )
    }
}

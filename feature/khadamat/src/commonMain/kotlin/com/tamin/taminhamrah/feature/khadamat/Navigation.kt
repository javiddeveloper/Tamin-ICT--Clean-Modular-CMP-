package com.tamin.taminhamrah.feature.khadamat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.khadamat.ui.KhadamatRoute as KhadamatRouteComposables
import com.tamin.taminhamrah.feature.khadamat.ui.KhadamatViewModel
import com.tamin.taminhamrah.feature.khadamat.ui.contract.TaminScreens
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object KhadamatRoute

fun NavController.navigateToKhadamat(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(KhadamatRoute, builder)
}

fun NavGraphBuilder.khadamatScreen(
    onNavigateToRoute: (TaminScreens) -> Unit,
    onBackClicked: () -> Unit
) {
    composable<KhadamatRoute> {
        val khadamatViewModel: KhadamatViewModel = koinViewModel()
        KhadamatRouteComposables(
            viewModel = khadamatViewModel,
            onNavigateToRoute = onNavigateToRoute,
            onBackClicked = onBackClicked
        )
    }
}

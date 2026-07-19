package com.tamin.taminhamrah.feature.taminServices

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.taminServices.ui.TaminServicesRoute
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.ui.contract.TaminScreens
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TaminServicesRoute

fun NavController.navigateToTaminServices(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(TaminServicesRoute, builder)
}

fun NavGraphBuilder.TaminServicesScreen(
    onNavigateToRoute: (TaminScreens) -> Unit,
    onBackClicked: () -> Unit
) {
    composable<TaminServicesRoute> {
        val TaminServicesViewModel: TamminServicesViewModel = koinViewModel()
        TaminServicesRoute(
            viewModel = TaminServicesViewModel,
            onNavigateToRoute = onNavigateToRoute,
            onBackClicked = onBackClicked
        )
    }
}

package com.tamin.taminhamrah.feature.taminServices

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.taminServices.ui.TaminServicesRoute
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TaminServicesRoute

fun NavController.navigateToTaminServices(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(TaminServicesRoute, builder)
}

fun NavGraphBuilder.taminServicesScreen(
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    composableWithFadeTransitions<TaminServicesRoute> {
        val TaminServicesViewModel: TamminServicesViewModel = koinViewModel()
        TaminServicesRoute(
            viewModel = TaminServicesViewModel,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
            onBackClicked = onBackClicked
        )
    }
}

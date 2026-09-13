package com.tamin.taminhamrah.feature.weddingPresent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.weddingPresent.ui.WeddingPresentScreen
import com.tamin.taminhamrah.feature.weddingPresent.ui.calculate.WeddingPresentCalculateScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object WeddingPresentRoute

@Serializable
data object WeddingPresentCalculateRoute

fun NavController.navigateToWeddingPresent(navOptions: NavOptions? = null) {
    navigate(WeddingPresentRoute, navOptions)
}

fun NavController.navigateToWeddingPresentCalculate(navOptions: NavOptions? = null) {
    navigate(WeddingPresentCalculateRoute, navOptions)
}

fun NavGraphBuilder.weddingPresentScreen(
    onBack: () -> Unit,
    onNavigateToCalculate: () -> Unit = {},
) {
    composableWithFadeTransitions<WeddingPresentRoute> {
        WeddingPresentScreen(
            onBack = onBack,
            onNavigateToCalculate = onNavigateToCalculate,
        )
    }
}

fun NavGraphBuilder.weddingPresentCalculateScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<WeddingPresentCalculateRoute> {
        WeddingPresentCalculateScreen(onBack = onBack)
    }
}

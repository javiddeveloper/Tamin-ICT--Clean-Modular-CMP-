package com.tamin.taminhamrah.feature.weddingPresent

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.weddingPresent.ui.WeddingPresentScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object WeddingPresentRoute

fun NavController.navigateToWeddingPresent(navOptions: NavOptions? = null) {
    navigate(WeddingPresentRoute, navOptions)
}

fun NavGraphBuilder.weddingPresentScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<WeddingPresentRoute> {
        WeddingPresentScreen(onBack = onBack)
    }
}

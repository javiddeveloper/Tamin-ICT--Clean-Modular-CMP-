package com.tamin.taminhamrah.feature.calculateWagePension

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.calculateWagePension.ui.CalculateWagePensionScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object CalculateWagePensionRoute

fun NavController.navigateToCalculateWagePension(navOptions: NavOptions? = null) {
    navigate(CalculateWagePensionRoute, navOptions)
}

fun NavGraphBuilder.calculateWagePensionScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<CalculateWagePensionRoute> {
        CalculateWagePensionScreen(onBack = onBack)
    }
}

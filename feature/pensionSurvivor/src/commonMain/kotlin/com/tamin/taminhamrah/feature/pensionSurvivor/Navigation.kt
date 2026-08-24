package com.tamin.taminhamrah.feature.pensionSurvivor

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.PensionSurvivorScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object PensionSurvivorRoute

fun NavController.navigateToPensionSurvivor(navOptions: NavOptions? = null) {
    navigate(PensionSurvivorRoute, navOptions)
}

fun NavGraphBuilder.pensionSurvivorScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<PensionSurvivorRoute> {
        PensionSurvivorScreen(onBack = onBack)
    }
}

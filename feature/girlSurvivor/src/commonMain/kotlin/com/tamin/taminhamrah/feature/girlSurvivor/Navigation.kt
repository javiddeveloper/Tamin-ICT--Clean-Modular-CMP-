package com.tamin.taminhamrah.feature.girlSurvivor

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.girlSurvivor.ui.GirlSurvivorScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object GirlSurvivorRoute

fun NavController.navigateToGirlSurvivor(navOptions: NavOptions? = null) {
    navigate(GirlSurvivorRoute, navOptions)
}

fun NavGraphBuilder.girlSurvivorScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<GirlSurvivorRoute> {
        GirlSurvivorScreen(onBack = onBack)
    }
}

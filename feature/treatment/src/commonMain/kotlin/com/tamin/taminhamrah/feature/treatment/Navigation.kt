package com.tamin.taminhamrah.feature.treatment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentScreen
import kotlinx.serialization.Serializable

@Serializable
data object TreatmentRoute

fun NavController.navigateToTreatment(navOptions: NavOptions? = null) {
    navigate(TreatmentRoute, navOptions)
}

fun NavController.navigateToTreatment(builder: NavOptionsBuilder.() -> Unit) {
    navigate(TreatmentRoute, builder)
}

fun NavGraphBuilder.treatmentScreen() {
    composable<TreatmentRoute> {
        TreatmentScreen()
    }
}

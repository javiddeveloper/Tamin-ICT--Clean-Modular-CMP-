package com.tamin.taminhamrah.feature.fractionContract

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.fractionContract.ui.FractionContractRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object FractionContractRoute

fun NavController.navigateToFractionContract(navOptions: NavOptions? = null) {
    navigate(FractionContractRoute, navOptions)
}

fun NavGraphBuilder.fractionContractScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<FractionContractRoute> {
        FractionContractRoute(onBack = onBack)
    }
}

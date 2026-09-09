package com.tamin.taminhamrah.feature.deferredInstallment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.deferredInstallment.ui.DeferredInstallmentScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object DeferredInstallmentRoute

fun NavGraphBuilder.deferredInstallmentScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<DeferredInstallmentRoute> {
        DeferredInstallmentScreen(onBackClicked = onBack)
    }
}

fun NavController.navigateToDeferredInstallment() {
    navigate(DeferredInstallmentRoute)
}

package com.tamin.taminhamrah.feature.pregnancyPay

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.pregnancyPay.ui.PregnancyPayScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object PregnancyPayRoute

fun NavGraphBuilder.pregnancyPayScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<PregnancyPayRoute> {
        PregnancyPayScreen(
            onBackClicked = onBack,
        )
    }
}

fun NavController.navigateToPregnancyPay() {
    navigate(PregnancyPayRoute)
}

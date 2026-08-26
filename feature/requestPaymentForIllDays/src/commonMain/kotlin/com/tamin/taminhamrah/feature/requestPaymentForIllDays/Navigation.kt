package com.tamin.taminhamrah.feature.requestPaymentForIllDays

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.RequestPaymentForIllDaysScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object RequestPaymentForIllDaysRoute

fun NavController.navigateToRequestPaymentForIllDays(navOptions: NavOptions? = null) {
    navigate(RequestPaymentForIllDaysRoute, navOptions)
}

fun NavGraphBuilder.requestPaymentForIllDaysScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<RequestPaymentForIllDaysRoute> {
        RequestPaymentForIllDaysScreen(onBack = onBack)
    }
}

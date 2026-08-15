package com.tamin.taminhamrah.feature.orotezprotez

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.orotezprotez.ui.OrotezProtezScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object OrotezProtezRoute

fun NavGraphBuilder.orotezProtezScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<OrotezProtezRoute> {
        OrotezProtezScreen(
            onBackClicked = onBack,
        )
    }
}

fun NavController.navigateToOrotezProtez() {
    navigate(OrotezProtezRoute)
}

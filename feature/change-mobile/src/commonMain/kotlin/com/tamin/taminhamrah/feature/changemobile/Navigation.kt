package com.tamin.taminhamrah.feature.changemobile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.changemobile.ui.ChangeMobileScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object ChangeMobileRoute

fun NavGraphBuilder.changeMobileScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<ChangeMobileRoute> {
        ChangeMobileScreen(
            onBack = onBack,
        )
    }
}

fun NavController.navigateToChangeMobile() {
    navigate(ChangeMobileRoute)
}

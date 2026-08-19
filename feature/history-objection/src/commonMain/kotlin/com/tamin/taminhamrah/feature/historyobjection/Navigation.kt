package com.tamin.taminhamrah.feature.historyobjection

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.historyobjection.ui.HistoryObjectionScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object HistoryObjectionRoute

fun NavGraphBuilder.historyObjectionScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<HistoryObjectionRoute> {
        HistoryObjectionScreen(
            onNavigateBack = onBack,
        )
    }
}

fun NavController.navigateToHistoryObjection() {
    navigate(HistoryObjectionRoute)
}

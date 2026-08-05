package com.tamin.taminhamrah.feature.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.history.ui.HistoryScreen
import kotlinx.serialization.Serializable


@Serializable
data object HistoryRoute


fun NavController.navigateToHistory() {
    navigate(HistoryRoute)
}

fun NavGraphBuilder.historyScreen() {
    composableWithFadeTransitions<HistoryRoute> {
        HistoryScreen()
    }
}

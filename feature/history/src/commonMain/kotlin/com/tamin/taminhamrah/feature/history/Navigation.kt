package com.tamin.taminhamrah.feature.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.history.ui.HistoryScreen
import kotlinx.serialization.Serializable


@Serializable
data object HistoryRoute


fun NavController.navigateToHistory() {
    navigate(HistoryRoute)
}

fun NavGraphBuilder.historyScreen() {
    composable<HistoryRoute> {
        HistoryScreen()
    }
}

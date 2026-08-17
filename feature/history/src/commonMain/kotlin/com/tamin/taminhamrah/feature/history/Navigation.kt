package com.tamin.taminhamrah.feature.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.history.ui.HistoryScreen
import com.tamin.taminhamrah.feature.history.ui.jobinfo.HistoryJobInfoScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable


@Serializable
data object HistoryRoute

@Serializable
data object HistoryJobInfoRoute


fun NavController.navigateToHistory() {
    navigate(HistoryRoute)
}

fun NavController.navigateToHistoryJobInfo() {
    navigate(HistoryJobInfoRoute)
}

fun NavGraphBuilder.historyScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<HistoryRoute> {
        HistoryScreen(onBackClicked = onBack)
    }
}

fun NavGraphBuilder.historyJobInfoScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<HistoryJobInfoRoute> {
        HistoryJobInfoScreen(onBackClicked = onBack)
    }
}

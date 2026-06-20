package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsScreen
import kotlinx.serialization.Serializable

@Serializable
data object WorkshopsRoute

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsRoute)
}

fun NavGraphBuilder.workshopsScreen() {
    composable<WorkshopsRoute> {
        WorkshopsScreen()
    }
}

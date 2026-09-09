package com.tamin.taminhamrah.feature.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.history.ui.HistoryScreen
import com.tamin.taminhamrah.feature.history.ui.HistoryViewModel
import com.tamin.taminhamrah.feature.history.ui.jobinfo.HistoryJobInfoScreen
import com.tamin.taminhamrah.feature.history.ui.yearWorkshops.YearWorkshopsScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.ui.sharedViewModel
import kotlinx.serialization.Serializable

/**
 * «کلیه سوابق» and the year it drills into.
 *
 * A graph rather than a single destination, so both screens resolve the *same*
 * [HistoryViewModel] through [sharedViewModel]: a career is one load, and opening a year must not
 * re-run it. Navigating to [HistoryRoute] still lands on the page it always did — a graph resolves
 * to its start destination — so nothing outside this file changes.
 */
@Serializable
data object HistoryRoute

@Serializable
internal data object HistoryMainRoute

/**
 * «کارگاه‌های سال» — one year's employers.
 *
 * The year travels in the route rather than in the state. It is what the screen is *for*, so a
 * process death restores the right one, and the page behind it never has to hold a "currently
 * open" field that a second drill-down could contradict.
 */
@Serializable
internal data class YearWorkshopsRoute(val year: String)

@Serializable
data object HistoryJobInfoRoute

fun NavController.navigateToHistory() {
    navigate(HistoryRoute)
}

fun NavController.navigateToHistoryJobInfo() {
    navigate(HistoryJobInfoRoute)
}

fun NavGraphBuilder.historyScreen(navController: NavController, onBack: () -> Unit) {
    navigation<HistoryRoute>(startDestination = HistoryMainRoute) {
        composableWithFadeTransitions<HistoryMainRoute> { backStackEntry ->
            HistoryScreen(
                viewModel = backStackEntry.sharedViewModel<HistoryViewModel>(navController),
                onBackClicked = onBack,
                onOpenYearWorkshops = { year -> navController.navigate(YearWorkshopsRoute(year)) },
            )
        }

        composableWithFadeTransitions<YearWorkshopsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<YearWorkshopsRoute>()
            YearWorkshopsScreen(
                year = route.year,
                viewModel = backStackEntry.sharedViewModel<HistoryViewModel>(navController),
                onBackClicked = { navController.popBackStack() },
            )
        }
    }
}

fun NavGraphBuilder.historyJobInfoScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<HistoryJobInfoRoute> {
        HistoryJobInfoScreen(onBackClicked = onBack)
    }
}

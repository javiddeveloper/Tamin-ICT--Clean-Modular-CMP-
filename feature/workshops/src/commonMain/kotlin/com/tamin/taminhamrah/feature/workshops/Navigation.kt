package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object WorkshopsListRoute

fun NavController.navigateToWorkshops() {
    navigate(WorkshopsListRoute)
}

/**
 * کارگاه‌های کارفرما: the list, and the جزئیات screen it opens.
 *
 * Each service the detail menu offers arrives as its own task, bringing its route, its destination
 * and its row in [com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction] together. The
 * `when` that maps an action to its route is introduced with the first of them, so that from then
 * on the compiler refuses an action with nowhere to go.
 */
fun NavGraphBuilder.workshopsScreen(
    navController: NavController,
    @Suppress("UNUSED_PARAMETER") onOpenUrl: (String) -> Unit,
) {
    composableWithFadeTransitions<WorkshopsListRoute> {
        WorkshopsRoute(
            onBack = { navController.popBackStack() },
            // No services yet: WorkshopAction is empty until a screen exists to open, so no menu
            // row can be tapped. The first service restores the dispatch.
            onOpenAction = { _, _, _, _ -> },
        )
    }
}

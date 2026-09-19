package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.CompleteEmployerInfoRoute as CompleteEmployerInfoContent

@Serializable
data object CompleteEmployerInfoRoute

fun NavController.navigateToCompleteEmployerInfo() {
    navigate(CompleteEmployerInfoRoute)
}

/**
 * تکمیل اطلاعات کارفرمایی — its own destination, registered separately from کارگاه‌های کارفرما.
 *
 * It shares this module with the workshop screens but is not one of them: it is reached from the
 * services menu rather than from a workshop row, so it has no [ui.model.WorkshopAction] entry and
 * nothing about it belongs in that feature's graph.
 *
 * Kept in its own file for a second reason. The workshop services land one merge request at a
 * time, and every one of them adds a route, a destination and a `when` arm to `Navigation.kt`; a
 * line of this feature's sitting among them would collide with each in turn.
 */
fun NavGraphBuilder.completeEmployerInfoScreen(navController: NavController) {
    composableWithFadeTransitions<CompleteEmployerInfoRoute> {
        CompleteEmployerInfoContent(
            onBack = { navController.popBackStack() },
        )
    }
}

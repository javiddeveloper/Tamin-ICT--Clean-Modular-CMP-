package com.tamin.taminhamrah.feature.retirementPension

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.retirementPension.ui.RetirementPensionRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object RetirementPensionDestination

fun NavController.navigateToRetirementPension(navOptions: NavOptions? = null) {
    navigate(RetirementPensionDestination, navOptions)
}

/**
 * All three screens of the service — intro, wizard and tracking — live behind this one
 * destination: they share the request being filled in, and splitting them would mean rebuilding
 * that from the back stack every time the user steps between them.
 */
fun NavGraphBuilder.retirementPensionScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<RetirementPensionDestination> {
        RetirementPensionRoute(onBack = onBack)
    }
}

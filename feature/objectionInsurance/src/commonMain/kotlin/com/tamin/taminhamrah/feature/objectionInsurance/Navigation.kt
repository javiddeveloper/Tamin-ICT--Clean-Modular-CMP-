package com.tamin.taminhamrah.feature.objectionInsurance

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.objectionInsurance.ui.ObjectionInsuranceScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object ObjectionInsuranceRoute

fun NavController.navigateToObjectionInsurance(navOptions: NavOptions? = null) {
    navigate(ObjectionInsuranceRoute, navOptions)
}

fun NavGraphBuilder.objectionInsuranceScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<ObjectionInsuranceRoute> {
        ObjectionInsuranceScreen(onBack = onBack)
    }
}

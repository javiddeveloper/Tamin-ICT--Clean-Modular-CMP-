package com.tamin.taminhamrah.feature.inquiryEducation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.inquiryEducation.ui.InquiryEducationScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object InquiryEducationRoute

fun NavController.navigateToInquiryEducation(navOptions: NavOptions? = null) {
    navigate(InquiryEducationRoute, navOptions)
}

fun NavGraphBuilder.inquiryEducationScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<InquiryEducationRoute> {
        InquiryEducationScreen(onBack = onBack)
    }
}

package com.tamin.taminhamrah.feature.pensionInquiry

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.pensionInquiry.ui.PensionInquiryScreen
import kotlinx.serialization.Serializable

@Serializable
data object PensionInquiryRoute

fun NavController.navigateToPensionInquiry(navOptions: NavOptions? = null) {
    navigate(PensionInquiryRoute, navOptions)
}

fun NavController.navigateToPensionInquiry(builder: NavOptionsBuilder.() -> Unit) {
    navigate(PensionInquiryRoute, builder)
}

fun NavGraphBuilder.pensionInquiryScreen() {
    composable<PensionInquiryRoute> {
        PensionInquiryScreen()
    }
}

package com.tamin.taminhamrah.feature.pensionStatusInquiry

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.PensionStatusInquiryRoute
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.PensionStatusInquiryViewModel
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object PensionStatusInquiryRoute

fun NavController.navigateToPensionStatusInquiry(navOptions: NavOptions? = null) {
    navigate(PensionStatusInquiryRoute, navOptions)
}

fun NavGraphBuilder.pensionStatusInquiryGraph(onBack: () -> Unit) {
    composableWithFadeTransitions<PensionStatusInquiryRoute> {
        val viewModel = koinViewModel<PensionStatusInquiryViewModel>()
        PensionStatusInquiryRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
        )
    }
}

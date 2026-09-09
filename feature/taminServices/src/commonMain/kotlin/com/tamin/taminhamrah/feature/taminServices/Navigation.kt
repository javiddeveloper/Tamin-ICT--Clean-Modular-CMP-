package com.tamin.taminhamrah.feature.taminServices

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.tamin.taminhamrah.feature.taminServices.occurrence.OccurrenceScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesRoute
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionRoute
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionViewModel
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.SendHistoryToInstitutionsScreen
import com.tamin.taminhamrah.feature.taminServices.ui.TaminServicesRoute
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TaminServicesRoute

@Serializable
data object SendInsuranceHistoryToInstitutionsRoute

@Serializable
data object InspectionRoute

@Serializable
data object OccurrenceRoute

@Serializable
data object EmployerOnlineServicesRoute

fun NavController.navigateToTaminServices(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(TaminServicesRoute, builder)
}

fun NavController.navigateToSendInsuranceHistoryToInstitutions(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(SendInsuranceHistoryToInstitutionsRoute, builder)
}

fun NavController.navigateToOccurrence(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(OccurrenceRoute, builder)
}

fun NavGraphBuilder.taminServicesScreen(
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    composableWithFadeTransitions<TaminServicesRoute> {
        val TaminServicesViewModel: TamminServicesViewModel = koinViewModel()
        TaminServicesRoute(
            viewModel = TaminServicesViewModel,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
            onBackClicked = onBackClicked
        )
    }
}

fun NavGraphBuilder.sendInsuranceHistoryToInstitutionsScreen(
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    composableWithFadeTransitions<SendInsuranceHistoryToInstitutionsRoute> {
        SendHistoryToInstitutionsScreen(
            onBack = onBack,
            onDone = onDone
        )
    }
}

fun NavController.navigateToInspection(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(InspectionRoute, builder)
}

fun NavGraphBuilder.inspectionScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<InspectionRoute> {
        val viewModel: InspectionViewModel = koinViewModel()
        InspectionRoute(
            viewModel = viewModel,
            onBackClicked = onBack
        )
    }
}

fun NavGraphBuilder.occurrenceScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    composableWithFadeTransitions<OccurrenceRoute> {
        OccurrenceScreen(
            onBack = onBack,
            onDone = onDone,
        )
    }
}

fun NavController.navigateToEmployerOnlineServices(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(EmployerOnlineServicesRoute, builder)
}

fun NavGraphBuilder.employerOnlineServicesScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<EmployerOnlineServicesRoute> {
        val viewModel: EmployerOnlineServicesViewModel = koinViewModel()
        EmployerOnlineServicesRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
        )
    }
}

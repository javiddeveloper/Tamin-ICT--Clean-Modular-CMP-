package com.tamin.taminhamrah.feature.treatment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentScreen
import kotlinx.serialization.Serializable

@Serializable
data object TreatmentRoute

fun NavController.navigateToTreatment(navOptions: NavOptions? = null) {
    navigate(TreatmentRoute, navOptions)
}

fun NavController.navigateToTreatment(builder: NavOptionsBuilder.() -> Unit) {
    navigate(TreatmentRoute, builder)
}

fun NavGraphBuilder.treatmentScreen(
    onOpenMedicalRecords: () -> Unit = {},
    onOpenHealthProfile: () -> Unit = {},
    onOpenCenters: () -> Unit = {},
    onOpenPrescriptions: () -> Unit = {},
    onOpenMedicalApprovals: () -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onSearch: () -> Unit = {},
) {
    composable<TreatmentRoute> {
        TreatmentScreen(
            onOpenMedicalRecords = onOpenMedicalRecords,
            onOpenHealthProfile = onOpenHealthProfile,
            onOpenCenters = onOpenCenters,
            onOpenPrescriptions = onOpenPrescriptions,
            onOpenMedicalApprovals = onOpenMedicalApprovals,
            onOpenMiscClaims = onOpenMiscClaims,
            onSearch = onSearch,
        )
    }
}

package com.tamin.taminhamrah.feature.treatment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentScreen
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientItems
import com.tamin.taminhamrah.feature.treatment.ui.records.MedicalRecordsScreen
import com.tamin.taminhamrah.feature.treatment.ui.records.RecordDetailScreen
import com.tamin.taminhamrah.feature.treatment.ui.records.RecordTab
import com.tamin.taminhamrah.ui.sharedViewModel
import kotlinx.serialization.Serializable

/**
 * Destinations of the treatment ("درمان") tab.
 *
 * Every sub-flow is its own destination inside [TreatmentRoute.Graph], so the system back button
 * unwinds through the nav back stack rather than through screen-local state. To add a sub-flow:
 * declare a route here, add a `composable<...>` to [treatmentGraph], and hand the hub a callback
 * that navigates to it — nothing outside this file needs to change.
 */
@Serializable
sealed interface TreatmentRoute {
    /** Parent graph; used to tell whether any treatment screen is on top. */
    @Serializable
    data object Graph : TreatmentRoute

    /** The dashboard hub. */
    @Serializable
    data object Main : TreatmentRoute

    /**
     * Medical records ("سوابق درمانی") for [nationalCode], opened on [tab].
     *
     * Prescriptions are not a screen of their own: the hub's «نسخه‌های الکترونیک» tile lands here
     * with [RecordTab.MEDICINE] selected, while «سوابق درمانی من» opens on [RecordTab.ALL].
     */
    @Serializable
    data class MedicalRecords(
        val nationalCode: String,
        val tab: RecordTab = RecordTab.ALL
    ) : TreatmentRoute

    /** One record: prescribed items, cost breakdown and the PDF exports. */
    @Serializable
    data class RecordDetail(
        val nationalCode: String,
        val noteHeadId: String
    ) : TreatmentRoute
}

fun NavController.navigateToTreatment(navOptions: NavOptions? = null) {
    navigate(TreatmentRoute.Graph, navOptions)
}

fun NavController.navigateToTreatment(builder: NavOptionsBuilder.() -> Unit) {
    navigate(TreatmentRoute.Graph, builder)
}

fun NavGraphBuilder.treatmentGraph(
    navController: NavController,
    onBack: () -> Unit
) {
    navigation<TreatmentRoute.Graph>(startDestination = TreatmentRoute.Main) {
        composable<TreatmentRoute.Main> {
            TreatmentScreen(
                onNavigateToRecords = { nationalCode, tab ->
                    navController.navigate(TreatmentRoute.MedicalRecords(nationalCode, tab))
                }
            )
        }

        composable<TreatmentRoute.MedicalRecords> { backStackEntry ->
            val route = backStackEntry.toRoute<TreatmentRoute.MedicalRecords>()
            // Graph-scoped so the patient filter reuses the dashboard's already-loaded dependants.
            val treatmentViewModel = backStackEntry.sharedViewModel<TreatmentViewModel>(navController)
            val treatmentState by treatmentViewModel.uiState.collectAsState()

            MedicalRecordsScreen(
                nationalCode = route.nationalCode,
                initialTab = route.tab,
                patients = treatmentState.toPatientItems(),
                onBack = onBack,
                onOpenRecord = { noteHeadId ->
                    navController.navigate(TreatmentRoute.RecordDetail(route.nationalCode, noteHeadId))
                }
            )
        }

        composable<TreatmentRoute.RecordDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TreatmentRoute.RecordDetail>()
            RecordDetailScreen(
                nationalCode = route.nationalCode,
                noteHeadId = route.noteHeadId,
                onBack = onBack
            )
        }
    }
}

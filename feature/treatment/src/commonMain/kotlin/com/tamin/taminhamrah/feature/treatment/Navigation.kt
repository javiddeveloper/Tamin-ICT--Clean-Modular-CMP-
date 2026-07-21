package com.tamin.taminhamrah.feature.treatment

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientList
import com.tamin.taminhamrah.feature.treatment.ui.records.MedicalRecordsScreen
import com.tamin.taminhamrah.feature.treatment.ui.records.RecordDetailScreen
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
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
     * with [RecordTab.MEDICINE] selected, while «سوابق درمانی من» opens on [RecordTab.Default].
     */
    @Serializable
    data class MedicalRecords(
        val nationalCode: String,
        val tab: RecordTab = RecordTab.Default
    ) : TreatmentRoute

    /** One record: prescribed items, cost breakdown and the PDF exports. */
    @Serializable
    data class RecordDetail(
        val nationalCode: String,
        val noteHeadId: String,
        val type: String,
        val flagSata: String,
        // Passed through because the detail screen's own list is empty — it loads only the detail
        // and price, so these header fields come from the record that was tapped, as the old app
        // carried them in the navigation bundle.
        val docName: String,
        val prescDate: String,
        val trackingCode: String,
    ) : TreatmentRoute
}

fun NavController.navigateToTreatment(navOptions: NavOptions? = null) {
    navigate(TreatmentRoute.Main, navOptions)
}

fun NavController.navigateToTreatment(builder: NavOptionsBuilder.() -> Unit) {
    navigate(TreatmentRoute.Main, builder)
}

fun NavGraphBuilder.treatmentGraph(
    navController: NavController,
    onBack: () -> Unit
) {
    navigation<TreatmentRoute.Graph>(startDestination = TreatmentRoute.Main) {
        composable<TreatmentRoute.Main> { backStackEntry ->
            // The hub and the records screen share one graph-scoped ViewModel, so the dependants
            // the hub loads are the same ones the records patient filter reads. Without this the
            // records screen gets a fresh ViewModel that never loaded, and the filter is empty.
            val treatmentViewModel = backStackEntry.sharedViewModel<TreatmentViewModel>(navController)
            TreatmentScreen(
                viewModel = treatmentViewModel,
                onOpenMedicalRecords = { nationalCode ->
                    navController.navigate(TreatmentRoute.MedicalRecords(nationalCode, RecordTab.Default))
                },
                onOpenPrescriptions = { nationalCode ->
                    navController.navigate(
                        TreatmentRoute.MedicalRecords(nationalCode, RecordTab.MEDICINE),
                    )
                },
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
                patients = treatmentState.toPatientList(),
                onBack = onBack,
                onOpenRecord = { record ->
                    navController.navigate(
                        TreatmentRoute.RecordDetail(
                            nationalCode = route.nationalCode,
                            noteHeadId = record.noteHeadEprescID,
                            type = record.prescType,
                            flagSata = record.flagSata,
                            docName = record.docName,
                            prescDate = record.prescDate,
                            trackingCode = record.trackingCode,
                        )
                    )
                }
            )
        }

        composable<TreatmentRoute.RecordDetail>(
            // A short slide+fade in from the side, so opening a record reads as a push forward
            // rather than a hard cut. The reverse plays on back.
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn()
            },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut()
            },
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<TreatmentRoute.RecordDetail>()
            RecordDetailScreen(
                nationalCode = route.nationalCode,
                noteHeadId = route.noteHeadId,
                type = route.type,
                flagSata = route.flagSata,
                docName = route.docName,
                prescDate = route.prescDate,
                trackingCode = route.trackingCode,
                onBack = onBack,
            )
        }
    }
}

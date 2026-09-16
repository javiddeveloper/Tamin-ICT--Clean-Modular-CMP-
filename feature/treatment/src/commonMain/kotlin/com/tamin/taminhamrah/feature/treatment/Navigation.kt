package com.tamin.taminhamrah.feature.treatment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentScreen
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.records.MedicalRecordsScreen
import com.tamin.taminhamrah.feature.treatment.ui.records.RecordDetailScreen
import com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts.TreatmentCostsRoute
import com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations.MedicalConfirmationsRoute
import kotlinx.serialization.Serializable

/**
 * Destinations of the treatment ("درمان") tab.
 *
 * Every sub-flow is its own destination inside [TreatmentRoute.Graph], so the system back button
 * unwinds through the nav back stack rather than through screen-local state. To add a sub-flow:
 * declare a route here, add a `composable<...>` to [treatmentGraph], and hand the hub a callback
 * that navigates to it.
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
        /** Blank when entered from a shortcut; the records screen falls back to the selected patient. */
        val nationalCode: String = "",
        val tab: RecordTab = RecordTab.Default
    ) : TreatmentRoute

    /** «خسارت متفرقه» — the miscellaneous-claim certificates and their PDF exports. */
    @Serializable
    data object TreatmentCosts : TreatmentRoute

    /** «تاییدیه‌های پزشکی» — medical confirmations and council decisions. */
    @Serializable
    data object MedicalConfirmations : TreatmentRoute

    /** One record: prescribed items, cost breakdown and the PDF exports. */
    @Serializable
    data class RecordDetail(
        val nationalCode: String,
        val noteHeadId: String,
        val type: String,
        val flagSata: String,
        val docName: String,
        val prescDate: String,
        val trackingCode: String,
    ) : TreatmentRoute
}

/** «نسخه‌های الکترونیک»: the records screen on its medicine tab, for the selected patient. */
fun NavController.navigateToPrescriptions() {
    navigate(TreatmentRoute.MedicalRecords(tab = RecordTab.MEDICINE))
}

/**
 * One prescription's detail, e.g. from an assistant link. The link does not carry the doctor, date
 * or tracking code the records list would pass, so the detail header shows them as unknown.
 */
fun NavController.navigateToPrescriptionDetail(
    patientNationalCode: String,
    noteHeadId: String,
    type: String,
    flagSata: String,
) {
    navigate(
        TreatmentRoute.RecordDetail(
            nationalCode = patientNationalCode,
            noteHeadId = noteHeadId,
            type = type,
            flagSata = flagSata,
            docName = "",
            prescDate = "",
            trackingCode = "",
        )
    )
}

fun NavGraphBuilder.treatmentGraph(
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToHealthProfile: (nationalCode: String) -> Unit,
) {
    navigation<TreatmentRoute.Graph>(startDestination = TreatmentRoute.Main) {
        composableWithFadeTransitions<TreatmentRoute.Main> {
            TreatmentScreen(
                onOpenMedicalRecords = { nationalCode ->
                    navController.navigate(TreatmentRoute.MedicalRecords(nationalCode, RecordTab.Default))
                },
                onOpenPrescriptions = { nationalCode ->
                    navController.navigate(TreatmentRoute.MedicalRecords(nationalCode, RecordTab.MEDICINE))
                },
                onOpenHealthProfile = onNavigateToHealthProfile,
                onOpenMiscClaims = { navController.navigate(TreatmentRoute.TreatmentCosts) },
                onOpenApprovals = { navController.navigate(TreatmentRoute.MedicalConfirmations) },
            )
        }

        composable<TreatmentRoute.TreatmentCosts> {
            TreatmentCostsRoute(onBackClicked = onBack)
        }

        composable<TreatmentRoute.MedicalConfirmations> {
            MedicalConfirmationsRoute(onBackClicked = onBack)
        }

        composableWithFadeTransitions<TreatmentRoute.MedicalRecords> { backStackEntry ->
            val route = backStackEntry.toRoute<TreatmentRoute.MedicalRecords>()
            MedicalRecordsScreen(
                nationalCode = route.nationalCode,
                initialTab = route.tab,
                onBack = onBack,
                onOpenRecord = { record, patientNationalCode ->
                    navController.navigate(
                        TreatmentRoute.RecordDetail(
                            nationalCode = patientNationalCode,
                            noteHeadId = record.noteHeadEprescID,
                            type = record.prescType,
                            flagSata = record.flagSata,
                            docName = record.docName,
                            prescDate = record.prescDate,
                            trackingCode = record.trackingCode,
                        )
                    )
                },
            )
        }

        composableWithFadeTransitions<TreatmentRoute.RecordDetail> { backStackEntry ->
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


package com.tamin.taminhamrah.feature.treatment.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TreatmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Using ImmutableList instead of standard List allows the Kotlin Compose compiler to mark
    // TreatmentUiState as 100% @Immutable, enabling child composables to skip recomposition cleanly.
    val deservedList: ImmutableList<DeservedTreatmentPR> = persistentListOf(),
    /** Under-18 dependants — the only ones with their own insurance card. */
    val dependantList: ImmutableList<DependantUserUnderEighteenPR> = persistentListOf(),
    val mainUserNationalCode: String? = null,

    val selectedNationalCode: String? = null,
    val selectedPatientName: String? = null,

    // Current-year treatment spend. Null means "not loaded yet" rather than zero, so the
    // summary card can say so instead of claiming the person has spent nothing.
    val insuredShareTotal: Long? = null,
    val organizationShareTotal: Long? = null,

    // Whether the health self-declaration is filled in. Null means the status has not
    // been fetched, so the card shows no pill rather than guessing either way.
    val healthProfileCompleted: Boolean? = null,

    // The menu's answer for each flag the hub reads. Null means the menu has not answered yet, so
    // the gated entries shimmer instead of guessing either way.
    val featureStatuses: Map<FeatureFlag, FeatureStatus>? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()

        data class DeservedLoaded(val list: ImmutableList<DeservedTreatmentPR>) : PartialState()
        data class DependantsLoaded(val list: ImmutableList<DependantUserUnderEighteenPR>) : PartialState()
        data class MainUserNationalCodeLoaded(val nationalCode: String) : PartialState()
        data class PatientSelected(val nationalCode: String, val fullName: String) : PartialState()
        data class HealthProfileStatusLoaded(val isCompleted: Boolean) : PartialState()
        data class FeatureStatusesLoaded(val statuses: Map<FeatureFlag, FeatureStatus>) : PartialState()
    }
}

sealed class TreatmentIntent {
    data object InitTreatmentFlow : TreatmentIntent()
    data class SelectPatient(val nationalCode: String, val fullName: String) : TreatmentIntent()

    /**
     * A hub entry was tapped. Opening is not immediate: the feature's flag decides whether it
     * navigates, or explains why it cannot, the same way the home services do.
     */
    data class OpenRecords(val tab: RecordTab) : TreatmentIntent()

    /** «هزینه‌های متفرقه» was tapped; gated by its flag like [OpenRecords]. */
    data object OpenMiscClaims : TreatmentIntent()

    /** «تاییدیه‌ها» was tapped; gated by its flag like [OpenRecords]. */
    data object OpenApprovals : TreatmentIntent()
}

sealed class TreatmentEvent {
    data class ShowMessage(val message: String, val type: TreatmentMessageType) : TreatmentEvent()

    /** The flag allowed it, so the records screen may open for this patient and category. */
    data class NavigateToRecords(val nationalCode: String, val tab: RecordTab) : TreatmentEvent()

    data object NavigateToMiscClaims : TreatmentEvent()

    data object NavigateToApprovals : TreatmentEvent()
}

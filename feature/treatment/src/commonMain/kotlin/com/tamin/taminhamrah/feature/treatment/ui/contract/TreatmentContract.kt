package com.tamin.taminhamrah.feature.treatment.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType

@Immutable
data class TreatmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val deservedList: List<DeservedTreatmentPR> = emptyList(),
    /** Under-18 dependants — the only ones with their own insurance card. */
    val dependantList: List<DependantUserUnderEighteenPR> = emptyList(),

    /**
     * Spouse and children from the subdominant endpoint: not cardholders, but their records can
     * be viewed, so they appear in the records patient filter.
     */
    val familyDependantList: List<DisabilityDependentPR> = emptyList(),
    val mainUserNationalCode: String? = null,

    val selectedNationalCode: String? = null,
    val selectedPatientName: String? = null,

    // Current-year treatment spend. Null means "not loaded yet" rather than zero, so the
    // summary card can say so instead of claiming the person has spent nothing.
    val insuredShareTotal: Long? = null,
    val organizationShareTotal: Long? = null,

    // Whether the health self-declaration is filled in. Null means the status has not
    // been fetched, so the card shows no pill rather than guessing either way.
    val healthProfileCompleted: Boolean? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()

        data class DeservedLoaded(val list: List<DeservedTreatmentPR>) : PartialState()
        data class DependantsLoaded(val list: List<DependantUserUnderEighteenPR>) : PartialState()
        data class FamilyDependantsLoaded(val list: List<DisabilityDependentPR>) : PartialState()
        data class MainUserNationalCodeLoaded(val nationalCode: String) : PartialState()
        data class PatientSelected(val nationalCode: String, val fullName: String) : PartialState()
        data class HealthProfileStatusLoaded(val isCompleted: Boolean) : PartialState()
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
}

sealed class TreatmentEvent {
    data class ShowMessage(val message: String, val type: TreatmentMessageType) : TreatmentEvent()

    /** The flag allowed it, so the records screen may open for this patient and category. */
    data class NavigateToRecords(val nationalCode: String, val tab: RecordTab) : TreatmentEvent()
}

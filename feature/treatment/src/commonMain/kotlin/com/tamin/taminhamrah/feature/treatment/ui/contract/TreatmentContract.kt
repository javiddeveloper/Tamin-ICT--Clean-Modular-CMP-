package com.tamin.taminhamrah.feature.treatment.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType

@Immutable
data class TreatmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val deservedList: List<DeservedTreatmentPR> = emptyList(),
    val dependantList: List<DependantUserUnderEighteenPR> = emptyList(),

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

        data class MainUserNationalCodeLoaded(val nationalCode: String) : PartialState()
        data class PatientSelected(val nationalCode: String, val fullName: String) : PartialState()
    }
}

sealed class TreatmentIntent {
    data object InitTreatmentFlow : TreatmentIntent()
    data class SelectPatient(val nationalCode: String, val fullName: String) : TreatmentIntent()
}

sealed class TreatmentEvent {
    data class ShowMessage(val message: String, val type: TreatmentMessageType) : TreatmentEvent()
}

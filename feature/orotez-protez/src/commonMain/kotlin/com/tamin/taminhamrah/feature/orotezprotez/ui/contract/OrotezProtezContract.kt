package com.tamin.taminhamrah.feature.orotezprotez.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.orotezprotez.ui.OrotezProtezStep
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/** One selectable row inside the branch/insured-person picker sheets. */
@Immutable
data class OrotezProtezOptionUi(
    val id: String,
    val label: String,
    /** The insured person's insurance number line. Null for branch options, which have none. */
    val subtitle: String? = null,
)

/** Which picker sheet is open, if any — only one can be at a time. */
enum class OrotezProtezPicker { NONE, BRANCH, INSURED_PERSON, DATE }

/** The read-only registry record shown on step 2, keyed off the step-1 [OrotezProtezOptionUi.id]. */
@Immutable
data class OrotezProtezInsuredDetailUi(
    val fullName: String,
    val relation: String,
    val nationalCode: String,
    val birthCertificateNumber: String,
    val issuePlace: String,
    val birthDateLabel: String,
    val bookletValidUntilLabel: String,
)

@Immutable
data class OrotezProtezUiState(
    val currentStep: OrotezProtezStep = OrotezProtezStep.UserSelection,
    val isLoading: Boolean = false,
    val error: String? = null,
    val branch: OrotezProtezOptionUi? = null,
    val insuredPerson: OrotezProtezOptionUi? = null,
    val prescriptionDateLabel: String? = null,
    val branchOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    /** Registry detail per insured-person id — loaded once, looked up by [insuredPerson]. */
    val insuredPersonDetails: ImmutableMap<String, OrotezProtezInsuredDetailUi> = persistentMapOf(),
    val picker: OrotezProtezPicker = OrotezProtezPicker.NONE,
) {
    val canGoNext: Boolean
        get() = branch != null && insuredPerson != null && prescriptionDateLabel != null

    /** Step 2's whole content: derived from the step-1 selection, never stored independently. */
    val selectedInsuredDetail: OrotezProtezInsuredDetailUi?
        get() = insuredPerson?.id?.let { insuredPersonDetails[it] }

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class DataLoaded(
            val branch: OrotezProtezOptionUi?,
            val branchOptions: ImmutableList<OrotezProtezOptionUi>,
            val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi>,
            val insuredPersonDetails: ImmutableMap<String, OrotezProtezInsuredDetailUi>,
        ) : PartialState
        data class PickerChanged(val picker: OrotezProtezPicker) : PartialState
        data class BranchSelected(val branch: OrotezProtezOptionUi) : PartialState
        data class InsuredPersonSelected(val insuredPerson: OrotezProtezOptionUi) : PartialState
        data class PrescriptionDateSelected(val label: String) : PartialState
        data class StepChanged(val step: OrotezProtezStep) : PartialState
    }
}

sealed interface OrotezProtezIntent {
    data object LoadInitialData : OrotezProtezIntent
    data class OnPickerRequested(val picker: OrotezProtezPicker) : OrotezProtezIntent
    data object OnPickerDismissed : OrotezProtezIntent
    data class OnBranchPicked(val option: OrotezProtezOptionUi) : OrotezProtezIntent
    data class OnInsuredPersonPicked(val option: OrotezProtezOptionUi) : OrotezProtezIntent
    data class OnPrescriptionDatePicked(val label: String) : OrotezProtezIntent
    data object OnNextStepClicked : OrotezProtezIntent
    data object OnConfirmInsuredInfoClicked : OrotezProtezIntent
    /** Goes to the previous step, or exits the feature (via [OrotezProtezEvent.NavigateBack]) from step 1. */
    data object BackToPreviousStep : OrotezProtezIntent
}

sealed interface OrotezProtezEvent {
    data object NavigateBack : OrotezProtezEvent
}

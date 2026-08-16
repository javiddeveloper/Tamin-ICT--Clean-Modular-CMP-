package com.tamin.taminhamrah.feature.orotezprotez.ui.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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

@Immutable
data class OrotezProtezUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val branch: OrotezProtezOptionUi? = null,
    val insuredPerson: OrotezProtezOptionUi? = null,
    val prescriptionDateLabel: String? = null,
    val branchOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    val picker: OrotezProtezPicker = OrotezProtezPicker.NONE,
) {
    val canGoNext: Boolean
        get() = branch != null && insuredPerson != null && prescriptionDateLabel != null

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class DataLoaded(
            val branch: OrotezProtezOptionUi?,
            val branchOptions: ImmutableList<OrotezProtezOptionUi>,
            val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi>,
        ) : PartialState
        data class PickerChanged(val picker: OrotezProtezPicker) : PartialState
        data class BranchSelected(val branch: OrotezProtezOptionUi) : PartialState
        data class InsuredPersonSelected(val insuredPerson: OrotezProtezOptionUi) : PartialState
        data class PrescriptionDateSelected(val label: String) : PartialState
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
}

sealed interface OrotezProtezEvent

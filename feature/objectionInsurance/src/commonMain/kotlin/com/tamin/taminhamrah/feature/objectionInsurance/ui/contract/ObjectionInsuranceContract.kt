package com.tamin.taminhamrah.feature.objectionInsurance.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildDetailDelta
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildMonthBars
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildSeasonGroups
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildStagedYearChips
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildWorkshopPickerRows
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildYearCards
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/** Why a year's ring is the color it is — resolved to an actual color only in the Composable. */
enum class YearCompletionStatus { Edited, Complete, Deficit }

/** The subtitle under a year's day count — kept unformatted so the Composable owns string lookup. */
sealed interface ObjectionYearSubtitle {
    data object Edited : ObjectionYearSubtitle
    data class WorkshopCount(val count: Int) : ObjectionYearSubtitle
    data class TotalDays(val days: Int) : ObjectionYearSubtitle
}

@Immutable
data class ObjectionYearCardPR(
    val year: String,
    val subtitle: ObjectionYearSubtitle,
    val days: String,
    /** Ring sweep, already clamped to 0.02f..1f — see [buildYearCards]. */
    val fraction: Float,
    val status: YearCompletionStatus,
    /** Indices into [ObjectionInsuranceUiState.records]; more than one means multiple workshops. */
    val recordIndices: ImmutableList<Int>,
)

/** One chip in the «سال‌های ویرایش‌شده» row — a year with at least one staged edit. */
@Immutable
data class StagedYearChipPR(
    val label: String,
    val recordIndices: ImmutableList<Int>,
)

/** One month's two-tone bar: registered days (green/orange) plus any user-added days (blue) on top. */
@Immutable
data class ObjectionMonthBarPR(
    val index: Int,
    val label: String,
    /** Of that month's real length — registered portion, drawn green when full, orange otherwise. */
    val recFraction: Float,
    val isFull: Boolean,
    /** Stacked above [recFraction]: what the user is declaring beyond what's registered. */
    val extraFraction: Float,
)

/** One editable row in the detail screen's month table — always visible, not one-at-a-time. */
@Immutable
data class ObjectionMonthRowPR(
    val index: Int,
    val label: String,
    val registeredDays: Int,
    val isRegistered: Boolean,
    val maxDays: Int,
    /** Currently typed value for this month, ASCII digits, empty if untouched. */
    val value: String,
)

/** The four Jalali seasons the month table groups its rows under. */
@Immutable
data class ObjectionSeasonGroupPR(
    val seasonLabel: String,
    val rows: ImmutableList<ObjectionMonthRowPR>,
)

/** How this record's declared total compares to what's registered — colors the delta pill. */
sealed interface ObjectionDelta {
    data object None : ObjectionDelta
    data class Added(val days: Int) : ObjectionDelta
    data class Reduced(val days: Int) : ObjectionDelta
}

/** One workshop card in the multi-workshop picker for a year. */
@Immutable
data class ObjectionWorkshopPickerRowPR(
    val recordIndex: Int,
    val workshopName: String,
    val meta: String,
    /** 12 values, one per month, 0f..1f of that month's length — drives the heatmap strip. */
    val monthOpacities: ImmutableList<Float>,
    val totalDays: Int,
    val isEdited: Boolean,
)

@Immutable
data class ObjectionInsuranceUiState(
    val isLoading: Boolean = false,
    val hasActiveRequest: Boolean = false,
    val showActiveRequestDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val records: ImmutableList<ObjectionInsuranceHistoryPR> = persistentListOf(),
    /** recordIndex -> (month 0..11 -> confirmed new value, ASCII digits). */
    val edits: ImmutableMap<Int, ImmutableMap<Int, String>> = persistentMapOf(),
    val description: String = "",
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val trackingNumber: String? = null,
    val showSubmitConfirmationDialog: Boolean = false,
    /** The year whose workshop-picker screen is open, when that year has more than one record. */
    val workshopPickerYear: String? = null,
    /** The record open in the detail screen, if any. */
    val detailRecordIndex: Int? = null,
    /** In-progress typing for the open detail screen, not yet saved with "ثبت سابقه". */
    val detailDraft: ImmutableMap<Int, String> = persistentMapOf(),
    /** Shown inline above the detail screen's footer after a save attempt with no real change. */
    val detailShowValidationError: Boolean = false,
) {
    val yearCards: ImmutableList<ObjectionYearCardPR> get() = buildYearCards(records, edits)

    val stagedYearChips: ImmutableList<StagedYearChipPR> get() = buildStagedYearChips(records, edits)

    val detailRecord: ObjectionInsuranceHistoryPR? get() = detailRecordIndex?.let(records::getOrNull)

    val detailRecordEdits: Map<Int, String> get() = detailRecordIndex?.let { edits[it] }.orEmpty()

    val detailSeasonGroups: ImmutableList<ObjectionSeasonGroupPR>
        get() = detailRecord?.let { buildSeasonGroups(it, detailDraft) } ?: persistentListOf()

    val detailChartBars: ImmutableList<ObjectionMonthBarPR>
        get() = detailRecord?.let { buildMonthBars(it, detailDraft) } ?: persistentListOf()

    val detailDelta: ObjectionDelta
        get() = detailRecord?.let { buildDetailDelta(it, detailDraft) } ?: ObjectionDelta.None

    val workshopPickerRows: ImmutableList<ObjectionWorkshopPickerRowPR>
        get() = workshopPickerYear?.let { buildWorkshopPickerRows(records, edits, it) } ?: persistentListOf()

    val hasStagedEdits: Boolean get() = stagedYearChips.isNotEmpty()

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data object ActiveRequestFound : PartialState
        data object ActiveRequestDialogDismissed : PartialState
        data class RecordsLoaded(val records: ImmutableList<ObjectionInsuranceHistoryPR>) : PartialState
        data class Error(val message: String) : PartialState
        data object ErrorDismissed : PartialState
        data class DescriptionChanged(val description: String) : PartialState
        data object HelpDialogShown : PartialState
        data object HelpDialogDismissed : PartialState
        data class WorkshopPickerShown(val year: String) : PartialState
        data object WorkshopPickerDismissed : PartialState
        data class DetailSheetShown(val recordIndex: Int) : PartialState
        data object DetailSheetDismissed : PartialState
        data class DetailDraftChanged(val month: Int, val value: String) : PartialState
        data object DetailValidationFailed : PartialState
        data class RecordEditsStaged(val recordIndex: Int, val edits: ImmutableMap<Int, String>) : PartialState
        data class RecordEditsCleared(val recordIndex: Int) : PartialState
        data class YearEditsCleared(val recordIndices: ImmutableList<Int>) : PartialState
        data object SubmitConfirmationShown : PartialState
        data object SubmitConfirmationDismissed : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class SubmitSucceeded(val trackingNumber: String) : PartialState
        data object TrackingNumberDismissed : PartialState
    }
}

sealed interface ObjectionInsuranceIntent {
    data object Load : ObjectionInsuranceIntent
    data object OnBackClicked : ObjectionInsuranceIntent
    data object OnHelpClicked : ObjectionInsuranceIntent
    data object OnHelpDismissed : ObjectionInsuranceIntent
    data object OnActiveRequestDialogDismissed : ObjectionInsuranceIntent
    data class OnYearCardClicked(val recordIndices: ImmutableList<Int>) : ObjectionInsuranceIntent
    data object OnWorkshopPickerDismissed : ObjectionInsuranceIntent
    data class OnWorkshopPicked(val recordIndex: Int) : ObjectionInsuranceIntent
    data object OnDetailSheetDismissed : ObjectionInsuranceIntent
    data class OnMonthValueChanged(val month: Int, val value: String) : ObjectionInsuranceIntent
    data object OnDetailResetClicked : ObjectionInsuranceIntent
    data object OnDetailConfirmClicked : ObjectionInsuranceIntent
    data class OnStagedChipRemoveClicked(val recordIndices: ImmutableList<Int>) : ObjectionInsuranceIntent
    data class OnDescriptionChanged(val description: String) : ObjectionInsuranceIntent
    data object OnSubmitClicked : ObjectionInsuranceIntent
    data object OnSubmitConfirmationDismissed : ObjectionInsuranceIntent
    data object OnSubmitConfirmed : ObjectionInsuranceIntent
    data object OnTrackingNumberAcknowledged : ObjectionInsuranceIntent
    data object OnErrorDismissed : ObjectionInsuranceIntent
}

sealed interface ObjectionInsuranceEvent {
    data object NavigateBack : ObjectionInsuranceEvent
}

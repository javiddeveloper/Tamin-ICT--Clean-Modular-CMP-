package com.tamin.taminhamrah.feature.objectionInsurance.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceEvent
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceIntent
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceUiState
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceUiState.PartialState
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildEditedRecords
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.hasRealEdit
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.sanitizeDayInput
import com.tamin.taminhamrah.mapper.objectionInsurance.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.objectionInsurance.CheckObjectionInsuranceStatusConflictUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.ConfirmObjectionInsuranceConflictUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.FinalConfirmObjectionInsuranceConflictUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.GetObjectionInsuranceHistoriesUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.SaveObjectionInsuranceConflictUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_insurance_blank_tracking_error
import taminx.core.core_ui.objection_insurance_no_staged_edits_error
import taminx.core.core_ui.objection_insurance_submit_rejected_error

class ObjectionInsuranceViewModel(
    private val checkStatusConflictUseCase: CheckObjectionInsuranceStatusConflictUseCase,
    private val getObjectionInsuranceHistoriesUseCase: GetObjectionInsuranceHistoriesUseCase,
    private val saveObjectionInsuranceConflictUseCase: SaveObjectionInsuranceConflictUseCase,
    private val confirmObjectionInsuranceConflictUseCase: ConfirmObjectionInsuranceConflictUseCase,
    private val finalConfirmObjectionInsuranceConflictUseCase: FinalConfirmObjectionInsuranceConflictUseCase,
) : BaseViewModel<ObjectionInsuranceUiState, PartialState, ObjectionInsuranceEvent, ObjectionInsuranceIntent>(
    initialState = ObjectionInsuranceUiState(),
) {

    override fun handleIntent(intent: ObjectionInsuranceIntent): Flow<PartialState> = when (intent) {
        ObjectionInsuranceIntent.Load -> loadData()

        ObjectionInsuranceIntent.OnBackClicked -> {
            sendEvent(ObjectionInsuranceEvent.NavigateBack)
            emptyFlow()
        }

        ObjectionInsuranceIntent.OnHelpClicked -> flow { emit(PartialState.HelpDialogShown) }
        ObjectionInsuranceIntent.OnHelpDismissed -> flow { emit(PartialState.HelpDialogDismissed) }

        ObjectionInsuranceIntent.OnActiveRequestDialogDismissed -> flow {
            emit(PartialState.ActiveRequestDialogDismissed)
            sendEvent(ObjectionInsuranceEvent.NavigateBack)
        }

        is ObjectionInsuranceIntent.OnYearCardClicked -> flow {
            val recordIndices = intent.recordIndices
            if (recordIndices.size == 1) {
                emit(PartialState.DetailSheetShown(recordIndices.first()))
            } else {
                val year = uiState.value.records.getOrNull(recordIndices.firstOrNull() ?: -1)?.year
                if (year != null) emit(PartialState.WorkshopPickerShown(year))
            }
        }

        ObjectionInsuranceIntent.OnWorkshopPickerDismissed -> flow {
            emit(PartialState.WorkshopPickerDismissed)
        }

        is ObjectionInsuranceIntent.OnWorkshopPicked -> flow {
            emit(PartialState.WorkshopPickerDismissed)
            emit(PartialState.DetailSheetShown(intent.recordIndex))
        }

        ObjectionInsuranceIntent.OnDetailSheetDismissed -> flow { emit(PartialState.DetailSheetDismissed) }

        is ObjectionInsuranceIntent.OnMonthValueChanged -> flow {
            val record = uiState.value.detailRecord ?: return@flow
            val maxDays = PersianDateFormatter.daysInMonth(record.year.toIntOrNull() ?: 0, intent.month + 1)
            emit(PartialState.DetailDraftChanged(intent.month, sanitizeDayInput(intent.value, maxDays)))
        }

        ObjectionInsuranceIntent.OnDetailResetClicked -> flow {
            val recordIndex = uiState.value.detailRecordIndex ?: return@flow
            emit(PartialState.RecordEditsCleared(recordIndex))
        }

        ObjectionInsuranceIntent.OnDetailConfirmClicked -> flow {
            val recordIndex = uiState.value.detailRecordIndex ?: return@flow
            val draft = uiState.value.detailDraft
            if (draft.hasRealEdit()) {
                emit(PartialState.RecordEditsStaged(recordIndex, draft))
                emit(PartialState.DetailSheetDismissed)
            } else {
                emit(PartialState.DetailValidationFailed)
            }
        }

        is ObjectionInsuranceIntent.OnStagedChipRemoveClicked -> flow {
            emit(PartialState.YearEditsCleared(intent.recordIndices))
        }

        is ObjectionInsuranceIntent.OnDescriptionChanged -> flow {
            emit(PartialState.DescriptionChanged(intent.description))
        }

        ObjectionInsuranceIntent.OnSubmitClicked -> flow {
            if (uiState.value.hasStagedEdits) {
                emit(PartialState.SubmitConfirmationShown)
            } else {
                emit(PartialState.Error(getString(Res.string.objection_insurance_no_staged_edits_error)))
            }
        }

        ObjectionInsuranceIntent.OnSubmitConfirmationDismissed -> flow {
            emit(PartialState.SubmitConfirmationDismissed)
        }

        ObjectionInsuranceIntent.OnSubmitConfirmed -> handleSubmitConfirmed()

        ObjectionInsuranceIntent.OnTrackingNumberAcknowledged -> flow {
            emit(PartialState.TrackingNumberDismissed)
            sendEvent(ObjectionInsuranceEvent.NavigateBack)
        }

        ObjectionInsuranceIntent.OnErrorDismissed -> flow { emit(PartialState.ErrorDismissed) }
    }

    private fun loadData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val hasActiveRequest = checkStatusConflictUseCase().first()
            if (hasActiveRequest) {
                emit(PartialState.ActiveRequestFound)
            } else {
                val records = getObjectionInsuranceHistoriesUseCase().first().toPresentation()
                emit(PartialState.RecordsLoaded(records.toPersistentList()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    private fun handleSubmitConfirmed(): Flow<PartialState> = flow {
        if (uiState.value.isSubmitting) return@flow
        emit(PartialState.SubmitConfirmationDismissed)
        emit(PartialState.Submitting(true))
        try {
            val editedRecords = buildEditedRecords(uiState.value.records, uiState.value.edits)
            saveObjectionInsuranceConflictUseCase(editedRecords).first()
            val description = uiState.value.description.takeIf { it.isNotBlank() }
            val confirmed = confirmObjectionInsuranceConflictUseCase(description).first()
            if (confirmed) {
                val trackingNumber = finalConfirmObjectionInsuranceConflictUseCase().first()
                // Legacy ObjectionInsuranceHistoryFragment treats blank/null tracking as an error,
                // not a success dialog with an empty number.
                if (trackingNumber.isBlank()) {
                    emit(PartialState.Error(getString(Res.string.objection_insurance_blank_tracking_error)))
                } else {
                    emit(PartialState.SubmitSucceeded(trackingNumber))
                }
            } else {
                emit(PartialState.Error(getString(Res.string.objection_insurance_submit_rejected_error)))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Submitting(false))
        }
    }

    override fun reduceState(
        currentState: ObjectionInsuranceUiState,
        partialState: PartialState,
    ): ObjectionInsuranceUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        PartialState.ActiveRequestFound -> currentState.copy(
            hasActiveRequest = true,
            showActiveRequestDialog = true,
        )
        PartialState.ActiveRequestDialogDismissed -> currentState.copy(showActiveRequestDialog = false)
        is PartialState.RecordsLoaded -> currentState.copy(records = partialState.records, error = null)
        is PartialState.Error -> currentState.copy(error = partialState.message)
        PartialState.ErrorDismissed -> currentState.copy(error = null)
        is PartialState.DescriptionChanged -> currentState.copy(description = partialState.description)
        PartialState.HelpDialogShown -> currentState.copy(showHelpDialog = true)
        PartialState.HelpDialogDismissed -> currentState.copy(showHelpDialog = false)
        is PartialState.WorkshopPickerShown -> currentState.copy(workshopPickerYear = partialState.year)
        PartialState.WorkshopPickerDismissed -> currentState.copy(workshopPickerYear = null)
        is PartialState.DetailSheetShown -> currentState.copy(
            detailRecordIndex = partialState.recordIndex,
            detailDraft = currentState.edits[partialState.recordIndex] ?: persistentMapOf(),
            detailShowValidationError = false,
        )
        PartialState.DetailSheetDismissed -> currentState.copy(
            detailRecordIndex = null,
            detailDraft = persistentMapOf(),
            detailShowValidationError = false,
        )
        is PartialState.DetailDraftChanged -> currentState.copy(
            detailDraft = currentState.detailDraft
                .toMutableMap()
                .apply { put(partialState.month, partialState.value) }
                .toPersistentMap(),
            detailShowValidationError = false,
        )
        PartialState.DetailValidationFailed -> currentState.copy(detailShowValidationError = true)
        is PartialState.RecordEditsStaged -> currentState.copy(
            edits = currentState.edits
                .toMutableMap()
                .apply { put(partialState.recordIndex, partialState.edits) }
                .toPersistentMap(),
        )
        is PartialState.RecordEditsCleared -> currentState.copy(
            edits = currentState.edits
                .toMutableMap()
                .apply { remove(partialState.recordIndex) }
                .toPersistentMap(),
            detailDraft = persistentMapOf(),
            detailShowValidationError = false,
        )
        is PartialState.YearEditsCleared -> currentState.copy(
            edits = currentState.edits
                .toMutableMap()
                .apply { partialState.recordIndices.forEach { index -> remove(index) } }
                .toPersistentMap(),
        )
        PartialState.SubmitConfirmationShown -> currentState.copy(showSubmitConfirmationDialog = true)
        PartialState.SubmitConfirmationDismissed -> currentState.copy(showSubmitConfirmationDialog = false)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.SubmitSucceeded -> currentState.copy(trackingNumber = partialState.trackingNumber)
        PartialState.TrackingNumberDismissed -> currentState.copy(trackingNumber = null)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

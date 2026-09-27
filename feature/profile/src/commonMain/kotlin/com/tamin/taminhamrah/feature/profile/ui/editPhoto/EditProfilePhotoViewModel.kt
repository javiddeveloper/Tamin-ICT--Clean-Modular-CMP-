package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoUiState
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.mapper.subdominant.toPresentation
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import taminx.core.core_ui.Res
import taminx.core.core_ui.profile_photo_error_empty_serial
import taminx.core.core_ui.profile_photo_error_select_dependant

class EditProfilePhotoViewModel(
    private val taminRelationUseCase: TaminRelationUseCase,
    private val subdominantUseCase: SubdominantUseCase,
    private val sendImageRequestUseCase: SendImageRequestUseCase,
) : BaseViewModel<EditProfilePhotoUiState, PartialState, EditProfilePhotoEvent, EditProfilePhotoIntent>(
    initialState = EditProfilePhotoUiState()
) {

    init {
        // Two intents, so a failed relation lookup (shown as the error dialog) cannot cancel the
        // dependants load.
        sendIntent(EditProfilePhotoIntent.LoadRelation)
        sendIntent(EditProfilePhotoIntent.LoadDependants)
    }

    override fun handleIntent(intent: EditProfilePhotoIntent): Flow<PartialState> = when (intent) {
        EditProfilePhotoIntent.LoadRelation -> loadRelation()
        EditProfilePhotoIntent.LoadDependants -> loadDependants()
        is EditProfilePhotoIntent.SerialNumberChanged -> flowOf(PartialState.SerialNumberChanged(intent.serial))
        is EditProfilePhotoIntent.DependantModeToggled -> flowOf(PartialState.DependantModeToggled(intent.enabled))
        is EditProfilePhotoIntent.DependantSelected -> flowOf(PartialState.DependantSelected(intent.dependant))
        is EditProfilePhotoIntent.DependantPickerVisibilityChanged ->
            flowOf(PartialState.DependantPickerVisibilityChanged(intent.isOpen))
        EditProfilePhotoIntent.Submit -> submit()
        EditProfilePhotoIntent.OpenGuide -> flowOf(PartialState.DialogShown(PhotoDialogState.Guide))
        EditProfilePhotoIntent.DismissDialog -> dismissDialog()
        EditProfilePhotoIntent.OnBackClicked -> {
            sendEvent(EditProfilePhotoEvent.NavigateBack)
            emptyFlow()
        }
    }

    /** The old app fills the identity card and takes the branch code from this one call. */
    private fun loadRelation(): Flow<PartialState> = flow {
        emitAll(
            taminRelationUseCase().map { relation ->
                PartialState.RelationLoaded(
                    userName = relation.fullName,
                    nationalCode = relation.nationalId.orEmpty(),
                    insuranceNumber = relation.insuranceId.orEmpty(),
                    branchCode = relation.brhCode.orEmpty(),
                )
            }
        )
    }

    private fun loadDependants(): Flow<PartialState> = flow {
        emitAll(
            subdominantUseCase().map { PartialState.DependantsLoaded(it.toPresentation().list.toImmutableList()) }
        )
    }.catch { emit(PartialState.DependantsLoaded(persistentListOf())) }

    // flatMapMerge runs intents concurrently, so a second Submit can still read a stale uiState;
    // this plain field is set before the first suspension point, so the second call sees it.
    private var isSubmitting = false

    private fun submit(): Flow<PartialState> = flow {
        if (isSubmitting) return@flow
        isSubmitting = true
        try {
            val state = uiState.value
            val dependant = state.selectedDependant.takeIf { state.isDependantMode }
            // Old-app rule: the serial is required in both modes, even though a dependant's request
            // sends their national code in its place.
            val serialError = state.serialNumber.isBlank()
            val dependantError = state.isDependantMode && dependant == null
            if (serialError || dependantError) {
                emit(PartialState.ValidationFailed(serialError, dependantError))
                return@flow
            }

            emit(PartialState.Submitting)
            // Path segment and filter value both fall back to "0", never "", as the old app does.
            val serialId = dependant?.nationalCode?.ifBlank { "0" } ?: state.serialNumber
            emitAll(
                sendImageRequestUseCase(branchCode = state.branchCode.ifBlank { "0" }, serialId = serialId)
                    .map { PartialState.DialogShown(PhotoDialogState.Success) }
            )
        } finally {
            isSubmitting = false
        }
    }

    private fun dismissDialog(): Flow<PartialState> {
        // A queued request returns to the profile; any other dialog keeps the form open.
        if (uiState.value.dialogState == PhotoDialogState.Success) {
            sendEvent(EditProfilePhotoEvent.NavigateBack)
        }
        return flowOf(PartialState.DialogDismissed)
    }

    override fun reduceState(
        currentState: EditProfilePhotoUiState,
        partialState: PartialState,
    ): EditProfilePhotoUiState = when (partialState) {
        is PartialState.RelationLoaded -> currentState.copy(
            userName = partialState.userName,
            nationalCode = partialState.nationalCode,
            insuranceNumber = partialState.insuranceNumber,
            branchCode = partialState.branchCode,
        )
        is PartialState.DependantsLoaded -> currentState.copy(
            dependants = partialState.dependants,
            isDependantsLoading = false,
        )
        is PartialState.SerialNumberChanged -> currentState.copy(
            serialNumber = partialState.serial,
            isSerialError = false,
        )
        is PartialState.DependantModeToggled -> currentState.copy(
            isDependantMode = partialState.enabled,
            selectedDependant = currentState.selectedDependant.takeIf { partialState.enabled },
            isDependantError = false,
        )
        is PartialState.DependantSelected -> currentState.copy(
            selectedDependant = partialState.dependant,
            isDependantPickerOpen = false,
            isDependantError = false,
        )
        is PartialState.DependantPickerVisibilityChanged -> currentState.copy(
            isDependantPickerOpen = partialState.isOpen,
        )
        is PartialState.ValidationFailed -> currentState.copy(
            isSerialError = partialState.serialError,
            isDependantError = partialState.dependantError,
            // The serial is named first, as in the old app.
            dialogState = PhotoDialogState.ValidationError(
                if (partialState.serialError) {
                    Res.string.profile_photo_error_empty_serial
                } else {
                    Res.string.profile_photo_error_select_dependant
                }
            ),
        )
        PartialState.Submitting -> currentState.copy(isSubmitting = true)
        // Every dialog ends a submission — the success, or the failure createErrorState routes here.
        is PartialState.DialogShown -> currentState.copy(
            dialogState = partialState.dialogState,
            isSubmitting = false,
        )
        PartialState.DialogDismissed -> currentState.copy(dialogState = null)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.DialogShown(PhotoDialogState.ServerError(message))
}

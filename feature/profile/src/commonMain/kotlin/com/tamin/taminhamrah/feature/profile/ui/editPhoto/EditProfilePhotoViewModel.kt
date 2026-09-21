package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoPartialState
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoUiState
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.mapper.subdominant.toPresentation
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.alphanumericOnly
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import taminx.core.core_ui.Res
import taminx.core.core_ui.profile_photo_error_empty_serial
import taminx.core.core_ui.profile_photo_error_select_dependant

class EditProfilePhotoViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val taminRelationUseCase: TaminRelationUseCase,
    private val subdominantUseCase: SubdominantUseCase,
    private val sendImageRequestUseCase: SendImageRequestUseCase,
) : BaseViewModel<EditProfilePhotoUiState, EditProfilePhotoPartialState, EditProfilePhotoEvent, EditProfilePhotoIntent>(
    initialState = EditProfilePhotoUiState()
) {

    init {
        sendIntent(EditProfilePhotoIntent.LoadInitialData)
    }

    override fun handleIntent(intent: EditProfilePhotoIntent): Flow<EditProfilePhotoPartialState> {
        return when (intent) {
            is EditProfilePhotoIntent.LoadInitialData -> handleLoadInitialData()
            is EditProfilePhotoIntent.SerialNumberChanged -> flow {
                val cleaned = intent.serial.alphanumericOnly().take(10)
                emit(EditProfilePhotoPartialState.SerialNumberChanged(cleaned))
            }
            is EditProfilePhotoIntent.DependantModeToggled -> flow {
                emit(EditProfilePhotoPartialState.DependantModeToggled(intent.enabled))
            }
            is EditProfilePhotoIntent.DependantSelected -> flow {
                emit(EditProfilePhotoPartialState.DependantSelected(intent.dependant))
            }
            is EditProfilePhotoIntent.DependantPickerVisibilityChanged -> flow {
                emit(EditProfilePhotoPartialState.DependantPickerVisibilityChanged(intent.isOpen))
            }
            is EditProfilePhotoIntent.Submit -> handleSubmit()
            is EditProfilePhotoIntent.OpenGuide -> flow {
                emit(EditProfilePhotoPartialState.ShowDialog(PhotoDialogState.Guide))
            }
            is EditProfilePhotoIntent.DismissDialog -> handleDismissDialog()
            is EditProfilePhotoIntent.OnBackClicked -> flow {
                sendEvent(EditProfilePhotoEvent.NavigateBack)
            }
        }
    }

    private fun handleLoadInitialData(): Flow<EditProfilePhotoPartialState> = flow {
        emit(EditProfilePhotoPartialState.Loading(true))
        emit(EditProfilePhotoPartialState.DependantsLoading(true))

        combine(
            identityInfoUseCase().catch {
                emit(
                    com.tamin.taminhamrah.model.identity.IdentityInfoDN(
                        cityOfBirthId = null,
                        cityOfIssueId = null,
                        countryId = null,
                        dateOfBirth = null,
                        fatherName = null,
                        firstName = "",
                        gender = null,
                        id = null,
                        idCardNumber = null,
                        idCardSerial1 = null,
                        idCardSerial2 = null,
                        lastName = "",
                        nationalId = "",
                        ssn = null
                    )
                )
            },
            taminRelationUseCase().catch { emit(com.tamin.taminhamrah.model.user.TaminRelationDN()) }
        ) { identity: com.tamin.taminhamrah.model.identity.IdentityInfoDN, relation: com.tamin.taminhamrah.model.user.TaminRelationDN ->
            val fullName = listOfNotNull(
                identity.firstName?.takeIf { it.isNotBlank() } ?: relation.firstName,
                identity.lastName?.takeIf { it.isNotBlank() } ?: relation.lastName
            ).joinToString(" ").trim()

            val nationalCode = identity.nationalId?.takeIf { it.isNotBlank() }
                ?: relation.nationalId.orEmpty()
            val insuranceNumber = relation.insuranceId.orEmpty()
            val branchCode = relation.brhCode.orEmpty()

            EditProfilePhotoPartialState.InitialDataLoaded(
                userName = fullName,
                nationalCode = nationalCode,
                insuranceNumber = insuranceNumber,
                branchCode = branchCode
            )
        }.collect { emit(it) }

        subdominantUseCase()
            .map { subdominantDN ->
                val items = subdominantDN.toPresentation().list.map { item ->
                    val resolvedRelation = resolveRelationDescription(item)
                    item.copy(relationDescription = resolvedRelation)
                }.toImmutableList()
                EditProfilePhotoPartialState.DependantsLoaded(items)
            }
            .catch {
                emit(EditProfilePhotoPartialState.DependantsLoaded(kotlinx.collections.immutable.persistentListOf()))
            }
            .collect { emit(it) }
    }

    private fun resolveRelationDescription(item: SubdominantItemPR): String {
        if (item.relationDescription.isNotBlank()) return item.relationDescription
        return ""
    }

    private fun handleSubmit(): Flow<EditProfilePhotoPartialState> = flow {
        val serial = uiState.value.serialNumber.trim()
        val isDependant = uiState.value.isDependantMode
        val selected = uiState.value.selectedDependant

        if (serial.isBlank()) {
            val dependantError = isDependant && selected == null
            emit(EditProfilePhotoPartialState.ValidationErrors(serialError = true, dependantError = dependantError))
            emit(EditProfilePhotoPartialState.ShowDialog(PhotoDialogState.ValidationError(Res.string.profile_photo_error_empty_serial)))
            return@flow
        }

        if (isDependant && selected == null) {
            emit(EditProfilePhotoPartialState.ValidationErrors(serialError = false, dependantError = true))
            emit(EditProfilePhotoPartialState.ShowDialog(PhotoDialogState.ValidationError(Res.string.profile_photo_error_select_dependant)))
            return@flow
        }

        emit(EditProfilePhotoPartialState.ValidationErrors(serialError = false, dependantError = false))
        emit(EditProfilePhotoPartialState.Submitting(true))

        val branchCode = uiState.value.branchCode.ifBlank { "0" }
        val targetSerial = if (isDependant) selected?.nationalCode ?: "0" else serial

        sendImageRequestUseCase(branchCode = branchCode, serialId = targetSerial)
            .map { serverMessage ->
                EditProfilePhotoPartialState.ShowDialog(PhotoDialogState.Success(serverMessage))
            }
            .catch { e ->
                emit(EditProfilePhotoPartialState.Submitting(false))
                emit(
                    EditProfilePhotoPartialState.ShowDialog(
                        PhotoDialogState.ServerError(e.message ?: "خطا در ارسال درخواست")
                    )
                )
            }
            .collect {
                emit(EditProfilePhotoPartialState.Submitting(false))
                emit(it)
            }
    }

    private fun handleDismissDialog(): Flow<EditProfilePhotoPartialState> = flow {
        if (uiState.value.dialogState is PhotoDialogState.Success) {
            sendEvent(EditProfilePhotoEvent.NavigateBackOnSuccess)
        }
        emit(EditProfilePhotoPartialState.DismissDialog)
    }

    override fun reduceState(
        currentState: EditProfilePhotoUiState,
        partialState: EditProfilePhotoPartialState,
    ): EditProfilePhotoUiState {
        return when (partialState) {
            is EditProfilePhotoPartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
            is EditProfilePhotoPartialState.InitialDataLoaded -> currentState.copy(
                userName = partialState.userName,
                nationalCode = partialState.nationalCode,
                insuranceNumber = partialState.insuranceNumber,
                branchCode = partialState.branchCode,
                isLoading = false
            )
            is EditProfilePhotoPartialState.DependantsLoading -> currentState.copy(
                isDependantsLoading = partialState.isLoading
            )
            is EditProfilePhotoPartialState.DependantsLoaded -> currentState.copy(
                dependants = partialState.dependants,
                isDependantsLoading = false
            )
            is EditProfilePhotoPartialState.SerialNumberChanged -> currentState.copy(
                serialNumber = partialState.serial,
                isSerialError = false
            )
            is EditProfilePhotoPartialState.DependantModeToggled -> currentState.copy(
                isDependantMode = partialState.enabled,
                isDependantError = false,
                selectedDependant = if (!partialState.enabled) null else currentState.selectedDependant
            )
            is EditProfilePhotoPartialState.DependantSelected -> currentState.copy(
                selectedDependant = partialState.dependant,
                isDependantError = false,
                isDependantPickerOpen = false
            )
            is EditProfilePhotoPartialState.DependantPickerVisibilityChanged -> currentState.copy(
                isDependantPickerOpen = partialState.isOpen
            )
            is EditProfilePhotoPartialState.Submitting -> currentState.copy(
                isSubmitting = partialState.isSubmitting
            )
            is EditProfilePhotoPartialState.ShowDialog -> currentState.copy(
                dialogState = partialState.dialogState
            )
            is EditProfilePhotoPartialState.DismissDialog -> currentState.copy(
                dialogState = null
            )
            is EditProfilePhotoPartialState.ValidationErrors -> currentState.copy(
                isSerialError = partialState.serialError,
                isDependantError = partialState.dependantError
            )
        }
    }

    override fun createErrorState(message: String): EditProfilePhotoPartialState {
        return EditProfilePhotoPartialState.ShowDialog(PhotoDialogState.ServerError(message))
    }
}

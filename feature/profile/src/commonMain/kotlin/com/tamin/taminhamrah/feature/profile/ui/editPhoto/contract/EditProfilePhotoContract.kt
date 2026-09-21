package com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

@Immutable
sealed interface PhotoDialogState {
    data class Success(val message: String? = null) : PhotoDialogState
    data class ValidationError(val message: StringResource) : PhotoDialogState
    data class ServerError(val message: String) : PhotoDialogState
    data object Guide : PhotoDialogState
}

@Immutable
data class EditProfilePhotoUiState(
    val userName: String = "",
    val nationalCode: String = "",
    val insuranceNumber: String = "",
    val branchCode: String = "",
    val serialNumber: String = "",
    val isDependantMode: Boolean = false,
    val selectedDependant: SubdominantItemPR? = null,
    val dependants: ImmutableList<SubdominantItemPR> = persistentListOf(),
    val isDependantsLoading: Boolean = false,
    val isDependantPickerOpen: Boolean = false,
    val isSerialError: Boolean = false,
    val isDependantError: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialogState: PhotoDialogState? = null,
)

sealed interface EditProfilePhotoPartialState {
    data class Loading(val isLoading: Boolean) : EditProfilePhotoPartialState
    data class InitialDataLoaded(
        val userName: String,
        val nationalCode: String,
        val insuranceNumber: String,
        val branchCode: String,
    ) : EditProfilePhotoPartialState
    data class DependantsLoading(val isLoading: Boolean) : EditProfilePhotoPartialState
    data class DependantsLoaded(val dependants: ImmutableList<SubdominantItemPR>) : EditProfilePhotoPartialState
    data class SerialNumberChanged(val serial: String) : EditProfilePhotoPartialState
    data class DependantModeToggled(val enabled: Boolean) : EditProfilePhotoPartialState
    data class DependantSelected(val dependant: SubdominantItemPR) : EditProfilePhotoPartialState
    data class DependantPickerVisibilityChanged(val isOpen: Boolean) : EditProfilePhotoPartialState
    data class Submitting(val isSubmitting: Boolean) : EditProfilePhotoPartialState
    data class ShowDialog(val dialogState: PhotoDialogState) : EditProfilePhotoPartialState
    data object DismissDialog : EditProfilePhotoPartialState
    data class ValidationErrors(val serialError: Boolean, val dependantError: Boolean) : EditProfilePhotoPartialState
}

sealed interface EditProfilePhotoIntent {
    data object LoadInitialData : EditProfilePhotoIntent
    data class SerialNumberChanged(val serial: String) : EditProfilePhotoIntent
    data class DependantModeToggled(val enabled: Boolean) : EditProfilePhotoIntent
    data class DependantSelected(val dependant: SubdominantItemPR) : EditProfilePhotoIntent
    data class DependantPickerVisibilityChanged(val isOpen: Boolean) : EditProfilePhotoIntent
    data object Submit : EditProfilePhotoIntent
    data object OpenGuide : EditProfilePhotoIntent
    data object DismissDialog : EditProfilePhotoIntent
    data object OnBackClicked : EditProfilePhotoIntent
}

sealed interface EditProfilePhotoEvent {
    data class ShowSnackbar(val message: StringResource) : EditProfilePhotoEvent
    data object NavigateBackOnSuccess : EditProfilePhotoEvent
    data object NavigateBack : EditProfilePhotoEvent
}

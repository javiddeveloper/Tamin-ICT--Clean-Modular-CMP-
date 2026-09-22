package com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

@Immutable
sealed interface PhotoDialogState {
    /** Fixed copy: the service answers `data: null`. */
    data object Success : PhotoDialogState
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
    val isDependantsLoading: Boolean = true,
    val isDependantPickerOpen: Boolean = false,
    val isSerialError: Boolean = false,
    val isDependantError: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialogState: PhotoDialogState? = null,
) {
    sealed interface PartialState {
        data class RelationLoaded(
            val userName: String,
            val nationalCode: String,
            val insuranceNumber: String,
            val branchCode: String,
        ) : PartialState
        data class DependantsLoaded(val dependants: ImmutableList<SubdominantItemPR>) : PartialState
        data class SerialNumberChanged(val serial: String) : PartialState
        data class DependantModeToggled(val enabled: Boolean) : PartialState
        data class DependantSelected(val dependant: SubdominantItemPR) : PartialState
        data class DependantPickerVisibilityChanged(val isOpen: Boolean) : PartialState
        data class ValidationFailed(val serialError: Boolean, val dependantError: Boolean) : PartialState
        data object Submitting : PartialState
        data class DialogShown(val dialogState: PhotoDialogState) : PartialState
        data object DialogDismissed : PartialState
    }
}

sealed interface EditProfilePhotoIntent {
    data object LoadRelation : EditProfilePhotoIntent
    data object LoadDependants : EditProfilePhotoIntent
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
    data object NavigateBack : EditProfilePhotoEvent
}

package com.tamin.taminhamrah.ui.home.services.dependentCancellation

import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoModel

object DependentCancellationContract {

    data class DependentCancellationState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val errorMessage: String? = null,
        val nationalCode: String? = null,
        val fullName: String? = null,
        val cancellationReason: List<CancellationReason> = CancellationReason.values().toList(),
        val selectedReason: CancellationReason? = null,
        val selectedDependent: DependentInfoModel? = null,
        val dependents: List<DependentInfoModel> = emptyList(),
        val approvalSelected: Boolean = false,
        val selectedDate : String? = ""
    )

    sealed class DependentCancellationIntent() {
        data class SelectDependent(val dependent: DependentInfoModel) :
            DependentCancellationIntent()

        data class SelectCancellationReason(val reason: CancellationReason) :
            DependentCancellationIntent()

        data object ToggleApproval : DependentCancellationIntent()
        data object Submit : DependentCancellationIntent()
        data object OnBackPress : DependentCancellationIntent()

        data class SelectDate(val date: String) : DependentCancellationIntent()
    }

    sealed class DependentCancellationEvent() {
        data object ShowEmptyDependentsDialog : DependentCancellationEvent()
        data object NavigateBack : DependentCancellationEvent()
        data class ShowSuccess(val message: String) : DependentCancellationEvent()
    }

    enum class CancellationReason(value: String) {
        DEATH(value = "dead"),
        DIVORCE(value = "divorce"),
        JOB_MALE(value = "job_male"),
        JOB_FEMALE(value = "job_female")
    }
}
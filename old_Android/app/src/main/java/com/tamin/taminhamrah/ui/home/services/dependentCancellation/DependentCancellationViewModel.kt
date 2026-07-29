package com.tamin.taminhamrah.ui.home.services.dependentCancellation

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModelMVI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentCancellationViewModel @Inject constructor(
    private val repository: ServiceRepository,
) : BaseViewModelMVI<DependentCancellationContract.DependentCancellationIntent,
        DependentCancellationContract.DependentCancellationState,
        DependentCancellationContract.DependentCancellationEvent>() {

    init {
        getDependents()
    }


    override fun createInitialState(): DependentCancellationContract.DependentCancellationState =
        DependentCancellationContract.DependentCancellationState()

    override suspend fun handleIntent(intent: DependentCancellationContract.DependentCancellationIntent) {
        when (intent) {
            is DependentCancellationContract.DependentCancellationIntent.SelectDependent -> {
                setState {
                    copy(
                        selectedDependent = intent.dependent,
                        nationalCode = intent.dependent.identityInfo.nationalId,
                        fullName = "${intent.dependent.identityInfo.firstName} ${intent.dependent.identityInfo.lastName}"
                    )
                }
            }

            is DependentCancellationContract.DependentCancellationIntent.SelectCancellationReason -> {
                setState { copy(selectedReason = intent.reason) }
            }

            DependentCancellationContract.DependentCancellationIntent.ToggleApproval -> {
                setState { copy(approvalSelected = !approvalSelected) }
            }

            DependentCancellationContract.DependentCancellationIntent.Submit -> {
                submitCancellation()
            }

            DependentCancellationContract.DependentCancellationIntent.OnBackPress -> {
                sendEvent(DependentCancellationContract.DependentCancellationEvent.NavigateBack)
            }
            is DependentCancellationContract.DependentCancellationIntent.SelectDate -> {
                setState { copy(selectedDate = intent.date) }
            }
        }
    }

    private fun getDependents() {
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true
                )
            }
            val response = repository.getDependentInfo()
            if (response.isSuccess && response.data != null) {
                val dependents = response.data?.list?.map { it.dependentInfo } ?: emptyList()
                if (dependents.isEmpty()) {
                    setState { copy(isLoading = false, dependents = emptyList()) }
                    sendEvent(DependentCancellationContract.DependentCancellationEvent.ShowEmptyDependentsDialog)
                } else {
                    val firstDependent = dependents.first()
                    setState {
                        copy(
                            dependents = dependents,
                            selectedDependent = firstDependent,
                            nationalCode = firstDependent.identityInfo.nationalId,
                            fullName = "${firstDependent.identityInfo.firstName} ${firstDependent.identityInfo.lastName}",
                            isLoading = false
                        )
                    }
                }
            } else {
                setState {
                    copy(
                        errorMessage = response.reason,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun submitCancellation() {
        val currentState = state.value
        val dependent = currentState.selectedDependent
        val reason = currentState.selectedReason

        if (dependent == null || reason == null || !currentState.approvalSelected) {
            // Handle validation if needed, or just return
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // Using the discovered deleteRecentlyAddedUser as a way to "cancel" or "delete" a dependent/subdominant
            // Although the name says "RecentlyAddedUser", the @DELETE("subdominants/{personalId}") matches the "subdominant" path used for dependents.


            if (state.value.selectedDependent?.identityInfo?.nationalId != null) {

            } else {
                setState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "خطا در دریافت شناسه فرد"
                    )
                }
            }
        }
    }


}
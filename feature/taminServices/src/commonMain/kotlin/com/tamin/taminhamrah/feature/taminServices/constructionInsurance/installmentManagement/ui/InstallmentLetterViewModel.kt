package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetInstallmentLetterListUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class InstallmentLetterViewModel(
    private val getInstallmentLetterListUseCase: GetInstallmentLetterListUseCase,
) : BaseViewModel<InstallmentLetterUiState, PartialState, InstallmentLetterEvent, InstallmentLetterIntent>(
    initialState = InstallmentLetterUiState()
) {

    private var workshopId: String = ""
    private var branchId: String = ""
    private var hasLoaded = false

    override fun handleIntent(intent: InstallmentLetterIntent): Flow<PartialState> =
        when (intent) {
            is InstallmentLetterIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    workshopId = intent.workshopId
                    branchId = intent.branchId
                    loadInstallmentLetters(seed = intent)
                }
            }

            InstallmentLetterIntent.Retry -> loadInstallmentLetters(seed = null)

            InstallmentLetterIntent.OnBackClicked -> {
                sendEvent(InstallmentLetterEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun loadInstallmentLetters(seed: InstallmentLetterIntent.Load?): Flow<PartialState> = flow {
        seed?.let { emit(PartialState.HeaderSeeded(it.workshopId, it.branchId)) }
        emit(PartialState.Loading(true))
        emitAll(
            getInstallmentLetterListUseCase(workshopId, branchId)
                .map { list -> PartialState.Loaded(list.map { it.toPR() }.toImmutableList()) as PartialState }
                .catch { e ->
                    val message = e.toSingleLineMessage()
                    sendEvent(InstallmentLetterEvent.ShowError(message))
                    emit(PartialState.Error(message))
                }
        )
        emit(PartialState.Loading(false))
    }

    override fun reduceState(
        currentState: InstallmentLetterUiState,
        partialState: PartialState,
    ): InstallmentLetterUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            workshopId = partialState.workshopId,
            branchId = partialState.branchId,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)

        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            error = null,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.DeleteLegalRepresentativeUseCase
import com.tamin.taminhamrah.useCases.workshops.GetLegalRepresentativeWorkshopContractsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetLegalRepresentativesUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LegalRepresentativeListViewModel(
    private val getLegalRepresentativesUseCase: GetLegalRepresentativesUseCase,
    private val getLegalRepresentativeWorkshopContractsUseCase: GetLegalRepresentativeWorkshopContractsUseCase,
    private val deleteLegalRepresentativeUseCase: DeleteLegalRepresentativeUseCase,
) : BaseViewModel<
    LegalRepresentativeListUiState,
    LegalRepresentativeListUiState.PartialState,
    LegalRepresentativeListEvent,
    LegalRepresentativeListIntent
    >(initialState = LegalRepresentativeListUiState()) {

    private var currentWorkshopId: String = ""
    private var currentBranchCode: String = ""
    private var currentTicket: String = ""

    override fun handleIntent(
        intent: LegalRepresentativeListIntent
    ): Flow<LegalRepresentativeListUiState.PartialState> = when (intent) {
        is LegalRepresentativeListIntent.Load -> flow {
            currentWorkshopId = intent.workshopId
            currentBranchCode = intent.branchCode
            currentTicket = intent.ticket
            emit(LegalRepresentativeListUiState.PartialState.Loading(true))
            try {
                emit(LegalRepresentativeListUiState.PartialState.Loaded(loadRepresentatives(intent.workshopId, intent.branchCode)))
            } catch (e: Exception) {
                emit(LegalRepresentativeListUiState.PartialState.Error(e.toSingleLineMessage()))
            }
        }

        is LegalRepresentativeListIntent.ToggleExpand -> flow {
            val current = uiState.value.expandedStakeId
            emit(LegalRepresentativeListUiState.PartialState.ToggleExpand(if (current == intent.stakeId) null else intent.stakeId))
        }

        is LegalRepresentativeListIntent.ToggleMenu -> flow {
            emit(LegalRepresentativeListUiState.PartialState.ToggleMenu(intent.stakeId))
        }

        is LegalRepresentativeListIntent.RequestDelete -> flow {
            emit(LegalRepresentativeListUiState.PartialState.SetDeleteTarget(intent.target))
        }

        is LegalRepresentativeListIntent.CancelDelete -> flow {
            emit(LegalRepresentativeListUiState.PartialState.SetDeleteTarget(null))
        }

        is LegalRepresentativeListIntent.ConfirmDelete -> flow {
            if (uiState.value.isDeleting) return@flow
            val target = uiState.value.deleteTarget ?: return@flow
            emit(LegalRepresentativeListUiState.PartialState.Deleting)
            try {
                deleteLegalRepresentativeUseCase(uiState.value.ticket, target.stakeId)
                emit(LegalRepresentativeListUiState.PartialState.Deleted)
                val updated = uiState.value.representatives
                    .filterNot { it.stakeId == target.stakeId }
                    .toPersistentList()
                emit(LegalRepresentativeListUiState.PartialState.Loaded(updated))
            } catch (e: Exception) {
                emit(LegalRepresentativeListUiState.PartialState.DeleteFailed(e.toSingleLineMessage()))
            }
        }

        is LegalRepresentativeListIntent.EditClicked -> flow {
            sendEvent(LegalRepresentativeListEvent.NavigateToEdit(intent.target))
        }

        is LegalRepresentativeListIntent.AddClicked -> flow {
            sendEvent(LegalRepresentativeListEvent.NavigateToAdd)
        }
    }

    private suspend fun loadRepresentatives(workshopId: String, branchCode: String): ImmutableList<LegalRepresentativePR> {
        val representatives = getLegalRepresentativesUseCase(workshopId, branchCode).first()?.list ?: emptyList()
        if (representatives.none { it.special }) {
            return representatives.map { it.toPresentation() }.toPersistentList()
        }
        val contractRowsByNationalId: Map<String, List<String>> = getLegalRepresentativeWorkshopContractsUseCase(workshopId, branchCode)
            .first()
            ?.list
            .orEmpty()
            .filter { !it.nationalCode.isNullOrBlank() }
            .groupBy({ it.nationalCode!! }, valueTransform = { it.contractRow })
        return representatives.map { it.toPresentation(contractRowsByNationalId[it.nationalId] ?: emptyList()) }.toPersistentList()
    }

    override fun reduceState(
        currentState: LegalRepresentativeListUiState,
        partialState: LegalRepresentativeListUiState.PartialState
    ): LegalRepresentativeListUiState = when (partialState) {
        is LegalRepresentativeListUiState.PartialState.Loading ->
            currentState.copy(
                isLoading = partialState.isLoading,
                error = null,
                workshopId = currentWorkshopId,
                branchCode = currentBranchCode,
                ticket = currentTicket,
            )

        is LegalRepresentativeListUiState.PartialState.Loaded ->
            currentState.copy(isLoading = false, representatives = partialState.representatives)

        is LegalRepresentativeListUiState.PartialState.Error ->
            currentState.copy(isLoading = false, error = partialState.message)

        is LegalRepresentativeListUiState.PartialState.ToggleExpand ->
            currentState.copy(expandedStakeId = partialState.stakeId)

        is LegalRepresentativeListUiState.PartialState.ToggleMenu ->
            currentState.copy(menuOpenStakeId = partialState.stakeId)

        is LegalRepresentativeListUiState.PartialState.SetDeleteTarget ->
            currentState.copy(deleteTarget = partialState.target, menuOpenStakeId = null)

        is LegalRepresentativeListUiState.PartialState.Deleting ->
            currentState.copy(isDeleting = true)

        is LegalRepresentativeListUiState.PartialState.Deleted ->
            currentState.copy(isDeleting = false, deleteTarget = null)

        is LegalRepresentativeListUiState.PartialState.DeleteFailed ->
            currentState.copy(isDeleting = false, error = partialState.message, deleteTarget = null)
    }

    override fun createErrorState(message: String): LegalRepresentativeListUiState.PartialState =
        LegalRepresentativeListUiState.PartialState.Error(message)
}

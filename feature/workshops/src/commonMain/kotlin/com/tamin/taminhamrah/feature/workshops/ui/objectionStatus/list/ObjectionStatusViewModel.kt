package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list.ObjectionStatusUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkShopObjectionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

/** پیگیری وضعیت اعتراض — every objection the employer has filed. */
class ObjectionStatusViewModel(
    private val getWorkShopObjections: GetWorkShopObjectionsUseCase,
    private val getIdentityInfo: GetIdentityInfoUseCase,
) : BaseViewModel<ObjectionStatusUiState, PartialState, ObjectionStatusEvent, ObjectionStatusIntent>(
    initialState = ObjectionStatusUiState()
) {

    init {
        sendIntent(ObjectionStatusIntent.Load)
    }

    override fun handleIntent(intent: ObjectionStatusIntent): Flow<PartialState> = when (intent) {
        ObjectionStatusIntent.Load -> merge(loadIdentity(), loadPage(page = 0))
        ObjectionStatusIntent.LoadMore -> loadMore()
        is ObjectionStatusIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is ObjectionStatusIntent.DraftChanged -> flow { emit(PartialState.DraftChanged(intent.draft)) }
        ObjectionStatusIntent.ApplyFilters -> applyFilters(uiState.value.draft)
        ObjectionStatusIntent.ClearFilters -> applyFilters(ObjectionStatusFilters())
        is ObjectionStatusIntent.RemoveFilter -> applyFilters(
            uiState.value.applied.without(intent.field)
        )
    }

    private fun loadIdentity(): Flow<PartialState> = flow {
        getIdentityInfo().collect { identity ->
            val name = listOfNotNull(identity.firstName, identity.lastName)
                .joinToString(" ")
                .trim()
            emit(PartialState.IdentityLoaded(name = name, nationalId = identity.nationalId.orEmpty()))
        }
    }.catch { /* the identity card degrades to blanks; the list still works without it */ }

    private fun loadPage(
        page: Int,
        filters: ObjectionStatusFilters = uiState.value.applied,
    ): Flow<PartialState> = flow {
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getWorkShopObjections(
            WorkShopObjectionQuery(
                workshopId = filters.workshopId.takeIf { it.isNotBlank() },
                objectionNumber = filters.objectionNumber.takeIf { it.isNotBlank() },
                debitNumber = filters.debitNumber.takeIf { it.isNotBlank() },
                page = page,
            )
        )
        emit(
            PartialState.Loaded(
                list = uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() },
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    private fun applyFilters(filters: ObjectionStatusFilters): Flow<PartialState> = flow {
        emit(PartialState.DraftChanged(filters))
        emit(PartialState.Applied(filters))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, filters = filters))
    }

    override fun reduceState(
        currentState: ObjectionStatusUiState,
        partialState: PartialState,
    ): ObjectionStatusUiState = when (partialState) {
        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(list = currentState.list.failed(partialState.message))
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)
        is PartialState.Applied -> currentState.copy(applied = partialState.filters)
        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.IdentityLoaded -> currentState.copy(
            identityName = partialState.name,
            identityNationalId = partialState.nationalId,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

private fun ObjectionStatusFilters.without(field: ObjectionStatusFilterField): ObjectionStatusFilters =
    when (field) {
        ObjectionStatusFilterField.OBJECTION_NUMBER -> copy(objectionNumber = "")
        ObjectionStatusFilterField.WORKSHOP_ID -> copy(workshopId = "")
        ObjectionStatusFilterField.DEBIT_NUMBER -> copy(debitNumber = "")
    }

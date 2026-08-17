package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.ConfirmRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.DeleteRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.GetRecentlyAddedMembersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.new_member_cannot_edit

/** نام نویسی غیر حضوری بیمه شده. */
class WorkshopRecentlyAddedMembersViewModel(
    private val getRecentlyAddedMembers: GetRecentlyAddedMembersUseCase,
    private val confirmRecentlyAddedMember: ConfirmRecentlyAddedMemberUseCase,
    private val deleteRecentlyAddedMember: DeleteRecentlyAddedMemberUseCase,
) : BaseViewModel<
    WorkshopRecentlyAddedMembersUiState,
    PartialState,
    WorkshopRecentlyAddedMembersEvent,
    WorkshopRecentlyAddedMembersIntent,
    >(initialState = WorkshopRecentlyAddedMembersUiState()) {

    override fun handleIntent(
        intent: WorkshopRecentlyAddedMembersIntent,
    ): Flow<PartialState> = when (intent) {
        is WorkshopRecentlyAddedMembersIntent.Open -> open(intent)
        WorkshopRecentlyAddedMembersIntent.LoadMore -> loadMore()
        WorkshopRecentlyAddedMembersIntent.Retry -> loadPage(page = 0)
        is WorkshopRecentlyAddedMembersIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is WorkshopRecentlyAddedMembersIntent.DraftChanged ->
            flow { emit(PartialState.DraftChanged(intent.draft)) }

        WorkshopRecentlyAddedMembersIntent.ApplySearch -> applySearch(uiState.value.draft)
        WorkshopRecentlyAddedMembersIntent.ClearSearch -> applySearch(NewMemberSearch())
        is WorkshopRecentlyAddedMembersIntent.Confirm -> confirm(intent.member)
        is WorkshopRecentlyAddedMembersIntent.Delete -> delete(intent.member)
        is WorkshopRecentlyAddedMembersIntent.Edit -> edit(intent.member)
        is WorkshopRecentlyAddedMembersIntent.Follow -> flow {
            sendEvent(WorkshopRecentlyAddedMembersEvent.OpenCartable(intent.member.referenceCode))
        }
    }

    private fun open(intent: WorkshopRecentlyAddedMembersIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        search: NewMemberSearch = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getRecentlyAddedMembers(
            WorkshopNewMemberQuery(
                workshopId = workshopId,
                branchCode = branchCode,
                nationalId = search.nationalId.takeIf { it.isNotBlank() },
                requestStatus = search.status,
                page = page,
            )
        )
        emit(
            PartialState.Loaded(
                uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    private fun applySearch(search: NewMemberSearch): Flow<PartialState> = flow {
        emit(PartialState.DraftChanged(search))
        emit(PartialState.Applied(search))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, search = search))
    }

    /** Confirming reloads the list, because the row's own state changes with it. */
    private fun confirm(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        val requestId = member.requestId
        if (!member.canConfirm || requestId == null) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        emit(PartialState.Busy(member.personalId))
        val referenceCode = confirmRecentlyAddedMember(requestId)
        emit(PartialState.Busy(null))
        sendEvent(WorkshopRecentlyAddedMembersEvent.Confirmed(referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.Busy(null))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    private fun delete(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        val personalId = member.personalId
        if (!member.isDraft || personalId == null) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        emit(PartialState.Busy(personalId))
        deleteRecentlyAddedMember(personalId)
        emit(PartialState.Busy(null))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.Busy(null))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    private fun edit(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        if (!member.isDraft) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        sendEvent(
            WorkshopRecentlyAddedMembersEvent.OpenMemberForm(member.requestId ?: NEW_REGISTRATION)
        )
    }

    override fun reduceState(
        currentState: WorkshopRecentlyAddedMembersUiState,
        partialState: PartialState,
    ): WorkshopRecentlyAddedMembersUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(
            busyPersonalId = null,
            list = currentState.list.failed(partialState.message),
        )

        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)
        is PartialState.Applied -> currentState.copy(applied = partialState.search)
        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.Busy -> currentState.copy(busyPersonalId = partialState.personalId)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private companion object {
        /** The member form takes `0` to mean "a registration that does not exist yet". */
        const val NEW_REGISTRATION = 0L
    }
}

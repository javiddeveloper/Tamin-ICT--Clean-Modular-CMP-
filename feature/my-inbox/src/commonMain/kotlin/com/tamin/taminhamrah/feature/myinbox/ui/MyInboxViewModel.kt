package com.tamin.taminhamrah.feature.myinbox.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent.*
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxIntent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState.PartialState
import com.tamin.taminhamrah.mapper.inbox.toPresentation
import com.tamin.taminhamrah.model.inbox.PermitDurationPR
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxItemsUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxSizeUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.permit_duration_one_day
import taminx.core.core_ui.permit_duration_one_month
import taminx.core.core_ui.permit_duration_one_week
import taminx.core.core_ui.permit_duration_one_year

class MyInboxViewModel(
    private val getPersonalInboxItemsUseCase: GetPersonalInboxItemsUseCase,
    private val getPersonalInboxSizeUseCase: GetPersonalInboxSizeUseCase,
) : BaseViewModel<MyInboxUiState, PartialState, MyInboxEvent, MyInboxIntent>(
    initialState = MyInboxUiState()
) {

    init {
        sendIntent(MyInboxIntent.LoadDurations)
        sendIntent(MyInboxIntent.LoadInbox)
    }

    override fun handleIntent(intent: MyInboxIntent): Flow<PartialState> {
        return when (intent) {
            is MyInboxIntent.LoadInbox -> handleLoadInbox()
            is MyInboxIntent.LoadDurations -> handleLoadDurations()
            is MyInboxIntent.OnBackClicked -> {
                sendEvent(MyInboxEvent.NavigateBack)
                emptyFlow()
            }

            is MyInboxIntent.OnCopyClicked -> {
                sendEvent(CopyToClipboard(intent.id))
                emptyFlow()
            }

            is MyInboxIntent.OnItemActionClicked -> {
                if (intent.actionValue == "ISSUE_LICENSE") {
                    flow { emit(PartialState.ShowInquiryPermitSheet(intent.id)) }
                } else {
                    emptyFlow()
                }
            }

            is MyInboxIntent.ShowInquiryPermit -> flow {
                emit(PartialState.ShowInquiryPermitSheet(intent.id))
            }

            is MyInboxIntent.DismissInquiryPermit -> flow {
                emit(PartialState.HideInquiryPermitSheet)
            }

            is MyInboxIntent.ConfirmInquiryPermit -> flow {
                // TODO: Call use case to issue permit with duration.valueInDays
                emit(PartialState.HideInquiryPermitSheet)
            }
        }
    }

    private fun handleLoadInbox(): Flow<PartialState> = merge(
        loadInboxItems(),
        loadInboxSize(),
    )

    private fun handleLoadDurations(): Flow<PartialState> = flow {
        val durations = persistentListOf(
            PermitDurationPR(label = getString(Res.string.permit_duration_one_day), valueInDays = 1),
            PermitDurationPR(label = getString(Res.string.permit_duration_one_week), valueInDays = 7),
            PermitDurationPR(label = getString(Res.string.permit_duration_one_month), valueInDays = 30),
            PermitDurationPR(label = getString(Res.string.permit_duration_one_year), valueInDays = 365)
        )
        emit(PartialState.DurationsLoaded(durations))
    }

    private fun loadInboxItems(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPersonalInboxItemsUseCase().collect { items ->
                emit(PartialState.ItemsLoaded(items.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "Unknown Error"))
        }
    }

    private fun loadInboxSize(): Flow<PartialState> = flow {
        try {
            getPersonalInboxSizeUseCase().collect { size ->
                emit(PartialState.SizeLoaded(size.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun reduceState(
        currentState: MyInboxUiState,
        partialState: PartialState
    ): MyInboxUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )

        is PartialState.ItemsLoaded -> currentState.copy(
            isLoading = false,
            items = partialState.items
        )

        is PartialState.SizeLoaded -> currentState.copy(
            size = partialState.size
        )

        is PartialState.ShowInquiryPermitSheet -> currentState.copy(
            showInquiryPermitSheet = true,
            selectedItemIdForPermit = partialState.itemId
        )

        is PartialState.HideInquiryPermitSheet -> currentState.copy(
            showInquiryPermitSheet = false,
            selectedItemIdForPermit = null
        )

        is PartialState.DurationsLoaded -> currentState.copy(
            permitDurations = partialState.durations
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}

package com.tamin.taminhamrah.feature.profile.ui.activeRelation

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationEvent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationIntent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState.PartialState
import com.tamin.taminhamrah.mapper.activeRelation.toUiModelList
import com.tamin.taminhamrah.mapper.certificate.toUiModelList
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.user.GetRecipientsUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.user.GetStatusCertificateReportUseCase
import com.tamin.taminhamrah.util.currentTime
import com.tamin.taminhamrah.util.toApiDateFormat
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ActiveRelationViewModel(
    private val getRelationTaminAllUseCase: GetRelationTaminAllUseCase,
    private val getStatusCertificateReportUseCase: GetStatusCertificateReportUseCase,
    private val getRecipientsUseCase: GetRecipientsUseCase
) : BaseViewModel<ActiveRelationUiState, PartialState, ActiveRelationEvent, ActiveRelationIntent>(
    initialState = ActiveRelationUiState()
) {

    init {
        sendIntent(ActiveRelationIntent.LoadActiveRelations)
    }

    override fun handleIntent(intent: ActiveRelationIntent): Flow<PartialState> =
        when (intent) {
            is ActiveRelationIntent.LoadActiveRelations -> handleLoadActiveRelations()
            is ActiveRelationIntent.OnBackClicked -> flow { sendEvent(ActiveRelationEvent.NavigateBack) }
            is ActiveRelationIntent.OnSendCertificateClicked -> flow { emit(PartialState.SetShowCertificateSheet(true, intent.item)) }
            is ActiveRelationIntent.OnSelectRecipientClicked -> handleSelectRecipientClicked()
            is ActiveRelationIntent.OnRecipientSelected -> flow {
                emit(PartialState.SetSelectedRecipient(intent.recipient))
                emit(PartialState.SetShowRecipientsSheet(false))
            }
            is ActiveRelationIntent.OnBranchNameChanged -> flow { emit(PartialState.SetBranchName(intent.name)) }
            is ActiveRelationIntent.OnIssueCertificateClicked -> handleIssueCertificate(intent.item)
            is ActiveRelationIntent.OnDismissCertificateSheet -> flow {
                emit(PartialState.SetShowCertificateSheet(false))
            }
            is ActiveRelationIntent.OnDismissRecipientsSheet -> flow {
                emit(PartialState.SetShowRecipientsSheet(false))
            }
            is ActiveRelationIntent.OnDismissSuccessDialog -> flow {
                emit(PartialState.SetShowSuccessDialog(false))
            }
            is ActiveRelationIntent.OnSearchRecipients -> flow { emit(PartialState.SetSearchQuery(intent.query)) }
        }

    private fun handleSelectRecipientClicked(): Flow<PartialState> = flow {
        emit(PartialState.SetShowRecipientsSheet(true))
        if (uiState.value.recipients.isEmpty()) {
            emit(PartialState.SetLoadingRecipients(true))
            getRecipientsUseCase()
                .catch { emit(PartialState.SetError(it.message ?: "خطای نامشخص")) }
                .collect { list ->
                    emit(PartialState.SetRecipients(list.toUiModelList().toImmutableList()))
                    emit(PartialState.SetLoadingRecipients(false))
                }
        }
    }

    private fun handleIssueCertificate(item: ActiveRelationPR): Flow<PartialState> = flow {
        val state = uiState.value
        val recipient = state.selectedRecipient

        emit(PartialState.SetLoading(true))
        val filters = listOf(
            ApiFilterDN(FilterProperty.PAYMENT_BRANCH_CODE, item.branchCode, FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.INSURANCE_NUMBER, item.insuranceId.toApiDateFormat(), FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.END_DATE, item.endDate?.toApiDateFormat() ?: "", FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.RECIPIENT, recipient?.code?:"", FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.BRANCH_NAME, if (state.branchName.isBlank()) item.organizationName else state.branchName, FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.TARGET, "", FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.STATUS_CODE, "01", FilterOperator.EQUAL)
        )

        getStatusCertificateReportUseCase(filters)
            .catch {
                emit(PartialState.SetError(it.message ?: "خطای نامشخص"))
            }
            .collect {
                emit(PartialState.SetLoading(false))
                emit(PartialState.SetShowCertificateSheet(false))
                emit(PartialState.SetShowSuccessDialog(true))
            }
    }

    private fun handleLoadActiveRelations(): Flow<PartialState> = flow {
        emit(PartialState.SetLoading(true))
        getRelationTaminAllUseCase.invoke()
            .map { relations ->
                val uiItems = relations.toUiModelList()
                val activeCount = uiItems.count { it.isActive }
                val inactiveCount = uiItems.count { !it.isActive }

                PartialState.SetData(
                    items = uiItems.toImmutableList(),
                    activeCount = activeCount,
                    inactiveCount = inactiveCount,
                    lastCheckTime = currentTime()
                )
            }
            .catch { emit(PartialState.SetError(it.message ?: "خطای نامشخص")) }
            .collect {
                emit(it)
                emit(PartialState.SetLoading(false))
            }
    }

    override fun reduceState(
        currentState: ActiveRelationUiState,
        partialState: PartialState
    ): ActiveRelationUiState = when (partialState) {
        is PartialState.SetLoading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SetData -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            activeCount = partialState.activeCount,
            inactiveCount = partialState.inactiveCount,
            lastCheckTime = partialState.lastCheckTime,
            error = null
        )
        is PartialState.SetError -> currentState.copy(
            isLoading = false,
            error = partialState.error
        )
        is PartialState.SetShowCertificateSheet -> currentState.copy(
            showCertificateSheet = partialState.show,
            selectedItem = partialState.item ?: currentState.selectedItem,
            selectedRecipient = if (!partialState.show) null else currentState.selectedRecipient,
            branchName = if (!partialState.show) "" else currentState.branchName
        )
        is PartialState.SetShowRecipientsSheet -> currentState.copy(
            showRecipientsSheet = partialState.show,
            searchQuery = if (!partialState.show) "" else currentState.searchQuery,
            filteredRecipients = if (!partialState.show) currentState.recipients else currentState.filteredRecipients
        )
        is PartialState.SetRecipients -> currentState.copy(
            recipients = partialState.list,
            filteredRecipients = partialState.list
        )
        is PartialState.SetLoadingRecipients -> currentState.copy(isLoadingRecipients = partialState.isLoading)
        is PartialState.SetSelectedRecipient -> currentState.copy(selectedRecipient = partialState.recipient)
        is PartialState.SetBranchName -> currentState.copy(branchName = partialState.name)
        is PartialState.SetSearchQuery -> {
            val filtered = if (partialState.query.isBlank()) {
                currentState.recipients
            } else {
                currentState.recipients.filter { it.name.contains(partialState.query) }.toImmutableList()
            }
            currentState.copy(searchQuery = partialState.query, filteredRecipients = filtered)
        }
        is PartialState.SetShowSuccessDialog -> currentState.copy(showSuccessDialog = partialState.show)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SetError(message)
}

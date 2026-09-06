package com.tamin.taminhamrah.feature.contracts.ui.affairs

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsEvent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsIntent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsUiState
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsUiState.PartialState
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractOperation
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractSearchFilter
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.contracts.CancelContractParamsDN
import com.tamin.taminhamrah.model.contracts.ContractFreeJobCode
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.ApiSortDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.request.SortDirection
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.useCases.contracts.CancelContractUseCase
import com.tamin.taminhamrah.useCases.contracts.DownloadContractReportUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractPaymentHistoryUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractStatesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsPageUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * امور قراردادها و پرداخت — the special-insured contract list with search, pagination and the
 * per-contract امور قرارداد operations (pay premium / view payments / edit / view PDF / deactivate).
 *
 * Separate from [com.tamin.taminhamrah.feature.contracts.ui.ContractsViewModel], which drives the
 * older plain list screen.
 */
class ContractAffairsViewModel(
    private val getContractsPageUseCase: GetContractsPageUseCase,
    private val getContractStatesUseCase: GetContractStatesUseCase,
    private val cancelContractUseCase: CancelContractUseCase,
    private val getContractPaymentHistoryUseCase: GetContractPaymentHistoryUseCase,
    private val downloadContractReportUseCase: DownloadContractReportUseCase,
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager,
) : BaseViewModel<ContractAffairsUiState, PartialState, ContractAffairsEvent, ContractAffairsIntent>(
    initialState = ContractAffairsUiState(),
) {

    private val paginator = Paginator(
        baseQuery = defaultQuery(),
        loadPage = { query -> getContractsPageUseCase(query).first() },
    )

    init {
        sendIntent(ContractAffairsIntent.LoadContracts)
    }

    override fun handleIntent(intent: ContractAffairsIntent): Flow<PartialState> {
        return when (intent) {
            ContractAffairsIntent.LoadContracts -> handleLoadContracts()
            ContractAffairsIntent.LoadNextPage -> flow { paginator.loadNext() }
            ContractAffairsIntent.RetryNextPage -> flow { paginator.retry() }
            ContractAffairsIntent.RefreshContracts -> refreshContracts()

            is ContractAffairsIntent.ApplySearch -> applySearch(intent)
            ContractAffairsIntent.ClearSearch -> clearSearch()

            is ContractAffairsIntent.OnNewContractServiceClick ->
                handleNewContractServiceClick(intent.service)

            is ContractAffairsIntent.ShowContractOperations -> flowOf(
                PartialState.OperationsSheetShown(
                    contract = intent.contract,
                    operations = operationsFor(intent.contract).toImmutableList(),
                ),
            )

            ContractAffairsIntent.DismissContractOperations ->
                flowOf(PartialState.OperationsSheetHidden)

            is ContractAffairsIntent.OnOperationClick ->
                handleOperationClick(intent.contract, intent.operation)

            ContractAffairsIntent.DismissCancelSheet -> flowOf(PartialState.CancelSheetHidden)

            is ContractAffairsIntent.OnCancelReasonSelected ->
                flowOf(PartialState.CancelReasonSelected(intent.reason))

            is ContractAffairsIntent.OnCancelDescriptionChanged ->
                flowOf(PartialState.CancelDescriptionChanged(intent.value))

            ContractAffairsIntent.ConfirmCancelContract -> confirmCancelContract()

            ContractAffairsIntent.DismissPaymentHistory ->
                flowOf(PartialState.PaymentHistoryVisibility(false))

            ContractAffairsIntent.DismissPdfViewer -> flow {
                emit(PartialState.PdfViewerVisibility(false))
                emit(PartialState.PdfLoaded(null))
                emit(PartialState.PdfFailed(false))
            }
        }
    }

    // ---- list + pagination ----

    private fun handleLoadContracts(): Flow<PartialState> = merge(
        observePaging(),
        flow { paginator.loadNext() },
        loadMenuOptions(),
    )

    private fun refreshContracts(): Flow<PartialState> = flow { paginator.refresh(currentQuery()) }

    private fun observePaging(): Flow<PartialState> = paginator.state.map { paging ->
        val message = paging.error?.toSingleLineMessage()
        if (message != null && paging.items.isEmpty()) {
            sendEvent(ContractAffairsEvent.ShowError(message))
        }
        PartialState.PagingChanged(
            items = paging.items.toPresentation().toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = message,
        )
    }

    private fun loadMenuOptions(): Flow<PartialState> = flow {
        try {
            val menuItems = getMainMenuUseCase(AppConfig.versionName, false).first()
            emit(PartialState.OptionsLoaded(menuItems.filter { it.id in NEW_CONTRACT_MENU_IDS }))
        } catch (e: Exception) {
            // A missing "new contract" menu is non-fatal; the list is what matters.
        }
    }

    // ---- جستجوی قرارداد ----

    private fun applySearch(intent: ContractAffairsIntent.ApplySearch): Flow<PartialState> = flow {
        val contractNumber = intent.contractNumber.trim()
        val active = contractNumber.isNotBlank() || intent.filter != ContractSearchFilter.ALL
        emit(PartialState.SearchChanged(contractNumber, intent.filter))
        emit(PartialState.SearchApplied(active))
        paginator.refresh(queryFor(contractNumber, intent.filter))
    }

    private fun clearSearch(): Flow<PartialState> = flow {
        emit(PartialState.SearchChanged("", ContractSearchFilter.ALL))
        emit(PartialState.SearchApplied(false))
        paginator.refresh(defaultQuery())
    }

    private fun currentQuery(): ApiQueryParamDN =
        queryFor(uiState.value.searchContractNumber, uiState.value.searchFilter)

    private fun queryFor(contractNumber: String, filter: ContractSearchFilter): ApiQueryParamDN {
        val filters = buildList {
            contractNumber.trim().takeIf { it.isNotEmpty() }?.let {
                add(ApiFilterDN(FilterProperty.CONTRACT_NUMBER, it, FilterOperator.EQ))
            }
            filter.premiumTypeCode?.let {
                add(ApiFilterDN(FilterProperty.PREMIUM_TYPE_CODE, it, FilterOperator.EQ))
            }
        }
        return ApiQueryParamDN(filters = filters, sorts = CREATE_DATE_DESC)
    }

    // ---- امور قرارداد ----

    private fun operationsFor(contract: ContractPR): List<ContractOperation> {
        if (contract.statusCode != ACTIVE_CONTRACT_STATUS_CODE) return emptyList()
        if (contract.premiumTypeCode == ContractPremiumType.FRACTION.code) {
            return listOf(ContractOperation.VIEW_CONTRACT)
        }
        val job = contract.freeJobCode
        return buildList {
            if (job != ContractFreeJobCode.RED_CRESCENT_CODE &&
                job != ContractFreeJobCode.MEDICAL_STUDENT_CODE
            ) {
                add(ContractOperation.PAY_PREMIUM)
            }
            add(ContractOperation.VIEW_PAYMENTS)
            add(ContractOperation.EDIT_CONTRACT)
            add(ContractOperation.VIEW_CONTRACT)
            if (job != ContractFreeJobCode.RED_CRESCENT_CODE) add(ContractOperation.DEACTIVATE)
        }
    }

    private fun handleOperationClick(
        contract: ContractPR,
        operation: ContractOperation,
    ): Flow<PartialState> = when (operation) {
        ContractOperation.PAY_PREMIUM -> {
            sendEvent(ContractAffairsEvent.NavigateToPremiumPayment(contract))
            flowOf(PartialState.OperationsSheetHidden)
        }

        ContractOperation.EDIT_CONTRACT -> {
            sendEvent(ContractAffairsEvent.NavigateToEditContract(contract))
            flowOf(PartialState.OperationsSheetHidden)
        }

        ContractOperation.VIEW_PAYMENTS -> loadPaymentHistory(contract)
        ContractOperation.VIEW_CONTRACT -> downloadContractReport(contract)
        ContractOperation.DEACTIVATE -> showCancelSheet(contract)
    }

    // ---- مشاهده پرداخت‌ها ----

    private fun loadPaymentHistory(contract: ContractPR): Flow<PartialState> = flow {
        emit(PartialState.OperationsSheetHidden)
        emit(PartialState.PaymentHistoryVisibility(true))
        emit(PartialState.PaymentHistoryLoading(true))
        try {
            val items = getContractPaymentHistoryUseCase(contract.contractNumber).first()
            emit(PartialState.PaymentHistoryLoaded(items.toPresentation().toImmutableList()))
        } catch (e: Exception) {
            sendEvent(ContractAffairsEvent.ShowError(e.toSingleLineMessage()))
            emit(PartialState.PaymentHistoryVisibility(false))
        } finally {
            emit(PartialState.PaymentHistoryLoading(false))
        }
    }

    // ---- مشاهده قرارداد (PDF) ----

    private fun downloadContractReport(contract: ContractPR): Flow<PartialState> = flow {
        val premiumType = ContractPremiumType.fromCode(contract.premiumTypeCode)
            ?: ContractPremiumType.OPTIONAL
        emit(PartialState.OperationsSheetHidden)
        emit(PartialState.PdfViewerVisibility(true))
        emit(PartialState.PdfFailed(false))
        emit(PartialState.PdfLoading(true))
        try {
            val pdf = downloadContractReportUseCase(premiumType).first().toPresentation()
            if (pdf.pdf?.pdf != null) {
                emit(PartialState.PdfLoaded(pdf))
            } else {
                emit(PartialState.PdfFailed(true))
            }
        } catch (e: Exception) {
            emit(PartialState.PdfFailed(true))
        } finally {
            emit(PartialState.PdfLoading(false))
        }
    }

    // ---- غیرفعال کردن قرارداد ----

    private fun showCancelSheet(contract: ContractPR): Flow<PartialState> = merge(
        flow {
            emit(PartialState.OperationsSheetHidden)
            emit(PartialState.CancelSheetShown(contract))
        },
        loadCancelReasons(),
    )

    private fun loadCancelReasons(): Flow<PartialState> = flow {
        emit(PartialState.CancelReasonsLoading(true))
        try {
            val reasons = getContractStatesUseCase().first()
            emit(PartialState.CancelReasonsLoaded(reasons.toPresentation().toImmutableList()))
        } catch (e: Exception) {
            sendEvent(ContractAffairsEvent.ShowError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.CancelReasonsLoading(false))
        }
    }

    private fun confirmCancelContract(): Flow<PartialState> = flow {
        val state = uiState.value
        val contract = state.cancelContract ?: return@flow
        val reason = state.selectedCancelReason
        if (reason == null) {
            sendEvent(ContractAffairsEvent.ShowError("لطفاً علت خاتمهٔ قرارداد را انتخاب کنید"))
            return@flow
        }
        val premiumType = ContractPremiumType.fromCode(contract.premiumTypeCode)
            ?: ContractPremiumType.OPTIONAL

        emit(PartialState.Cancelling(true))
        try {
            cancelContractUseCase(
                CancelContractParamsDN(
                    premiumType = premiumType,
                    stateCode = reason.code,
                    description = state.cancelDescription.trim().ifBlank { null },
                ),
            ).collect()
            emit(PartialState.CancelSheetHidden)
            sendEvent(ContractAffairsEvent.ContractCancelled)
            refreshContracts().collect { emit(it) }
        } catch (e: Exception) {
            sendEvent(ContractAffairsEvent.ShowError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Cancelling(false))
        }
    }

    // ---- انعقاد قرارداد جدید ----

    private fun handleNewContractServiceClick(service: MainServiceDN): Flow<PartialState> = flow {
        val flag = FeatureFlag.fromId(service.id) ?: return@flow
        when (val status = featureManager.getFeatureStatus(flag).first()) {
            is FeatureStatus.Enabled -> sendEvent(ContractAffairsEvent.NavigateToService(flag))
            is FeatureStatus.Disabled ->
                status.message?.let { sendEvent(ContractAffairsEvent.ShowToast(it)) }

            is FeatureStatus.TemporaryDisabled ->
                status.message?.let { sendEvent(ContractAffairsEvent.ShowToast(it)) }

            is FeatureStatus.EnabledWithError -> {
                status.message?.let { sendEvent(ContractAffairsEvent.ShowToast(it)) }
                sendEvent(ContractAffairsEvent.NavigateToService(flag))
            }

            is FeatureStatus.WebView -> sendEvent(ContractAffairsEvent.NavigateToWeb(status.url))
        }
    }

    override fun reduceState(
        currentState: ContractAffairsUiState,
        partialState: PartialState,
    ): ContractAffairsUiState = when (partialState) {
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)

        is PartialState.OptionsLoaded -> currentState.copy(newContractOptions = partialState.options)

        is PartialState.PagingChanged -> currentState.copy(
            contracts = partialState.items,
            isLoading = partialState.isLoadingFirstPage,
            isLoadingNextPage = partialState.isLoadingNextPage,
            endReached = partialState.endReached,
            paginationError = partialState.error,
        )

        is PartialState.SearchChanged -> currentState.copy(
            searchContractNumber = partialState.contractNumber,
            searchFilter = partialState.filter,
        )

        is PartialState.SearchApplied -> currentState.copy(isSearchActive = partialState.active)

        is PartialState.OperationsSheetShown -> currentState.copy(
            operationsContract = partialState.contract,
            operations = partialState.operations,
        )

        PartialState.OperationsSheetHidden -> currentState.copy(
            operationsContract = null,
            operations = persistentListOf(),
        )

        is PartialState.CancelSheetShown -> currentState.copy(
            showCancelSheet = true,
            cancelContract = partialState.contract,
            selectedCancelReason = null,
            cancelDescription = "",
        )

        PartialState.CancelSheetHidden -> currentState.copy(
            showCancelSheet = false,
            cancelContract = null,
            cancelReasons = persistentListOf(),
            selectedCancelReason = null,
            cancelDescription = "",
            isCancelReasonsLoading = false,
        )

        is PartialState.CancelReasonsLoading ->
            currentState.copy(isCancelReasonsLoading = partialState.loading)

        is PartialState.CancelReasonsLoaded -> currentState.copy(cancelReasons = partialState.reasons)

        is PartialState.CancelReasonSelected ->
            currentState.copy(selectedCancelReason = partialState.reason)

        is PartialState.CancelDescriptionChanged ->
            currentState.copy(cancelDescription = partialState.description)

        is PartialState.Cancelling -> currentState.copy(isCancelling = partialState.inProgress)

        is PartialState.PaymentHistoryVisibility -> currentState.copy(
            showPaymentHistory = partialState.visible,
            paymentHistory = if (partialState.visible) currentState.paymentHistory else persistentListOf(),
        )

        is PartialState.PaymentHistoryLoading ->
            currentState.copy(isPaymentHistoryLoading = partialState.loading)

        is PartialState.PaymentHistoryLoaded ->
            currentState.copy(paymentHistory = partialState.items)

        is PartialState.PdfViewerVisibility -> currentState.copy(
            showPdfViewer = partialState.visible,
            pdfDownload = if (partialState.visible) currentState.pdfDownload else null,
            pdfDownloadFailed = if (partialState.visible) currentState.pdfDownloadFailed else false,
        )

        is PartialState.PdfLoading -> currentState.copy(isPdfLoading = partialState.loading)

        is PartialState.PdfLoaded -> currentState.copy(
            pdfDownload = partialState.pdf,
            pdfDownloadFailed = false,
        )

        is PartialState.PdfFailed -> currentState.copy(pdfDownloadFailed = partialState.failed)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN(sorts = CREATE_DATE_DESC)

    companion object {
        /** `contractStatusObject.selfIsuContStatDode == 1` → contract is currently active. */
        private const val ACTIVE_CONTRACT_STATUS_CODE = 1

        /** Dynamic-menu ids for انعقاد قرارداد جدید (اختیاری / حرف و مشاغل / تکمیل سوابق …). */
        private val NEW_CONTRACT_MENU_IDS = listOf(33, 34, 36, 37, 39)

        private val CREATE_DATE_DESC = listOf(
            ApiSortDN(property = "creatDate", direction = SortDirection.DESC),
        )
    }
}

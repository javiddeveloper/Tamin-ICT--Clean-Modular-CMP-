package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.ARTICLE16_FILING_WINDOW_DAYS
import com.tamin.taminhamrah.model.workshop.Article16DebtPR
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.Article16RequestStatus
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetArticle16DebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16ReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16RequestInfoUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.article16_deadline_passed
import taminx.core.core_ui.workshop_error_receive_data

/** رسیدگی به بدهی ماده ۱۶ — the debt list and the actions each row offers. */
class ManagementDebitViewModel(
    private val getArticle16Debts: GetArticle16DebtsUseCase,
    private val getArticle16RequestInfo: GetArticle16RequestInfoUseCase,
    private val getArticle16ReportPdf: GetArticle16ReportPdfUseCase,
) : BaseViewModel<
    ManagementDebitUiState,
    PartialState,
    ManagementDebitEvent,
    ManagementDebitIntent,
    >(initialState = ManagementDebitUiState()) {

    override fun handleIntent(intent: ManagementDebitIntent): Flow<PartialState> = when (intent) {
        is ManagementDebitIntent.Open -> open(intent)
        ManagementDebitIntent.LoadMore -> loadMore()
        ManagementDebitIntent.Retry -> loadPage(page = 0)
        is ManagementDebitIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is ManagementDebitIntent.DraftChanged -> flow { emit(PartialState.DraftChanged(intent.draft)) }
        ManagementDebitIntent.ApplySearch -> applySearch(uiState.value.draft)
        ManagementDebitIntent.ClearSearch -> applySearch(Article16Search())
        is ManagementDebitIntent.StatusFilterChanged ->
            flow { emit(PartialState.StatusFilterChanged(intent.status)) }

        is ManagementDebitIntent.ActionsRequested ->
            flow { emit(PartialState.ActionsForChanged(intent.debt)) }

        ManagementDebitIntent.ActionsDismissed -> flow { emit(PartialState.ActionsForChanged(null)) }
        is ManagementDebitIntent.RequestReview -> requestReview(intent.debt)
        is ManagementDebitIntent.FixRequest -> fixRequest(intent.debt)
        is ManagementDebitIntent.ShowRequestPdf -> showRequestPdf(intent.debt)
        is ManagementDebitIntent.ShowExpertMessage -> showExpertMessage(intent.debt)
        ManagementDebitIntent.DismissViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
        ManagementDebitIntent.DismissExpertMessage ->
            flow { emit(PartialState.ExpertMessageChanged(null)) }
    }

    private fun open(intent: ManagementDebitIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode, intent.workshopName))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        search: Article16Search = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getArticle16Debts(
            Article16DebtQuery(
                workshopId = workshopId,
                branchCode = branchCode,
                debitNumber = search.debitNumber.takeIf { it.isNotBlank() },
                agreementRow = search.agreementRow.takeIf { it.isNotBlank() },
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

    private fun applySearch(search: Article16Search): Flow<PartialState> = flow {
        emit(PartialState.DraftChanged(search))
        emit(PartialState.Applied(search))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, search = search))
    }

    /**
     * A first request is only accepted within a day of ابلاغ اجراییه.
     *
     * There is no `diff-days` endpoint for ماده ۱۶, so the count is taken from the device clock. A
     * row whose date the service did not send cannot be checked, and is refused rather than let
     * through — the deadline is the service's rule, not a formality.
     */
    private fun requestReview(debt: Article16DebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        val elapsed = PersianDateFormatter.daysSince(debt.executiveNotifyDate)
        if (elapsed == null || elapsed > ARTICLE16_FILING_WINDOW_DAYS) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.article16_deadline_passed))
            return@flow
        }
        sendEvent(ManagementDebitEvent.OpenRequestForm(debt, status = null))
    }

    private fun fixRequest(debt: Article16DebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        sendEvent(ManagementDebitEvent.OpenRequestForm(debt, Article16RequestStatus.DOCUMENT_DEFECT))
    }

    private fun showRequestPdf(debt: Article16DebtPR): Flow<PartialState> = flow<PartialState> {
        emit(PartialState.ActionsForChanged(null))
        val seqNo = debt.seqNo
        if (seqNo == null) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.Busy(true))
        emit(PartialState.ViewerPdfChanged(getArticle16ReportPdf(seqNo).toPresentation()))
    }.catch {
        emit(PartialState.Busy(false))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    private fun showExpertMessage(debt: Article16DebtPR): Flow<PartialState> = flow<PartialState> {
        emit(PartialState.ActionsForChanged(null))
        val seqNo = debt.seqNo
        if (seqNo == null) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.Busy(true))
        val info = getArticle16RequestInfo(seqNo)
        emit(PartialState.Busy(false))
        emit(PartialState.ExpertMessageChanged(info.defectDescription))
    }.catch {
        emit(PartialState.Busy(false))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    override fun reduceState(
        currentState: ManagementDebitUiState,
        partialState: PartialState,
    ): ManagementDebitUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
            workshopName = partialState.workshopName,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(
            isBusy = false,
            list = currentState.list.failed(partialState.message),
        )

        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)
        is PartialState.Applied -> currentState.copy(applied = partialState.search)
        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.StatusFilterChanged -> currentState.copy(statusFilter = partialState.status)
        is PartialState.ActionsForChanged -> currentState.copy(actionsFor = partialState.debt)
        is PartialState.Busy -> currentState.copy(isBusy = partialState.isBusy)
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isBusy = false,
            viewerPdf = partialState.pdf,
        )

        is PartialState.ExpertMessageChanged -> currentState.copy(
            isBusy = false,
            expertMessage = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

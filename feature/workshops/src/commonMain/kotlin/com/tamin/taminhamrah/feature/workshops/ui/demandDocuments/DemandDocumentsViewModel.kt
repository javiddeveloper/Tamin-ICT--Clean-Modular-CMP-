package com.tamin.taminhamrah.feature.workshops.ui.demandDocuments

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.demandDocuments.DemandDocumentsUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetDebitTurnoverPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDemandDocumentsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** اسناد مطالبه of one debt. */
class DemandDocumentsViewModel(
    private val getDemandDocuments: GetDemandDocumentsUseCase,
    private val getDebitTurnoverPdf: GetDebitTurnoverPdfUseCase,
) : BaseViewModel<DemandDocumentsUiState, PartialState, DemandDocumentsEvent, DemandDocumentsIntent>(
    initialState = DemandDocumentsUiState()
) {

    override fun handleIntent(intent: DemandDocumentsIntent): Flow<PartialState> = when (intent) {
        is DemandDocumentsIntent.Open -> open(intent)
        DemandDocumentsIntent.LoadMore -> loadMore()
        DemandDocumentsIntent.Retry -> loadPage(page = 0)
        DemandDocumentsIntent.ShowCalculationPdf -> downloadPdf()
        DemandDocumentsIntent.DismissViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
    }

    private fun open(intent: DemandDocumentsIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.debitNumber == intent.debitNumber && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.debitNumber, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.debitNumber to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        identity: Pair<String, String> = uiState.value.debitNumber to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (debitNumber, branchCode) = identity
        if (debitNumber.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getDemandDocuments(debitNumber, branchCode, page)
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

    private fun downloadPdf(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Downloading(true))
        emit(PartialState.ViewerPdfChanged(null))
        val pdf = getDebitTurnoverPdf(state.debitNumber, state.branchCode)
        emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
    }.catch {
        emit(PartialState.Error(it.toSingleLineMessage()))
        emit(PartialState.DownloadFailed)
    }

    override fun reduceState(
        currentState: DemandDocumentsUiState,
        partialState: PartialState,
    ): DemandDocumentsUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            debitNumber = partialState.debitNumber,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(
            isDownloading = false,
            list = currentState.list.failed(partialState.message),
        )

        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.Downloading -> currentState.copy(
            isDownloading = partialState.isDownloading,
            downloadFailed = false,
        )

        is PartialState.ViewerPdfChanged -> currentState.copy(
            isDownloading = false,
            viewerPdf = partialState.pdf,
            downloadFailed = false,
        )

        PartialState.DownloadFailed -> currentState.copy(
            isDownloading = false,
            downloadFailed = true,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

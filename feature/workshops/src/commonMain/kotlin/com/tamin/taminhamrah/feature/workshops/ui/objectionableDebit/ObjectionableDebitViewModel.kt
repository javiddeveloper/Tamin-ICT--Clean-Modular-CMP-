package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.CheckObjectionDeadlineUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetObjectionableDebitsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_expired
import taminx.core.core_ui.workshop_error_receive_data

/** اعتراض به بدهی. */
class ObjectionableDebitViewModel(
    private val getObjectionableDebits: GetObjectionableDebitsUseCase,
    private val checkObjectionDeadline: CheckObjectionDeadlineUseCase,
    private val getDebitObjectionPdf: GetDebitObjectionPdfUseCase,
) : BaseViewModel<
    ObjectionableDebitUiState,
    PartialState,
    ObjectionableDebitEvent,
    ObjectionableDebitIntent,
    >(initialState = ObjectionableDebitUiState()) {

    /**
     * The domain rows the presentation rows were built from, kept so a deadline check and the
     * eventual submission work on the debt the service sent rather than on its formatted copy.
     */
    private var debtsByNumber: Map<String, WorkShopDebtDN> = emptyMap()

    override fun handleIntent(intent: ObjectionableDebitIntent): Flow<PartialState> = when (intent) {
        is ObjectionableDebitIntent.Open -> open(intent)
        ObjectionableDebitIntent.LoadMore -> loadMore()
        ObjectionableDebitIntent.Retry -> loadPage(page = 0)
        is ObjectionableDebitIntent.RowAction -> rowAction(intent.debt)
        ObjectionableDebitIntent.DismissViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
    }

    private fun open(intent: ObjectionableDebitIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getObjectionableDebits(workshopId, branchCode, page)
        debtsByNumber = (if (page == 0) emptyMap() else debtsByNumber) +
            result.items.associateBy { it.debitNumber }
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

    /**
     * One action per row: an objection already filed is opened as a PDF, and a new one is only
     * allowed once the service's own day count says the filing window is still open.
     */
    private fun rowAction(debt: WorkShopDebtPR): Flow<PartialState> = when (debt.objectionKind) {
        ObjectionKind.FILED -> downloadObjectionPdf(debt)
        else -> checkDeadline(debt)
    }

    private fun downloadObjectionPdf(debt: WorkShopDebtPR): Flow<PartialState> = flow {
        val seqNo = debt.objectionSeqNo
        if (seqNo == null) {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.Downloading(true))
        emit(PartialState.ViewerPdfChanged(getDebitObjectionPdf(seqNo).toPresentation()))
    }.catch {
        emit(PartialState.Downloading(false))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    private fun checkDeadline(debt: WorkShopDebtPR): Flow<PartialState> = flow {
        val domainDebt = debtsByNumber[debt.debitNumber]
        if (domainDebt == null) {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.Checking(debt.debitNumber))
        val allowed = checkObjectionDeadline(domainDebt)
        emit(PartialState.Checking(null))
        if (allowed) {
            sendEvent(ObjectionableDebitEvent.OpenObjectionForm(debt))
        } else {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.objection_expired))
        }
    }.catch {
        emit(PartialState.Checking(null))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    override fun reduceState(
        currentState: ObjectionableDebitUiState,
        partialState: PartialState,
    ): ObjectionableDebitUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(
            isDownloading = false,
            list = currentState.list.failed(partialState.message),
        )

        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.Checking -> currentState.copy(checkingDebitNumber = partialState.debitNumber)
        is PartialState.Downloading -> currentState.copy(isDownloading = partialState.isDownloading)
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isDownloading = false,
            viewerPdf = partialState.pdf,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

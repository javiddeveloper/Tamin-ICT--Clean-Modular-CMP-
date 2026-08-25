package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormDocument
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.Article16DocumentTypes
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.workshop.ARTICLE16_FILING_WINDOW_DAYS
import com.tamin.taminhamrah.model.workshop.Article16DebtPR
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.Article16SaveRequestDN
import com.tamin.taminhamrah.model.workshop.ObjectionDocumentDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16DebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16ReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16RequestInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16WorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveArticle16RequestUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.article16_deadline_passed
import taminx.core.core_ui.workshop_error_receive_data

/** رسیدگی به بدهی ماده ۱۶ — the debt list and the actions each row offers. */
class ManagementDebitViewModel(
    private val getArticle16Debts: GetArticle16DebtsUseCase,
    private val getArticle16RequestInfo: GetArticle16RequestInfoUseCase,
    private val getArticle16ReportPdf: GetArticle16ReportPdfUseCase,
    private val getArticle16WorkshopInfo: GetArticle16WorkshopInfoUseCase,
    private val uploadImage: UploadImageUseCase,
    private val saveArticle16Request: SaveArticle16RequestUseCase,
) : BaseViewModel<
    ManagementDebitUiState,
    PartialState,
    ManagementDebitEvent,
    ManagementDebitIntent,
    >(initialState = ManagementDebitUiState()) {

    /**
     * The domain rows the presentation rows were built from, kept so the submission works on the
     * debt the service sent rather than on its formatted copy.
     */
    private var debtsByNumber: Map<String, WorkshopsDebtListModelDN> = emptyMap()

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
        ManagementDebitIntent.FormDismissed -> flow { emit(PartialState.FormChanged(null)) }
        ManagementDebitIntent.FormNext -> formNext()
        ManagementDebitIntent.FormPrev -> editForm { copy(step = (step - 1).coerceAtLeast(1)) }
        is ManagementDebitIntent.FormDebtOpenChanged ->
            editForm { copy(isDebtOpen = intent.isOpen) }

        is ManagementDebitIntent.FormWorkshopOpenChanged ->
            editForm { copy(isWorkshopOpen = intent.isOpen) }

        is ManagementDebitIntent.FormConfirmedChanged ->
            editForm { copy(isConfirmed = intent.isConfirmed, hasTriedSubmit = false) }

        is ManagementDebitIntent.FormAddDocument -> addDocument(intent)
        is ManagementDebitIntent.FormRemoveDocument -> editForm {
            copy(
                documents = documents.removeAt(intent.index),
                uploaded = uploaded.removeAt(intent.index),
            )
        }

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
        emitAll(openForm(debt))
    }

    private fun fixRequest(debt: Article16DebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        emitAll(openForm(debt))
    }

    private fun showRequestPdf(debt: Article16DebtPR): Flow<PartialState> = flow {
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

    private fun showExpertMessage(debt: Article16DebtPR): Flow<PartialState> = flow {
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

    /** One-line edits of the open form, which is most of what it does. */
    private fun editForm(edit: Article16FormState.() -> Article16FormState) =
        flow { emit(PartialState.FormEdited(edit)) }

    /**
     * Opens the request on a debt, with the workshop block its first step reviews.
     *
     * The workshop lookup is allowed to fail quietly: it fills a review panel, and losing it is
     * not a reason to refuse a request the deadline check has already allowed.
     */
    private fun openForm(debt: Article16DebtPR): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.FormChanged(Article16FormState(debt = debt)))
        val info = runCatching {
            getArticle16WorkshopInfo(state.workshopId, state.branchCode)
        }.getOrNull() ?: return@flow
        emit(
            PartialState.FormEdited {
                copy(
                    workshopInfo = Article16WorkshopInfoPR(
                        workshopName = info.workshopName,
                        workshopCode = info.workshopId.toPersianDigits(),
                        branchCode = info.branchCode.toPersianDigits(),
                        employerName = info.employerName,
                        address = info.address,
                    ),
                )
            },
        )
    }

    /** «مرحلهٔ بعد» on step one, and the submission on the last. */
    private fun formNext(): Flow<PartialState> {
        val form = uiState.value.form ?: return flow { }
        if (!form.isLastStep) return editForm { copy(step = step + 1) }
        return submitRequest()
    }

    /**
     * Sends the picked file up and keeps only the guid that comes back.
     *
     * The file joins the list once the service has taken it, not when it was chosen — otherwise a
     * failed upload leaves a row that stands for nothing.
     */
    private fun addDocument(
        intent: ManagementDebitIntent.FormAddDocument,
    ): Flow<PartialState> = flow<PartialState> {
        emit(PartialState.FormEdited { copy(isUploading = true) })
        val guid = uploadImage(
            UploadImageRequestDN(fileName = intent.fileName, bytes = intent.bytes),
        ).first()
        val type = Article16DocumentTypes.first { it.code == intent.typeCode }
        emit(
            PartialState.FormEdited {
                copy(
                    isUploading = false,
                    hasTriedSubmit = false,
                    documents = documents.add(
                        WorkshopFormDocument(
                            typeCode = type.code,
                            typeLabel = type.label,
                            size = intent.bytes.size.asKilobytes(),
                        ),
                    ),
                    uploaded = uploaded.add(UploadedArticle16Document(guid, intent.typeCode)),
                )
            },
        )
    }.catch {
        emit(PartialState.FormEdited { copy(isUploading = false) })
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    /**
     * Files the request, once every rule the form states is actually satisfied.
     *
     * The guard runs here rather than in the screen, so a form that is not ready simply reveals
     * why and cannot be submitted by any other path.
     */
    private fun submitRequest(): Flow<PartialState> = flow {
        val state = uiState.value
        val form = state.form ?: return@flow
        if (form.documents.isEmpty() || !form.isConfirmed) {
            emit(PartialState.FormEdited { copy(hasTriedSubmit = true) })
            return@flow
        }
        val domainDebt = debtsByNumber[form.debt.debitNumber]
        if (domainDebt == null) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }

        emit(PartialState.FormEdited { copy(isSubmitting = true) })
        val result = saveArticle16Request(
            Article16SaveRequestDN(
                workshopId = state.workshopId,
                branchCode = state.branchCode,
                debt = domainDebt,
                documents = form.uploaded.map { ObjectionDocumentDN(it.guid, it.typeCode) },
            ),
        )
        emit(PartialState.FormChanged(null))
        sendEvent(ManagementDebitEvent.Article16Filed(result.referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.FormEdited { copy(isSubmitting = false) })
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
        is PartialState.FormChanged -> currentState.copy(form = partialState.form)
        is PartialState.FormEdited -> currentState.copy(
            form = currentState.form?.let(partialState.edit),
        )

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

/**
 * A byte count as the whole kilobytes the upload box prints.
 *
 * Rounded up, so a file that is genuinely there never reads as «۰ کیلوبایت».
 */
private fun Int.asKilobytes(): String =
    ((this + BYTES_PER_KB - 1) / BYTES_PER_KB).toString().toPersianDigits()

private const val BYTES_PER_KB = 1024

package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.ArticleSixteenDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.ARTICLE_SIXTEEN_FILING_WINDOW_DAYS
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtQuery
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDN
import com.tamin.taminhamrah.model.workshop.ObjectionDocumentDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenDebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenRequestInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveArticleSixteenRequestUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_sixteen_deadline_passed
import taminx.core.core_ui.workshop_error_receive_data

/** رسیدگی به بدهی ماده ۱۶ — the debt list and the actions each row offers. */
class ManagementDebitViewModel(
    private val getArticleSixteenDebts: GetArticleSixteenDebtsUseCase,
    private val getArticleSixteenRequestInfo: GetArticleSixteenRequestInfoUseCase,
    private val getArticleSixteenReportPdf: GetArticleSixteenReportPdfUseCase,
    private val getArticleSixteenWorkshopInfo: GetArticleSixteenWorkshopInfoUseCase,
    private val uploadAttachment: WorkshopAttachmentUploader,
    private val saveArticleSixteenRequest: SaveArticleSixteenRequestUseCase,
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
        ManagementDebitIntent.ClearSearch -> applySearch(ArticleSixteenSearch())
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
        ManagementDebitIntent.FormPrev -> just(
            PartialState.FormStepChanged((currentStep() - 1).coerceAtLeast(FIRST_STEP)),
        )

        is ManagementDebitIntent.FormDebtOpenChanged ->
            just(PartialState.FormDebtOpenChanged(intent.isOpen))

        is ManagementDebitIntent.FormWorkshopOpenChanged ->
            just(PartialState.FormWorkshopOpenChanged(intent.isOpen))

        is ManagementDebitIntent.FormConfirmedChanged ->
            just(PartialState.FormConfirmedChanged(intent.isConfirmed))

        is ManagementDebitIntent.FormAddDocument -> addAttachment(intent)
        is ManagementDebitIntent.FormRemoveDocument ->
            just(PartialState.FormAttachmentRemoved(intent.index))

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
        search: ArticleSixteenSearch = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getArticleSixteenDebts(
            ArticleSixteenDebtQuery(
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

    private fun applySearch(search: ArticleSixteenSearch): Flow<PartialState> = flow {
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
    private fun requestReview(debt: ArticleSixteenDebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        val elapsed = PersianDateFormatter.daysSince(debt.executiveNotifyDate)
        if (elapsed == null || elapsed > ARTICLE_SIXTEEN_FILING_WINDOW_DAYS) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.article_sixteen_deadline_passed))
            return@flow
        }
        emitAll(openForm(debt))
    }

    private fun fixRequest(debt: ArticleSixteenDebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        emitAll(openForm(debt))
    }

    private fun showRequestPdf(debt: ArticleSixteenDebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        val seqNo = debt.seqNo
        if (seqNo == null) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.Busy(true))
        emit(PartialState.ViewerPdfChanged(getArticleSixteenReportPdf(seqNo).toPresentation()))
    }.catch {
        emit(PartialState.Busy(false))
        emit(reportFailure(it))
    }

    private fun showExpertMessage(debt: ArticleSixteenDebtPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        val seqNo = debt.seqNo
        if (seqNo == null) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.Busy(true))
        val info = getArticleSixteenRequestInfo(seqNo)
        emit(PartialState.Busy(false))
        emit(PartialState.ExpertMessageChanged(info.defectDescription))
    }.catch {
        emit(PartialState.Busy(false))
        emit(reportFailure(it))
    }

    /** One-line edits of the open form, which is most of what it does. */
    private fun just(partialState: PartialState): Flow<PartialState> =
        flow { emit(partialState) }

    /** The step the open form is on, or the first when none is open. */
    private fun currentStep(): Int = uiState.value.form?.step ?: FIRST_STEP

    /**
     * Opens the request on a debt, with the workshop block its first step reviews.
     *
     * The workshop lookup is allowed to fail quietly: it fills a review panel, and losing it is
     * not a reason to refuse a request the deadline check has already allowed.
     */
    private fun openForm(debt: ArticleSixteenDebtPR): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.FormChanged(ArticleSixteenFormState(debt = debt)))
        val info = runCatching {
            getArticleSixteenWorkshopInfo(state.workshopId, state.branchCode)
        }.getOrNull() ?: return@flow
        emit(
            PartialState.FormWorkshopInfoLoaded(
                ArticleSixteenWorkshopInfoPR(
                    workshopName = info.workshopName,
                    workshopCode = info.workshopId.toPersianDigits(),
                    branchCode = info.branchCode.toPersianDigits(),
                    employerName = info.employerName,
                    address = info.address,
                ),
            ),
        )
    }

    /** «مرحلهٔ بعد» on step one, and the submission on the last. */
    private fun formNext(): Flow<PartialState> {
        val form = uiState.value.form ?: return flow { }
        if (!form.isLastStep) return just(PartialState.FormStepChanged(form.step + 1))
        return submitRequest()
    }

    /**
     * Sends the picked file up and keeps only the guid that comes back.
     *
     * The file joins the list once the service has taken it, not when it was chosen — otherwise a
     * failed upload leaves a row that stands for nothing.
     */
    private fun addAttachment(
        intent: ManagementDebitIntent.FormAddDocument,
    ): Flow<PartialState> = flow {
        emit(PartialState.FormUploadingChanged(true))
        val attachment = uploadAttachment(
            fileName = intent.fileName,
            bytes = intent.bytes,
            typeCode = intent.typeCode,
            types = ArticleSixteenDocumentTypes,
        )
        emit(PartialState.FormAttachmentAdded(attachment))
    }.catch {
        emit(PartialState.FormUploadingChanged(false))
        emit(reportFailure(it))
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
        if (form.attachments.isEmpty() || !form.isConfirmed) {
            emit(PartialState.FormSubmitRejected)
            return@flow
        }
        val domainDebt = debtsByNumber[form.debt.debitNumber]
        if (domainDebt == null) {
            sendEvent(ManagementDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }

        emit(PartialState.FormSubmittingChanged(true))
        val result = saveArticleSixteenRequest(
            ArticleSixteenSaveRequestDN(
                workshopId = state.workshopId,
                branchCode = state.branchCode,
                debt = domainDebt,
                documents = form.attachments.map {
                    ObjectionDocumentDN(it.guid, it.type.code)
                },
            ),
        )
        emit(PartialState.FormChanged(null))
        sendEvent(ManagementDebitEvent.ArticleSixteenFiled(result.referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.FormSubmittingChanged(false))
        emit(reportFailure(it))
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
        is PartialState.FormStepChanged -> currentState.editForm {
            copy(step = partialState.step, hasTriedSubmit = false)
        }

        is PartialState.FormDebtOpenChanged -> currentState.editForm {
            copy(isDebtOpen = partialState.isOpen)
        }

        is PartialState.FormWorkshopOpenChanged -> currentState.editForm {
            copy(isWorkshopOpen = partialState.isOpen)
        }

        is PartialState.FormWorkshopInfoLoaded -> currentState.editForm {
            copy(workshopInfo = partialState.info)
        }
        is PartialState.FormConfirmedChanged -> currentState.editForm {
            copy(isConfirmed = partialState.isConfirmed, hasTriedSubmit = false)
        }

        is PartialState.FormAttachmentAdded -> currentState.editForm {
            copy(
                isUploading = false,
                hasTriedSubmit = false,
                attachments = attachments.add(partialState.attachment),
            )
        }

        is PartialState.FormAttachmentRemoved -> currentState.editForm {
            copy(attachments = attachments.removeAt(partialState.index))
        }

        PartialState.FormSubmitRejected -> currentState.editForm { copy(hasTriedSubmit = true) }
        is PartialState.FormUploadingChanged -> currentState.editForm {
            copy(isUploading = partialState.isUploading)
        }

        is PartialState.FormSubmittingChanged -> currentState.editForm {
            copy(isSubmitting = partialState.isSubmitting)
        }

        is PartialState.ViewerPdfChanged -> currentState.copy(
            isBusy = false,
            viewerPdf = partialState.pdf,
        )

        is PartialState.ExpertMessageChanged -> currentState.copy(
            isBusy = false,
            expertMessage = partialState.message,
        )
    }

    /**
     * A failure the user must see now.
     *
     * With a form open the list is not on screen, so its error state is not either; the
     * message is raised as an event instead and the toast host shows it.
     */
    private fun reportFailure(throwable: Throwable): PartialState {
        val message = throwable.toSingleLineMessage()
        if (uiState.value.form != null) {
            sendEvent(ManagementDebitEvent.ShowServerMessage(message))
        }
        return PartialState.Error(message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/** Applies [edit] to the open form, or does nothing when no form is open. */
private inline fun ManagementDebitUiState.editForm(
    edit: ArticleSixteenFormState.() -> ArticleSixteenFormState,
): ManagementDebitUiState = copy(form = form?.edit())

/** Forms count their steps from one. */
private const val FIRST_STEP = 1

package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.ObjectionDocumentDN
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.CheckObjectionDeadlineUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetObjectionableDebitsUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveDebitObjectionUseCase
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
    private val uploadAttachment: WorkshopAttachmentUploader,
    private val saveDebitObjection: SaveDebitObjectionUseCase,
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
    private val debtsByNumber = mutableMapOf<String, WorkShopDebtDN>()

    override fun handleIntent(intent: ObjectionableDebitIntent): Flow<PartialState> = when (intent) {
        is ObjectionableDebitIntent.Open -> open(intent)
        ObjectionableDebitIntent.LoadMore -> loadMore()
        ObjectionableDebitIntent.Retry -> loadPage(page = 0)
        is ObjectionableDebitIntent.RowAction -> rowAction(intent.debt)
        ObjectionableDebitIntent.DismissViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }

        ObjectionableDebitIntent.FormDismissed -> flow { emit(PartialState.FormChanged(null)) }
        is ObjectionableDebitIntent.FormDebtOpenChanged ->
            just(PartialState.FormDebtOpenChanged(intent.isOpen))

        is ObjectionableDebitIntent.FormDescriptionChanged ->
            just(PartialState.FormDescriptionChanged(intent.text))

        is ObjectionableDebitIntent.FormDepositChanged ->
            just(PartialState.FormDepositChanged(intent.isDeposit))

        is ObjectionableDebitIntent.FormConfirmedChanged ->
            just(PartialState.FormConfirmedChanged(intent.isConfirmed))

        is ObjectionableDebitIntent.FormAddDocument -> addAttachment(intent)
        is ObjectionableDebitIntent.FormRemoveDocument ->
            just(PartialState.FormAttachmentRemoved(intent.index))

        ObjectionableDebitIntent.FormSubmit -> askToConfirm()
        ObjectionableDebitIntent.FormConfirmDismissed ->
            just(PartialState.FormConfirmVisible(false))

        ObjectionableDebitIntent.FormConfirmAccepted -> submitObjection()
    }

    private fun just(partialState: PartialState): Flow<PartialState> = flow { emit(partialState) }

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
        if (page == 0) debtsByNumber.clear()
        result.items.forEach { debtsByNumber[it.debitNumber] = it }
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
        emit(reportFailure(it))
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
            emit(PartialState.FormChanged(ObjectionFormState(debt = debt)))
        } else {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.objection_expired))
        }
    }.catch {
        emit(PartialState.Checking(null))
        emit(reportFailure(it))
    }

    /**
     * Sends the picked file up and keeps only the guid that comes back.
     *
     * The upload is what takes the time, so the box shows a spinner for it alone — the file only
     * joins the list once the service has actually taken it.
     */
    private fun addAttachment(
        intent: ObjectionableDebitIntent.FormAddDocument,
    ): Flow<PartialState> = flow {
        emit(PartialState.FormUploadingChanged(true))
        val attachment = uploadAttachment(
            fileName = intent.fileName,
            bytes = intent.bytes,
            typeCode = intent.typeCode,
            types = ObjectionDocumentTypes,
        )
        emit(PartialState.FormAttachmentAdded(attachment))
    }.catch {
        emit(PartialState.FormUploadingChanged(false))
        emit(reportFailure(it))
    }

    /**
     * The last gate before the objection leaves: every rule the form states, then the user saying
     * so out loud.
     *
     * The rules are checked here rather than in the screen so that a form which is not ready
     * simply reveals why, and cannot be submitted by any other path either. The dialog is what
     * the old app asked for too — a filed objection cannot be withdrawn, so the tick alone is
     * not taken as the answer.
     */
    private fun askToConfirm(): Flow<PartialState> = flow {
        val form = uiState.value.form ?: return@flow
        if (form.attachments.isEmpty() || !form.isConfirmed) {
            emit(PartialState.FormSubmitRejected)
            return@flow
        }
        emit(PartialState.FormConfirmVisible(true))
    }

    /** Files the objection, once [askToConfirm] has been answered. */
    private fun submitObjection(): Flow<PartialState> = flow {
        val state = uiState.value
        val form = state.form ?: return@flow
        emit(PartialState.FormConfirmVisible(false))
        val domainDebt = debtsByNumber[form.debt.debitNumber]
        if (domainDebt == null) {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }

        emit(PartialState.FormSubmittingChanged(true))
        val result = saveDebitObjection(
            DebitObjectionRequestDN(
                workshopId = state.workshopId,
                branchCode = state.branchCode,
                debt = domainDebt,
                description = form.description,
                documents = form.attachments.map {
                    ObjectionDocumentDN(it.guid, it.type.code)
                },
                deposit = form.isDeposit,
            ),
        )
        emit(PartialState.FormChanged(null))
        sendEvent(ObjectionableDebitEvent.ObjectionFiled(result.referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.FormSubmittingChanged(false))
        emit(reportFailure(it))
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

        is PartialState.FormChanged -> currentState.copy(form = partialState.form)
        is PartialState.FormDebtOpenChanged -> currentState.editForm {
            copy(isDebtOpen = partialState.isOpen)
        }

        is PartialState.FormDescriptionChanged -> currentState.editForm {
            copy(description = partialState.text)
        }

        is PartialState.FormDepositChanged -> currentState.editForm {
            copy(isDeposit = partialState.isDeposit)
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

        is PartialState.FormConfirmVisible -> currentState.editForm {
            copy(isConfirmVisible = partialState.isVisible)
        }
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
            sendEvent(ObjectionableDebitEvent.ShowServerMessage(message))
        }
        return PartialState.Error(message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/** Applies [edit] to the open form, or does nothing when no form is open. */
private inline fun ObjectionableDebitUiState.editForm(
    edit: ObjectionFormState.() -> ObjectionFormState,
): ObjectionableDebitUiState = copy(form = form?.edit())

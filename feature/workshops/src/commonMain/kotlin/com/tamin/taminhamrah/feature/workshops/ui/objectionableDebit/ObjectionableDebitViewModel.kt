package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormDocument
import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.ObjectionDocumentDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveDebitObjectionUseCase
import com.tamin.taminhamrah.useCases.workshops.CheckObjectionDeadlineUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetObjectionableDebitsUseCase
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_expired
import taminx.core.core_ui.workshop_error_receive_data

/** اعتراض به بدهی. */
class ObjectionableDebitViewModel(
    private val getObjectionableDebits: GetObjectionableDebitsUseCase,
    private val checkObjectionDeadline: CheckObjectionDeadlineUseCase,
    private val getDebitObjectionPdf: GetDebitObjectionPdfUseCase,
    private val uploadImage: UploadImageUseCase,
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
    private var debtsByNumber: Map<String, WorkShopDebtDN> = emptyMap()

    override fun handleIntent(intent: ObjectionableDebitIntent): Flow<PartialState> = when (intent) {
        is ObjectionableDebitIntent.Open -> open(intent)
        ObjectionableDebitIntent.LoadMore -> loadMore()
        ObjectionableDebitIntent.Retry -> loadPage(page = 0)
        is ObjectionableDebitIntent.RowAction -> rowAction(intent.debt)
        ObjectionableDebitIntent.DismissViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }

        ObjectionableDebitIntent.FormDismissed -> flow { emit(PartialState.FormChanged(null)) }
        is ObjectionableDebitIntent.FormDebtOpenChanged ->
            editForm { copy(isDebtOpen = intent.isOpen) }

        is ObjectionableDebitIntent.FormDescriptionChanged ->
            editForm { copy(description = intent.text) }

        is ObjectionableDebitIntent.FormDepositChanged ->
            editForm { copy(isDeposit = intent.isDeposit) }

        is ObjectionableDebitIntent.FormConfirmedChanged ->
            editForm { copy(isConfirmed = intent.isConfirmed, hasTriedSubmit = false) }

        is ObjectionableDebitIntent.FormAddDocument -> addDocument(intent)
        is ObjectionableDebitIntent.FormRemoveDocument -> editForm {
            copy(
                documents = documents.removeAt(intent.index),
                uploaded = uploaded.removeAt(intent.index),
            )
        }

        ObjectionableDebitIntent.FormSubmit -> submitObjection()
    }

    /** One-line edits of the open form, which is most of what it does. */
    private fun editForm(edit: ObjectionFormState.() -> ObjectionFormState) =
        flow { emit(PartialState.FormEdited(edit)) }

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
            emit(PartialState.FormChanged(ObjectionFormState(debt = debt)))
        } else {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.objection_expired))
        }
    }.catch {
        emit(PartialState.Checking(null))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    /**
     * Sends the picked file up and keeps only the guid that comes back.
     *
     * The upload is what takes the time, so the box shows a spinner for it alone — the file only
     * joins the list once the service has actually taken it.
     */
    private fun addDocument(
        intent: ObjectionableDebitIntent.FormAddDocument,
    ): Flow<PartialState> = flow<PartialState> {
        emit(PartialState.FormEdited { copy(isUploading = true) })
        val guid = uploadImage(
            UploadImageRequestDN(fileName = intent.fileName, bytes = intent.bytes),
        ).first()
        val type = ObjectionDocumentTypes.first { it.code == intent.typeCode }
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
                    uploaded = uploaded.add(UploadedDocument(guid, intent.typeCode)),
                )
            },
        )
    }.catch {
        emit(PartialState.FormEdited { copy(isUploading = false) })
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    /**
     * Files the objection, once every rule the form states is actually satisfied.
     *
     * The guard runs here rather than in the screen so that a form which is not ready simply
     * reveals why, and cannot be submitted by any other path either.
     */
    private fun submitObjection(): Flow<PartialState> = flow {
        val state = uiState.value
        val form = state.form ?: return@flow
        if (form.documents.isEmpty() || !form.isConfirmed) {
            emit(PartialState.FormEdited { copy(hasTriedSubmit = true) })
            return@flow
        }
        val domainDebt = debtsByNumber[form.debt.debitNumber]
        if (domainDebt == null) {
            sendEvent(ObjectionableDebitEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }

        emit(PartialState.FormEdited { copy(isSubmitting = true) })
        val result = saveDebitObjection(
            DebitObjectionRequestDN(
                workshopId = state.workshopId,
                branchCode = state.branchCode,
                debt = domainDebt,
                description = form.description,
                documents = form.uploaded.map { ObjectionDocumentDN(it.guid, it.typeCode) },
                deposit = form.isDeposit,
            ),
        )
        emit(PartialState.FormChanged(null))
        sendEvent(ObjectionableDebitEvent.ObjectionFiled(result.referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.FormEdited { copy(isSubmitting = false) })
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

        is PartialState.FormChanged -> currentState.copy(form = partialState.form)
        is PartialState.FormEdited -> currentState.copy(
            form = currentState.form?.let(partialState.edit),
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

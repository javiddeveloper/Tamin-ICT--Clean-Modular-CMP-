package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractFilter
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsEvent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsUiState
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerSearchDraft
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.ComputationalBaseKeys
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.DocumentFailure
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.DocumentPreview
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.AssignerContractQuery
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.ComputationalBaseQuery
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.errorHandling.asTaminApiException
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.userRequest.DownloadUserRequestDocumentUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAssignerContractsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetComputationalBasePdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetComputationalBasesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.StringResource

/**
 * واگذارندگان — the پیمان‌ها the signed-in employer is the واگذارنده of, and what hangs off each.
 *
 * One ViewModel for four destinations, resolved graph-scoped so all four share this instance. The
 * list response already carries everything جزئیات پیمان draws, so that drill-down costs no request
 * — only مبانی محاسباتی and the documents fetch again.
 *
 * @param downloadDocumentImage the shared `upload-image/{guid}/0/0` fetch. It is named for user
 *   requests because that is where it was first needed; it is the one route every attachment in
 *   the app comes through, and duplicating the chain to rename it would be worse than the mismatch.
 */
class AssignerContractsViewModel(
    private val getContracts: GetAssignerContractsUseCase,
    private val getBases: GetComputationalBasesUseCase,
    private val getBasePdf: GetComputationalBasePdfUseCase,
    private val downloadDocumentImage: DownloadUserRequestDocumentUseCase,
    private val getMyWorkshops: GetEmployerAgreementsUseCase,
) : BaseViewModel<AssignerContractsUiState, PartialState, AssignerContractsEvent, AssignerContractsIntent>(
    initialState = AssignerContractsUiState()
) {

    override fun handleIntent(intent: AssignerContractsIntent): Flow<PartialState> = when (intent) {
        is AssignerContractsIntent.Open -> open(intent)
        AssignerContractsIntent.LoadMore -> loadMore()
        AssignerContractsIntent.Retry -> loadPage(page = 0)
        is AssignerContractsIntent.SearchOpenChanged -> setSearchOpen(intent.isOpen)

        is AssignerContractsIntent.DraftWorkshopIdChanged -> flow {
            emit(PartialState.DraftChanged(workshopId = intent.value))
            // The message clears the moment the field it names is edited.
            emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        }

        is AssignerContractsIntent.DraftBranchCodeChanged ->
            flow { emit(PartialState.DraftChanged(branchCode = intent.value)) }

        is AssignerContractsIntent.DraftContractRowChanged ->
            flow { emit(PartialState.DraftChanged(contractRow = intent.value)) }

        is AssignerContractsIntent.QuickPicked -> flow {
            emit(
                PartialState.DraftChanged(
                    workshopId = intent.workshopId,
                    branchCode = intent.branchCode,
                )
            )
            emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        }

        AssignerContractsIntent.ApplySearch -> applySearch()
        AssignerContractsIntent.ClearSearch -> flow {
            emit(PartialState.Cleared)
            emit(PartialState.SearchOpenChanged(isOpen = false))
        }

        is AssignerContractsIntent.OpenBases -> openBases(intent.keys)
        AssignerContractsIntent.LoadMoreBases -> loadMoreBases()
        AssignerContractsIntent.RetryBases -> uiState.value.basesKeys
            ?.let { loadBasesPage(page = 0, keys = it) }
            ?: flow { }

        is AssignerContractsIntent.DocumentTapped -> openDocument(
            documentId = intent.document.documentId,
            title = intent.document.category.title,
            kind = intent.document.kind,
        )

        // The open preview already carries the three things a refetch needs, so a retry does not
        // have to find the document again.
        AssignerContractsIntent.RetryDocument -> uiState.value.preview
            ?.let { openDocument(it.documentId, it.title, it.kind) }
            ?: flow { }

        AssignerContractsIntent.PreviewDismissed -> flow {
            emit(PartialState.PreviewChanged(null))
            emit(PartialState.DocumentOpening(null))
        }
    }

    // ------------------------------------------------------------------------------ the list

    /**
     * Opened with a workshop — the drill-down from جزئیات کارگاه — searches for it straight away.
     * Opened without one — the services grid — has nothing to fetch, so it raises the search sheet
     * rather than showing an empty list the user has no way to read as "search for a workshop".
     *
     * Re-opening on a filter already in state does not refetch: coming back from جزئیات پیمان keeps
     * the page and the scroll position the user left behind.
     */
    private fun open(intent: AssignerContractsIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (intent.workshopId.isBlank()) {
            if (state.filter == null) emitAll(setSearchOpen(isOpen = true))
            return@flow
        }
        val filter = AssignerContractFilter(
            workshopId = intent.workshopId,
            branchCode = intent.branchCode,
        )
        if (state.filter == filter) return@flow
        emit(PartialState.DraftChanged(intent.workshopId, intent.branchCode, contractRow = ""))
        emit(PartialState.Applied(filter))
        emitAll(loadPage(page = 0, filter = filter))
    }

    private fun loadPage(
        page: Int,
        filter: AssignerContractFilter? = uiState.value.filter,
    ): Flow<PartialState> = flow {
        if (filter == null) return@flow
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getContracts(
            AssignerContractQuery(
                workshopId = filter.workshopId,
                // Blank is dropped from the filter array by the repository, which *widens* the
                // search — unlike ردیف‌های پیمان, where a blank addresses a route that 404s.
                branchCode = filter.branchCode.ifBlank { null },
                contractRow = filter.contractRow.ifBlank { null },
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

    /**
     * Opening the sheet seeds it from what is already applied, so re-opening edits the live search
     * rather than starting from blank, and fetches کارگاه‌های شما the first time only.
     */
    private fun setSearchOpen(isOpen: Boolean): Flow<PartialState> = flow {
        emit(PartialState.SearchOpenChanged(isOpen))
        if (!isOpen) return@flow

        val state = uiState.value
        state.filter?.let {
            emit(PartialState.DraftChanged(it.workshopId, it.branchCode, it.contractRow))
        }
        emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        if (state.myWorkshops.isNotEmpty()) return@flow

        val workshops = getMyWorkshops(WorkshopListQuery(page = 0))
        emit(
            PartialState.MyWorkshopsLoaded(
                workshops.items
                    .map { it.toPresentation() }
                    // One کارگاه reaches this list once per agreement it holds, so the same
                    // workshop arrives two or three times. Keyed on the identity the pick actually
                    // uses, not on the whole card.
                    .distinctBy { it.workshopId to it.branchCode }
                    .toImmutableList(),
                total = workshops.total,
            )
        )
    }.catch {
        // کارگاه‌های شما is a convenience above three fields that already work. Failing to fetch it
        // must not put an error on the list the user has not asked for yet, so it is swallowed and
        // the section simply does not appear.
        emit(
            PartialState.MyWorkshopsLoaded(
                uiState.value.myWorkshops,
                uiState.value.myWorkshopsTotal,
            )
        )
    }

    /**
     * کد کارگاه is the one required field.
     *
     * Not because the service demands it — all three travel as filter clauses and a blank one is
     * simply omitted — but because the design does: without it the screen would open onto every
     * پیمان the employer holds, which is what its two worded-apart empty states exist to prevent.
     */
    private fun applySearch(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.list.isLoading) return@flow

        val draft = state.draft
        val workshopId = draft.workshopId.trim()
        emit(PartialState.WorkshopIdErrorChanged(isVisible = workshopId.isBlank()))
        if (workshopId.isBlank()) return@flow

        val filter = AssignerContractFilter(
            workshopId = workshopId,
            branchCode = draft.branchCode.trim(),
            contractRow = draft.contractRow.trim(),
        )
        emit(PartialState.Applied(filter))
        emit(PartialState.SearchOpenChanged(isOpen = false))
        emitAll(loadPage(page = 0, filter = filter))
    }

    // ---------------------------------------------------------------------- مبانی محاسباتی

    /**
     * The bases screen asks for its own پیمان on every entry; only a different one refetches.
     *
     * Its route carries all four keys, so this survives process death without depending on
     * [AssignerContractsUiState.selected] having been set by a tap that happened in a previous
     * process.
     */
    private fun openBases(keys: ComputationalBaseKeys): Flow<PartialState> {
        if (uiState.value.basesKeys == keys) return flow { }
        return loadBasesPage(page = 0, keys = keys)
    }

    private fun loadBasesPage(
        page: Int,
        keys: ComputationalBaseKeys,
    ): Flow<PartialState> = flow {
        emit(if (page == 0) PartialState.BasesLoading(keys) else PartialState.BasesLoadingMore)
        val result = getBases(
            ComputationalBaseQuery(
                workshopId = keys.workshopId,
                branchCode = keys.branchCode,
                contractRow = keys.contractRow,
                contractSequence = keys.contractSequence,
                page = page,
            )
        )
        // Page 0 replaces because `BasesLoading` already cleared the previous پیمان's rows.
        val previous = if (page == 0) PagedListState() else uiState.value.bases
        emit(
            PartialState.BasesLoaded(
                previous.loaded(result, isFirstPage = page == 0) { it.toPresentation() },
                keys,
            )
        )
    }.catch { emit(PartialState.BasesError(it.toSingleLineMessage(), keys)) }

    private fun loadMoreBases(): Flow<PartialState> {
        val state = uiState.value
        val keys = state.basesKeys ?: return flow { }
        if (!state.bases.canLoadMore) return flow { }
        return loadBasesPage(page = state.bases.nextPage, keys = keys)
    }

    // ---------------------------------------------------------------------------- documents

    /**
     * Opens one attachment through whichever of the two routes its kind names.
     *
     * Both kinds are staged identically: the viewer goes up first with nothing in it, fills in
     * when the bytes land, and carries the failure when they do not. That is what `TaminPdfViewer`
     * already did, and `TaminImageViewer` now does too — so neither kind leaves the tap looking
     * like it did nothing while a download runs.
     *
     * A failure also lands on the **row**, carrying the service's own words, so it survives
     * dismissing the viewer and names which of several attachments is unavailable. `upload-image`
     * answers a missing document with «داده ای با اطلاعات شناسه … یافت نشد.», which says far more
     * than any line this screen could write, and `BaseDTO.rawErrorText` already lifts it onto the
     * exception. Tapping the row again clears the failure and retries.
     */
    private fun openDocument(
        documentId: String,
        title: StringResource,
        kind: BaseDocumentKind,
    ): Flow<PartialState> {
        // Built out here so the `catch` below can reach it. Reading it back off `uiState` there
        // does not work: the emissions above are reduced downstream, so the pending preview is
        // not in state yet when the failure arrives.
        val pending = DocumentPreview(documentId = documentId, title = title, kind = kind)
        return flow {
        if (documentId.isBlank() || uiState.value.openingDocumentId != null) return@flow

        emit(PartialState.DocumentOpening(documentId))
        // A retry starts clean: the previous reason must not sit under a row that is trying again.
        emit(PartialState.DocumentFailed(null))
        // Up front, empty. On a retry this is also what clears the viewer's failure state and
        // puts its wait back.
        emit(PartialState.PreviewChanged(pending))

        when (kind) {
            BaseDocumentKind.IMAGE -> {
                // Base64 from the shared upload-image route, handed straight to the async image
                // loader — the same path درخواست‌های من takes for its attachments.
                val data = downloadDocumentImage(documentId)
                // A service that answered with nothing is a failure, not an empty document. It
                // said nothing about why, so both the viewer and the row fall back to their
                // generic line.
                if (data.isBlank()) {
                    emit(PartialState.PreviewChanged(pending.copy(didFail = true)))
                    // Answered, with nothing in it: the document is not there.
                    emit(
                        PartialState.DocumentFailed(
                            DocumentFailure(documentId, message = null, isMissing = true)
                        )
                    )
                } else {
                    emit(PartialState.PreviewChanged(pending.copy(imageData = data)))
                }
            }

            BaseDocumentKind.PDF -> {
                val pdf = getBasePdf(documentId).toPresentation()
                if (pdf.pdf == null) {
                    emit(PartialState.PreviewChanged(pending.copy(didFail = true)))
                    emit(
                        PartialState.DocumentFailed(
                            DocumentFailure(documentId, message = null, isMissing = true)
                        )
                    )
                } else {
                    emit(PartialState.PreviewChanged(pending.copy(pdf = pdf)))
                }
            }
        }
        emit(PartialState.DocumentOpening(null))
        }.catch { error ->
            // The service's own reason, not ours. The viewer keeps its place and carries the
            // failure, because closing it silently is indistinguishable from the tap having done
            // nothing, and the row keeps it too so it survives the viewer being dismissed.
            emit(PartialState.PreviewChanged(pending.copy(didFail = true)))
            emit(
                PartialState.DocumentFailed(
                    DocumentFailure(
                        documentId = documentId,
                        message = error.toSingleLineMessage(),
                        // A document the service says is not there will not appear on a second
                        // ask; anything else might, so only this one closes the row.
                        isMissing = error.isResourceMissing(),
                    )
                )
            )
            emit(PartialState.DocumentOpening(null))
        }
    }

    // ------------------------------------------------------------------------------ reducer

    override fun reduceState(
        currentState: AssignerContractsUiState,
        partialState: PartialState,
    ): AssignerContractsUiState = when (partialState) {
        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.Error ->
            currentState.copy(list = currentState.list.failed(partialState.message))

        is PartialState.Applied -> currentState.copy(filter = partialState.filter)
        PartialState.Cleared -> currentState.copy(
            filter = null,
            draft = AssignerSearchDraft(),
            list = PagedListState(),
        )

        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.DraftChanged -> currentState.copy(
            draft = currentState.draft.copy(
                workshopId = partialState.workshopId ?: currentState.draft.workshopId,
                branchCode = partialState.branchCode ?: currentState.draft.branchCode,
                contractRow = partialState.contractRow ?: currentState.draft.contractRow,
            )
        )

        is PartialState.WorkshopIdErrorChanged -> currentState.copy(
            draft = currentState.draft.copy(showWorkshopIdError = partialState.isVisible)
        )

        is PartialState.MyWorkshopsLoaded -> currentState.copy(
            myWorkshops = partialState.workshops,
            myWorkshopsTotal = partialState.total,
        )

        // The keys move with the request, so a page arriving for a پیمان the user has left can be
        // told apart from one for the پیمان on screen.
        is PartialState.BasesLoading -> currentState.copy(
            basesKeys = partialState.keys,
            bases = PagedListState(isLoading = true),
        )

        PartialState.BasesLoadingMore -> currentState.copy(bases = currentState.bases.loadingMore())
        is PartialState.BasesLoaded ->
            if (partialState.keys == currentState.basesKeys) {
                currentState.copy(bases = partialState.list)
            } else {
                currentState
            }

        is PartialState.BasesError ->
            if (partialState.keys == null || partialState.keys == currentState.basesKeys) {
                currentState.copy(bases = currentState.bases.failed(partialState.message))
            } else {
                currentState
            }

        is PartialState.DocumentOpening ->
            currentState.copy(openingDocumentId = partialState.documentId)

        is PartialState.PreviewChanged -> currentState.copy(preview = partialState.preview)
        is PartialState.DocumentFailed ->
            currentState.copy(documentFailure = partialState.failure)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/**
 * Whether the service said the thing simply is not there.
 *
 * Read off the parsed `ErrorUri` rather than the message text, which is localized copy and would
 * make this a string comparison against words a translator can change. Null-safe on purpose: a
 * throwable that never reached the parser has no uri, and "unknown" must not read as "missing".
 */
private fun Throwable.isResourceMissing(): Boolean =
    (asTaminApiException().cause as? TaminErrorUriException)?.uri == ErrorUri.RESOURCE_NOT_FOUND

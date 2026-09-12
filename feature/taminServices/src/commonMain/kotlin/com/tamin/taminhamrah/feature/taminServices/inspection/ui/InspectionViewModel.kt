package com.tamin.taminhamrah.feature.taminServices.inspection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.IdentityContactStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionEvent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.mapper.toPR
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.inspection.GetBranchPageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInspectionReportPDFUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInsurancePageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobPageUseCase
import com.tamin.taminhamrah.useCases.inspection.SubmitInspectionUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class InspectionViewModel(
    private val getInsurancePageUseCase: GetInsurancePageUseCase,
    private val getBranchPageUseCase: GetBranchPageUseCase,
    private val getJobPageUseCase: GetJobPageUseCase,
    private val submitInspectionUseCase: SubmitInspectionUseCase,
    private val getInspectionReportPDFUseCase: GetInspectionReportPDFUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : BaseViewModel<InspectionUiState, PartialState, InspectionEvent, InspectionIntent>(
    initialState = InspectionUiState()
) {

    // One offset paginator per list endpoint (see docs/vault/Pagination.md). The branch/job
    // paginators stay idle at their default empty state until the request wizard opens and
    // refreshes them; the inspection paginator loads its first page on init.
    private val inspectionPaginator = Paginator(
        loadPage = { query -> getInsurancePageUseCase(query).first() },
    )
    private val branchPaginator = Paginator(
        loadPage = { query -> getBranchPageUseCase(query).first() },
    )
    private val jobPaginator = Paginator(
        loadPage = { query -> getJobPageUseCase(query).first() },
    )

    init {
        sendIntent(InspectionIntent.LoadInspections)
    }

    override fun handleIntent(intent: InspectionIntent): Flow<PartialState> {
        return when (intent) {
            is InspectionIntent.LoadInspections -> handleLoadInspections()
            is InspectionIntent.LoadNextInspections -> flow { inspectionPaginator.loadNext() }
            is InspectionIntent.RetryNextInspections -> flow { inspectionPaginator.retry() }

            is InspectionIntent.LoadNextBranches -> flow { branchPaginator.loadNext() }
            is InspectionIntent.RetryNextBranches -> flow { branchPaginator.retry() }
            is InspectionIntent.SearchBranches -> flow {
                emit(PartialState.BranchQueryChanged(intent.query))
                branchPaginator.refresh(branchBaseQuery(intent.query))
            }

            is InspectionIntent.LoadNextJobs -> flow { jobPaginator.loadNext() }
            is InspectionIntent.RetryNextJobs -> flow { jobPaginator.retry() }
            is InspectionIntent.SearchJobs -> flow {
                emit(PartialState.JobQueryChanged(intent.query))
                jobPaginator.refresh(jobBaseQuery(intent.query))
            }

            is InspectionIntent.SubmitRequest -> handleSubmitRequest(intent)
            is InspectionIntent.DownloadReportPdf -> handleDownloadReportPdf(intent)
            is InspectionIntent.DismissPdfViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
            is InspectionIntent.OpenRequestFlow -> handleOpenRequestFlow(intent)
            is InspectionIntent.CloseRequestFlow -> flow { emit(PartialState.RequestFlowClosed) }
            is InspectionIntent.RetrySource -> handleRetrySource(intent.source)
                .onStart { emit(PartialState.Loading(true)) }
                .onCompletion { emit(PartialState.Loading(false)) }
            is InspectionIntent.UpdateIdentityContact ->
                flow { emit(PartialState.IdentityContactUpdated(intent.identityContact)) }
            is InspectionIntent.UpdateWorkshopInfo ->
                flow { emit(PartialState.WorkshopInfoUpdated(intent.workshopInfo)) }
            is InspectionIntent.UpdateRequestDescription ->
                flow { emit(PartialState.RequestDescriptionUpdated(intent.description)) }
            is InspectionIntent.GoToNextRequestStep -> flow { emit(PartialState.GoToNextRequestStep) }
            is InspectionIntent.GoToPreviousRequestStep -> handleGoToPreviousRequestStep()
            is InspectionIntent.SetExitConfirmationVisible ->
                flow { emit(PartialState.ExitConfirmationChanged(intent.visible)) }
        }
    }

    /**
     * Long-lived intent: subscribes the three paginators' state to the UI (mirrors
     * `MyInboxViewModel.handleLoadInbox`) and kicks off the inspection list's first page.
     * Branch/job observers are wired here too so the wizard only has to call `refresh()` —
     * it never re-subscribes.
     */
    private fun handleLoadInspections(): Flow<PartialState> = merge(
        observeInspectionPaging(),
        observeBranchPaging(),
        observeJobPaging(),
        flow { inspectionPaginator.loadNext() },
    )

    private fun observeInspectionPaging(): Flow<PartialState> = inspectionPaginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(InspectionEvent.ShowToast(errorMessage))
        }
        PartialState.InspectionsPagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = errorMessage,
        )
    }

    private fun observeBranchPaging(): Flow<PartialState> = branchPaginator.state.map { paging ->
        PartialState.BranchesPagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = paging.error?.toSingleLineMessage(),
        )
    }

    private fun observeJobPaging(): Flow<PartialState> = jobPaginator.state.map { paging ->
        PartialState.JobsPagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = paging.error?.toSingleLineMessage(),
        )
    }

    /** `type=1` + `status=1` are always sent; a non-blank [query] adds a `name LIKE "*query*"` filter. */
    private fun branchBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = buildList {
            add(ApiFilterDN(FilterProperty.TYPE, "1", FilterOperator.EQUAL))
            add(ApiFilterDN(FilterProperty.STATUS, "1", FilterOperator.EQUAL))
            query.trim().takeIf { it.isNotEmpty() }?.let {
                add(ApiFilterDN(FilterProperty.NAME, "*$it*", FilterOperator.LIKE))
            }
        }
    )

    /** `jobDescription LIKE "*query*"`, or `LIKE "*"` (match all) when [query] is blank. */
    private fun jobBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = listOf(
            ApiFilterDN(
                FilterProperty.JOB_DESCRIPTION,
                query.trim().takeIf { it.isNotEmpty() }?.let { "*$it*" } ?: "*",
                FilterOperator.LIKE,
            )
        )
    )

    /**
     * Fires current-user (Step1) and branch+job (Step2) requests together on entry, per product
     * decision to prefetch the whole wizard's data up front rather than per-step. Each call's
     * failure blocks only the step it feeds with a fatal, retryable error tagged to that specific
     * call — Step1 via [InspectionRequestErrorSource.USER_INFO], Step2's branch/job pickers via
     * [InspectionRequestErrorSource.BRANCHES]/[InspectionRequestErrorSource.JOBS] independently
     * (each picker has its own paginator), so retrying one never re-triggers the other.
     */
    private fun handleOpenRequestFlow(intent: InspectionIntent.OpenRequestFlow): Flow<PartialState> = merge(
        flow { emit(PartialState.RequestFlowOpened(intent.item)) },
        fetchUserProfileForRequest(),
        flow { branchPaginator.refresh(branchBaseQuery("")) },
        flow { jobPaginator.refresh(jobBaseQuery("")) },
    ).onStart {
        emit(PartialState.ClearRequestErrors)
        emit(PartialState.BranchQueryChanged(""))
        emit(PartialState.JobQueryChanged(""))
        emit(PartialState.Loading(true))
    }.onCompletion {
        emit(PartialState.Loading(false))
    }

    private fun handleRetrySource(source: InspectionRequestErrorSource): Flow<PartialState> = when (source) {
        InspectionRequestErrorSource.USER_INFO -> fetchUserProfileForRequest()
        InspectionRequestErrorSource.BRANCHES -> flow { branchPaginator.retry() }
        InspectionRequestErrorSource.JOBS -> flow { jobPaginator.retry() }
    }.onStart {
        // Optimistic clear here rather than in the success reducer: branches and jobs load
        // independently, so only the call actually being retried should have its error cleared.
        emit(PartialState.RequestErrorCleared(source))
    }

    private fun fetchUserProfileForRequest(): Flow<PartialState> = flow {
        emitAll(
            getUserProfileUseCase().map { profile ->
                PartialState.IdentityContactUpdated(
                    IdentityContactStepState(
                        fullName = listOfNotNull(profile.firstName, profile.lastName)
                            .joinToString(separator = " "),
                        nationalCode = profile.nationalCode.orEmpty(),
                        mobile = profile.mobile.orEmpty(),
                    )
                ) as PartialState
            }
        )
    }.catch { e -> emit(PartialState.RequestError(e.toSingleLineMessage(), InspectionRequestErrorSource.USER_INFO)) }

    private fun handleGoToPreviousRequestStep(): Flow<PartialState> = flow {
        if (uiState.value.requestStep == InspectionRequestStep.IDENTITY_CONTACT) {
            emit(PartialState.RequestFlowClosed)
        } else {
            emit(PartialState.GoToPreviousRequestStep)
        }
    }

    private fun handleSubmitRequest(intent: InspectionIntent.SubmitRequest): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = submitInspectionUseCase(intent.request)
            emit(PartialState.SubmitSuccess(result.id))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleDownloadReportPdf(intent: InspectionIntent.DownloadReportPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            val pdf = getInspectionReportPDFUseCase(intent.inspectionNo)
            emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            emit(PartialState.ViewerDownloadFailed)
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: InspectionUiState,
        partialState: PartialState
    ): InspectionUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.InspectionsPagingChanged -> currentState.copy(
            inspections = partialState.items,
            isLoadingInspections = partialState.isLoadingFirstPage,
            isLoadingNextInspections = partialState.isLoadingNextPage,
            inspectionsEndReached = partialState.endReached,
            inspectionsPagingError = partialState.error,
        )

        is PartialState.BranchesPagingChanged -> currentState.copy(
            branches = partialState.items,
            isLoadingBranches = partialState.isLoadingFirstPage,
            isLoadingNextBranches = partialState.isLoadingNextPage,
            branchesEndReached = partialState.endReached,
            branchesPagingError = partialState.error,
            requestErrors = tagPickerError(
                currentState.requestErrors,
                InspectionRequestErrorSource.BRANCHES,
                partialState.error,
                partialState.items.isEmpty(),
            ),
        )

        is PartialState.JobsPagingChanged -> currentState.copy(
            jobs = partialState.items,
            isLoadingJobs = partialState.isLoadingFirstPage,
            isLoadingNextJobs = partialState.isLoadingNextPage,
            jobsEndReached = partialState.endReached,
            jobsPagingError = partialState.error,
            requestErrors = tagPickerError(
                currentState.requestErrors,
                InspectionRequestErrorSource.JOBS,
                partialState.error,
                partialState.items.isEmpty(),
            ),
        )

        is PartialState.BranchQueryChanged -> currentState.copy(branchQuery = partialState.query)
        is PartialState.JobQueryChanged -> currentState.copy(jobQuery = partialState.query)

        is PartialState.SubmitSuccess -> currentState.copy(
            isLoading = false,
            isSubmitted = true,
            submittedTrackingId = partialState.id,
        )
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isLoading = false,
            viewerPdf = partialState.pdf,
            viewerDownloadFailed = false,
        )
        is PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
        is PartialState.RequestFlowOpened -> {
            val item = partialState.item
            currentState.copy(
                showRequestFlow = true,
                requestInspectionNo = item?.inspectionNo,
                requestInsuranceNo = item?.insuranceNo,
                isObjectionRequest = item != null,
                requestStep = InspectionRequestStep.IDENTITY_CONTACT,
                // Objection flow prefills what the card already knows (workshop + branch) so the
                // user doesn't retype it; the rest of Step 2 (job, dates, address) is still theirs to fill.
                workshopInfo = WorkshopInfoStepState(
                    workshopName = item?.workshopName.orEmpty(),
                    workshopCode = item?.workshopNo.orEmpty(),
                    branchCode = item?.branchCode.orEmpty(),
                    branchName = item?.branchdesc.orEmpty(),
                ),
                requestDescription = "",
                isSubmitted = false,
                submittedTrackingId = null,
                requestErrors = emptyMap(),
                showExitConfirmation = false,
            )
        }
        is PartialState.RequestFlowClosed -> currentState.copy(
            showRequestFlow = false,
            requestStep = InspectionRequestStep.IDENTITY_CONTACT,
            showExitConfirmation = false,
        )
        is PartialState.ClearRequestErrors -> currentState.copy(requestErrors = emptyMap())
        is PartialState.RequestError -> currentState.copy(
            requestErrors = currentState.requestErrors + (partialState.source to partialState.message)
        )
        is PartialState.IdentityContactUpdated -> currentState.copy(
            identityContact = partialState.identityContact,
            requestErrors = currentState.requestErrors - InspectionRequestErrorSource.USER_INFO,
        )
        is PartialState.WorkshopInfoUpdated -> currentState.copy(workshopInfo = partialState.workshopInfo)
        is PartialState.RequestDescriptionUpdated -> currentState.copy(requestDescription = partialState.description)
        is PartialState.GoToNextRequestStep -> {
            val next = InspectionRequestStep.entries
                .getOrElse(currentState.requestStep.ordinal + 1) { currentState.requestStep }
            currentState.copy(requestStep = next)
        }
        is PartialState.GoToPreviousRequestStep -> {
            val previous = InspectionRequestStep.entries
                .getOrElse(currentState.requestStep.ordinal - 1) { currentState.requestStep }
            currentState.copy(requestStep = previous)
        }
        is PartialState.ExitConfirmationChanged -> currentState.copy(showExitConfirmation = partialState.show)
        is PartialState.RequestErrorCleared -> currentState.copy(requestErrors = currentState.requestErrors - partialState.source)
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(InspectionEvent.ShowToast(message))
        return PartialState.Loading(false)
    }

    /** A picker's first-page failure with nothing to show blocks Step 2; anything else clears its tag. */
    private fun tagPickerError(
        current: Map<InspectionRequestErrorSource, String>,
        source: InspectionRequestErrorSource,
        error: String?,
        itemsEmpty: Boolean,
    ): Map<InspectionRequestErrorSource, String> =
        if (error != null && itemsEmpty) current + (source to error) else current - source
}

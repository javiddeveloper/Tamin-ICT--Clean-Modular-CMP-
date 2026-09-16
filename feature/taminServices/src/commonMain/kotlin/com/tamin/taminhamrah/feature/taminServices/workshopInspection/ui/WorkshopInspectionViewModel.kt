package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.IdentityContactStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.mapper.toPR
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionEvent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionFilter
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.inspection.GetInspectionReportPDFUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobPageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetWorkshopInspectionsPageUseCase
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

class WorkshopInspectionViewModel(
    private val getWorkshopInspectionsPageUseCase: GetWorkshopInspectionsPageUseCase,
    private val getJobPageUseCase: GetJobPageUseCase,
    private val submitInspectionUseCase: SubmitInspectionUseCase,
    private val getInspectionReportPDFUseCase: GetInspectionReportPDFUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : BaseViewModel<WorkshopInspectionUiState, PartialState, WorkshopInspectionEvent, WorkshopInspectionIntent>(
    initialState = WorkshopInspectionUiState()
) {

    private val inspectionPaginator = Paginator(
        loadPage = { query -> getWorkshopInspectionsPageUseCase(query).first() },
    )
    private val jobPaginator = Paginator(
        loadPage = { query -> getJobPageUseCase(query).first() },
    )

    init {
        sendIntent(WorkshopInspectionIntent.LoadInspections)
    }

    override fun handleIntent(intent: WorkshopInspectionIntent): Flow<PartialState> {
        return when (intent) {
            is WorkshopInspectionIntent.LoadInspections -> handleLoadInspections()
            is WorkshopInspectionIntent.LoadNextInspections -> flow { inspectionPaginator.loadNext() }
            is WorkshopInspectionIntent.RetryNextInspections -> flow { inspectionPaginator.retry() }

            is WorkshopInspectionIntent.LoadNextJobs -> flow { jobPaginator.loadNext() }
            is WorkshopInspectionIntent.RetryNextJobs -> flow { jobPaginator.retry() }
            is WorkshopInspectionIntent.SearchJobs -> flow {
                emit(PartialState.JobQueryChanged(intent.query))
                jobPaginator.refresh(jobBaseQuery(intent.query))
            }

            is WorkshopInspectionIntent.SubmitRequest -> handleSubmitRequest(intent)
            is WorkshopInspectionIntent.DownloadReportPdf -> handleDownloadReportPdf(intent)
            is WorkshopInspectionIntent.DismissPdfViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
            is WorkshopInspectionIntent.OpenRequestFlow -> handleOpenRequestFlow(intent)
            is WorkshopInspectionIntent.CloseRequestFlow -> flow { emit(PartialState.RequestFlowClosed) }
            is WorkshopInspectionIntent.RetrySource -> handleRetrySource(intent.source)
                .onStart { emit(PartialState.Loading(true)) }
                .onCompletion { emit(PartialState.Loading(false)) }
            is WorkshopInspectionIntent.UpdateIdentityContact ->
                flow { emit(PartialState.IdentityContactUpdated(intent.identityContact)) }
            is WorkshopInspectionIntent.UpdateWorkshopInfo ->
                flow { emit(PartialState.WorkshopInfoUpdated(intent.workshopInfo)) }
            is WorkshopInspectionIntent.UpdateRequestDescription ->
                flow { emit(PartialState.RequestDescriptionUpdated(intent.description)) }
            is WorkshopInspectionIntent.GoToNextRequestStep -> flow { emit(PartialState.GoToNextRequestStep) }
            is WorkshopInspectionIntent.GoToPreviousRequestStep -> handleGoToPreviousRequestStep()
            is WorkshopInspectionIntent.SetExitConfirmationVisible ->
                flow { emit(PartialState.ExitConfirmationChanged(intent.visible)) }

            is WorkshopInspectionIntent.OpenSearchSheet -> flow { emit(PartialState.SearchSheetOpened) }
            is WorkshopInspectionIntent.CloseSearchSheet -> flow { emit(PartialState.SearchSheetClosed) }
            is WorkshopInspectionIntent.UpdateWorkshopCodeQuery ->
                flow { emit(PartialState.WorkshopCodeQueryChanged(intent.query)) }
            is WorkshopInspectionIntent.UpdateInspectionIdQuery ->
                flow { emit(PartialState.InspectionIdQueryChanged(intent.query)) }
            is WorkshopInspectionIntent.ApplySearch -> handleApplySearch()
            is WorkshopInspectionIntent.ClearSearch -> handleClearSearch()
        }
    }

    /** Long-lived intent: subscribes both paginators' state to the UI and kicks off the first page. */
    private fun handleLoadInspections(): Flow<PartialState> = merge(
        observeInspectionPaging(),
        observeJobPaging(),
        flow { inspectionPaginator.loadNext() },
    )

    private fun observeInspectionPaging(): Flow<PartialState> = inspectionPaginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(WorkshopInspectionEvent.ShowToast(errorMessage))
        }
        PartialState.InspectionsPagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = errorMessage,
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

    private fun jobBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = listOf(
            ApiFilterDN(
                FilterProperty.JOB_DESCRIPTION,
                query.trim().takeIf { it.isNotEmpty() }?.let { "*$it*" } ?: "*",
                FilterOperator.LIKE,
            )
        )
    )

    /** Objection flow prefills Step 1 (identity) and Step 2's job picker together on entry. */
    private fun handleOpenRequestFlow(intent: WorkshopInspectionIntent.OpenRequestFlow): Flow<PartialState> = merge(
        flow { emit(PartialState.RequestFlowOpened(intent.item)) },
        fetchUserProfileForRequest(),
        flow { jobPaginator.refresh(jobBaseQuery("")) },
    ).onStart {
        emit(PartialState.ClearRequestErrors)
        emit(PartialState.JobQueryChanged(""))
        emit(PartialState.Loading(true))
    }.onCompletion {
        emit(PartialState.Loading(false))
    }

    private fun handleRetrySource(source: InspectionRequestErrorSource): Flow<PartialState> = when (source) {
        InspectionRequestErrorSource.USER_INFO -> fetchUserProfileForRequest()
        InspectionRequestErrorSource.JOBS -> flow { jobPaginator.retry() }
        // Unreachable here: this screen has no branch picker (branch is always locked/prefilled),
        // so requestErrors is never tagged with BRANCHES — kept only because the enum is shared
        // with the insured-side screen and the `when` must stay exhaustive.
        InspectionRequestErrorSource.BRANCHES -> flow { }
    }.onStart {
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

    private fun handleSubmitRequest(intent: WorkshopInspectionIntent.SubmitRequest): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = submitInspectionUseCase(intent.request)
            emit(PartialState.SubmitSuccess(result.id))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(WorkshopInspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleDownloadReportPdf(intent: WorkshopInspectionIntent.DownloadReportPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            val pdf = getInspectionReportPDFUseCase(intent.inspectionNo)
            emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            emit(PartialState.ViewerDownloadFailed)
            sendEvent(WorkshopInspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleApplySearch(): Flow<PartialState> = flow {
        val state = uiState.value
        val code = state.workshopCodeQuery.trim()
        val id = state.inspectionIdQuery.trim()
        emit(PartialState.SearchSheetClosed)
        emit(
            PartialState.FilterApplied(
                if (code.isEmpty() && id.isEmpty()) null else WorkshopInspectionFilter(code, id)
            )
        )
    }

    private fun handleClearSearch(): Flow<PartialState> = flow {
        emit(PartialState.WorkshopCodeQueryChanged(""))
        emit(PartialState.InspectionIdQueryChanged(""))
        emit(PartialState.FilterApplied(null))
        emit(PartialState.SearchSheetClosed)
    }

    override fun reduceState(
        currentState: WorkshopInspectionUiState,
        partialState: PartialState
    ): WorkshopInspectionUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.InspectionsPagingChanged -> currentState.copy(
            inspections = partialState.items,
            isLoadingInspections = partialState.isLoadingFirstPage,
            isLoadingNextInspections = partialState.isLoadingNextPage,
            inspectionsEndReached = partialState.endReached,
            inspectionsPagingError = partialState.error,
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
                requestInspectionNo = item.inspectionNo,
                requestInsuranceNo = item.insuranceNo,
                requestStep = InspectionRequestStep.IDENTITY_CONTACT,
                // Prefills what the card already knows (workshop + branch) so the user doesn't
                // retype it; the rest of Step 2 (job, dates, address) is still theirs to fill.
                workshopInfo = WorkshopInfoStepState(
                    workshopName = item.workshopName,
                    workshopCode = item.workshopNo,
                    branchCode = item.branchCode,
                    branchName = item.branchdesc,
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

        is PartialState.SearchSheetOpened -> currentState.copy(isSearchSheetOpen = true)
        is PartialState.SearchSheetClosed -> currentState.copy(isSearchSheetOpen = false)
        is PartialState.WorkshopCodeQueryChanged -> currentState.copy(workshopCodeQuery = partialState.query)
        is PartialState.InspectionIdQueryChanged -> currentState.copy(inspectionIdQuery = partialState.query)
        is PartialState.FilterApplied -> currentState.copy(appliedFilter = partialState.filter)
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(WorkshopInspectionEvent.ShowToast(message))
        return PartialState.Loading(false)
    }

    private fun tagPickerError(
        current: Map<InspectionRequestErrorSource, String>,
        source: InspectionRequestErrorSource,
        error: String?,
        itemsEmpty: Boolean,
    ): Map<InspectionRequestErrorSource, String> =
        if (error != null && itemsEmpty) current + (source to error) else current - source
}

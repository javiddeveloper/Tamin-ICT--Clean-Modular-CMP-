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
import com.tamin.taminhamrah.useCases.inspection.GetInspectionListUseCase
import com.tamin.taminhamrah.useCases.inspection.GetBranchListUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInspectionReportPDFUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobListUseCase
import com.tamin.taminhamrah.useCases.inspection.SubmitInspectionUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class InspectionViewModel(
    private val getInspectionListUseCase: GetInspectionListUseCase,
    private val getBranchListUseCase: GetBranchListUseCase,
    private val getJobListUseCase: GetJobListUseCase,
    private val submitInspectionUseCase: SubmitInspectionUseCase,
    private val getInspectionReportPDFUseCase: GetInspectionReportPDFUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : BaseViewModel<InspectionUiState, PartialState, InspectionEvent, InspectionIntent>(
    initialState = InspectionUiState()
) {

    init {
        sendIntent(InspectionIntent.LoadInspections())
    }

    override fun handleIntent(intent: InspectionIntent): Flow<PartialState> {
        return when (intent) {
            is InspectionIntent.LoadInspections -> handleLoadInspections(intent)
            is InspectionIntent.LoadBranches -> handleLoadBranches(intent)
            is InspectionIntent.LoadJobs -> handleLoadJobs(intent)
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
     * Fires current-user (Step1) and branch+job (Step2) requests together on entry, per product
     * decision to prefetch the whole wizard's data up front rather than per-step. Each call's
     * failure blocks only the step it feeds with a fatal, retryable error tagged to that specific
     * call (mirrors the occurrence wizard's per-[InspectionRequestErrorSource] error map) — Step1
     * via [InspectionRequestErrorSource.USER_INFO], Step2's branch/job pickers via
     * [InspectionRequestErrorSource.BRANCHES]/[InspectionRequestErrorSource.JOBS] independently, so
     * retrying one never re-triggers the other if it already succeeded.
     */
    private fun handleOpenRequestFlow(intent: InspectionIntent.OpenRequestFlow): Flow<PartialState> = merge(
        flow { emit(PartialState.RequestFlowOpened(intent.item)) },
        fetchUserProfileForRequest(),
        handleLoadBranches(InspectionIntent.LoadBranches()),
        handleLoadJobs(InspectionIntent.LoadJobs()),
    ).onStart {
        emit(PartialState.ClearRequestErrors)
        emit(PartialState.Loading(true))
    }.onCompletion {
        emit(PartialState.Loading(false))
    }

    private fun handleRetrySource(source: InspectionRequestErrorSource): Flow<PartialState> = when (source) {
        InspectionRequestErrorSource.USER_INFO -> fetchUserProfileForRequest()
        InspectionRequestErrorSource.BRANCHES -> handleLoadBranches(InspectionIntent.LoadBranches())
        InspectionRequestErrorSource.JOBS -> handleLoadJobs(InspectionIntent.LoadJobs())
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

    private fun handleLoadInspections(intent: InspectionIntent.LoadInspections): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = getInspectionListUseCase(filters = intent.filters)
            emit(PartialState.InspectionsLoaded(result.list.map { it.toPR() }))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleLoadBranches(intent: InspectionIntent.LoadBranches): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = getBranchListUseCase(filters = intent.filters)
            emit(PartialState.BranchesLoaded(result.list.map { it.toPR() }))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            emit(PartialState.RequestError(e.toSingleLineMessage(), InspectionRequestErrorSource.BRANCHES))
        }
    }

    private fun handleLoadJobs(intent: InspectionIntent.LoadJobs): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = getJobListUseCase(filters = intent.filters)
            emit(PartialState.JobsLoaded(result.list.map { it.toPR() }))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            emit(PartialState.RequestError(e.toSingleLineMessage(), InspectionRequestErrorSource.JOBS))
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
        is PartialState.InspectionsLoaded -> currentState.copy(isLoading = false, inspections = partialState.list)
        is PartialState.BranchesLoaded -> currentState.copy(isLoading = false, branches = partialState.list)
        is PartialState.JobsLoaded -> currentState.copy(isLoading = false, jobs = partialState.list)
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
}

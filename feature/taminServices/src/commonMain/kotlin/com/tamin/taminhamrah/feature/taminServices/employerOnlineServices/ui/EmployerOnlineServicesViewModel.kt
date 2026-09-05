package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestStep
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.ContractRowsUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesEvent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toAgreementDocumentPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toIdentityCardPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toRowPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementSearch
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.paging.PaginationConfig
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.content.GetLegalDocumentUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementContactInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopContractRowsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopsWithoutContractUseCase
import com.tamin.taminhamrah.useCases.workshops.RequestEmployerAgreementTicketUseCase
import com.tamin.taminhamrah.useCases.workshops.SubmitEmployerAgreementUseCase
import com.tamin.taminhamrah.util.LegalDocumentIds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * The one ViewModel behind the whole خدمات غیرحضوری کارفرمایان flow.
 *
 * On entry it fires identity ([GetUserProfileUseCase]) and the registered agreements list
 * ([GetEmployerAgreementsUseCase]) together; each failure blocks only its own part of the landing
 * screen with a fatal, retryable error tagged to that call — the same per-source error map the
 * inspection wizard uses. A card's "ردیف‌های پیمان" tap switches [EmployerOnlineServicesUiState.currentScreen]
 * to [EmployerOnlineServicesScreen.CONTRACT_ROWS] and loads that workshop's rows
 * ([GetWorkshopContractRowsUseCase]).
 *
 * "ثبت درخواست تعهدنامه" opens [EmployerOnlineServicesScreen.REQUEST_WIZARD] — the same shared
 * ViewModel drives its two steps (اعتبارسنجی → پذیرش تعهدنامه) through [AgreementRequestUiState]:
 * [RequestEmployerAgreementTicketUseCase] sends the OTP to the registered mobile, then
 * [GetEmployerAgreementContactInfoUseCase] verifies the code and reads back the employer identity.
 */
class EmployerOnlineServicesViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getEmployerAgreementsUseCase: GetEmployerAgreementsUseCase,
    private val getWorkshopContractRowsUseCase: GetWorkshopContractRowsUseCase,
    private val requestEmployerAgreementTicketUseCase: RequestEmployerAgreementTicketUseCase,
    private val getEmployerAgreementContactInfoUseCase: GetEmployerAgreementContactInfoUseCase,
    private val getWorkshopsWithoutContractUseCase: GetWorkshopsWithoutContractUseCase,
    private val getLegalDocumentUseCase: GetLegalDocumentUseCase,
    private val submitEmployerAgreementUseCase: SubmitEmployerAgreementUseCase,
) : BaseViewModel<EmployerOnlineServicesUiState, PartialState, EmployerOnlineServicesEvent, EmployerOnlineServicesIntent>(
    initialState = EmployerOnlineServicesUiState(),
) {

    /** Bumped on every successful (re)send so the code screen's countdown restarts. */
    private var ticketNonce = 0

    /** The کد شعبه / کد کارگاه filter currently applied to the agreements list, if any. */
    private var agreementsSearch: EmployerAgreementSearch = EmployerAgreementSearch()

    /**
     * The server's grand total for [agreementsSearch], read alongside each page it returns.
     * [Paginator] doesn't carry this itself (it only needs `total` to know when it has reached the
     * end), so it is captured here for the count tile.
     */
    private var agreementsTotal: Int = 0

    /** One server-paged window at a time — the same engine [MyInboxViewModel] uses for its inbox. */
    private val agreementsPaginator = Paginator<EmployerAgreementDN>(
        config = PaginationConfig(pageSize = WORKSHOP_PAGE_SIZE),
        loadPage = { query ->
            val page = getEmployerAgreementsUseCase(
                WorkshopListQuery(
                    workshopId = agreementsSearch.workshopCode.ifBlank { null },
                    branchCode = agreementsSearch.branchCode.ifBlank { null },
                    page = query.page,
                    pageSize = query.limit,
                ),
            )
            agreementsTotal = page.total
            PageDN(items = page.items, total = page.total)
        },
    )

    init {
        sendIntent(EmployerOnlineServicesIntent.Load)
    }

    override fun handleIntent(intent: EmployerOnlineServicesIntent): Flow<PartialState> = when (intent) {
        EmployerOnlineServicesIntent.Load -> merge(
            loadIdentity()
                .onStart { emit(PartialState.Loading(true)) }
                .onCompletion { emit(PartialState.Loading(false)) },
            observeAgreementsPaging(),
            flow<PartialState> { agreementsPaginator.loadNext() },
        )

        is EmployerOnlineServicesIntent.RetrySource -> retry(intent.source)
            .onStart {
                emit(PartialState.ErrorCleared(intent.source))
                emit(PartialState.Loading(true))
            }
            .onCompletion { emit(PartialState.Loading(false)) }

        EmployerOnlineServicesIntent.LoadMoreAgreements ->
            flow<PartialState> { agreementsPaginator.loadNext() }

        is EmployerOnlineServicesIntent.SearchAgreements -> flow<PartialState> {
            agreementsSearch = intent.criteria
            emit(PartialState.AgreementsSearchChanged(intent.criteria))
            agreementsPaginator.refresh()
        }

        EmployerOnlineServicesIntent.ClearAgreementsSearch -> flow<PartialState> {
            agreementsSearch = EmployerAgreementSearch()
            emit(PartialState.AgreementsSearchChanged(EmployerAgreementSearch()))
            agreementsPaginator.refresh()
        }

        EmployerOnlineServicesIntent.OpenAgreementRequest -> flow<PartialState> {
            emit(PartialState.RequestWizardOpened)
            emit(PartialState.ScreenChanged(EmployerOnlineServicesScreen.REQUEST_WIZARD))
        }

        EmployerOnlineServicesIntent.CloseAgreementRequest ->
            flow<PartialState> { emit(PartialState.RequestWizardClosed) }

        is EmployerOnlineServicesIntent.UpdateRequestMobile ->
            flow<PartialState> { emit(PartialState.RequestMobileUpdated(intent.mobile)) }

        is EmployerOnlineServicesIntent.UpdateRequestEmail ->
            flow<PartialState> { emit(PartialState.RequestEmailUpdated(intent.email)) }

        is EmployerOnlineServicesIntent.UpdateRequestCode ->
            flow<PartialState> { emit(PartialState.RequestCodeUpdated(intent.code)) }

        EmployerOnlineServicesIntent.RequestAgreementTicket -> requestTicket()
            .onStart {
                emit(PartialState.ErrorCleared(EmployerOnlineServicesErrorSource.REQUEST_TICKET))
                emit(PartialState.RequestSubmitting(true))
            }
            .onCompletion { emit(PartialState.RequestSubmitting(false)) }

        EmployerOnlineServicesIntent.EditAgreementContact ->
            flow<PartialState> { emit(PartialState.RequestContactEditing) }

        EmployerOnlineServicesIntent.VerifyAgreementCode -> verifyCode()
            .onStart {
                emit(PartialState.ErrorCleared(EmployerOnlineServicesErrorSource.VERIFY_CODE))
                emit(PartialState.RequestSubmitting(true))
            }
            .onCompletion { emit(PartialState.RequestSubmitting(false)) }

        is EmployerOnlineServicesIntent.SetAgreementAccepted ->
            flow<PartialState> { emit(PartialState.AgreementAcceptedChanged(intent.accepted)) }

        EmployerOnlineServicesIntent.SubmitAgreement -> submitAgreement()
            .onStart {
                emit(PartialState.ErrorCleared(EmployerOnlineServicesErrorSource.SUBMIT))
                emit(PartialState.RequestSubmitting(true))
            }
            .onCompletion { emit(PartialState.RequestSubmitting(false)) }

        EmployerOnlineServicesIntent.DismissAgreementSuccess -> flow<PartialState> {
            emit(PartialState.RequestWizardClosed)
            agreementsPaginator.refresh()
        }
            .onStart { emit(PartialState.Loading(true)) }
            .onCompletion { emit(PartialState.Loading(false)) }

        is EmployerOnlineServicesIntent.OpenContractRows -> flow<PartialState> {
            emit(
                PartialState.ContractRowsTarget(
                    workshopName = intent.workshopName,
                    workshopCodeLabel = intent.workshopCodeLabel,
                    workshopId = intent.workshopId,
                    branchCode = intent.branchCode,
                    originScreen = uiState.value.currentScreen,
                ),
            )
            emit(PartialState.ScreenChanged(EmployerOnlineServicesScreen.CONTRACT_ROWS))
            emitAll(loadContractRows(intent.workshopId, intent.branchCode))
        }
            .onStart { emit(PartialState.Loading(true)) }
            .onCompletion { emit(PartialState.Loading(false)) }

        EmployerOnlineServicesIntent.CloseContractRows ->
            flow<PartialState> { emit(PartialState.ScreenChanged(uiState.value.contractRows.originScreen)) }
    }

    private fun retry(source: EmployerOnlineServicesErrorSource): Flow<PartialState> = when (source) {
        EmployerOnlineServicesErrorSource.IDENTITY -> loadIdentity()
        EmployerOnlineServicesErrorSource.AGREEMENTS -> flow<PartialState> { agreementsPaginator.retry() }
        EmployerOnlineServicesErrorSource.CONTRACT_ROWS -> uiState.value.contractRows.let {
            loadContractRows(it.workshopId, it.branchCode)
        }
        EmployerOnlineServicesErrorSource.REQUEST_TICKET -> requestTicket()
        EmployerOnlineServicesErrorSource.VERIFY_CODE -> verifyCode()
        EmployerOnlineServicesErrorSource.STEP2_CONTENT -> uiState.value.agreementRequest.let {
            loadStep2Content(name = it.employerName, nationalCode = it.employerNationalCode)
        }
        EmployerOnlineServicesErrorSource.SUBMIT -> submitAgreement()
    }

    private fun loadIdentity(): Flow<PartialState> = flow<PartialState> {
        getUserProfileUseCase().collect { profile ->
            emit(PartialState.IdentityLoaded(profile.toIdentityCardPR()))
            emit(PartialState.ContactPrefillLoaded(profile.mobile.orEmpty(), profile.email.orEmpty()))
        }
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.IDENTITY))
    }

    /**
     * Reflects [agreementsPaginator]'s state into the UI forever — started once from [Load] and kept
     * alive by [BaseViewModel]'s `flatMapMerge`, exactly like `MyInboxViewModel.observePaging()`.
     * [LoadMoreAgreements] / [EmployerOnlineServicesIntent.SearchAgreements] / retry only ever poke the
     * paginator; this is the one collector that turns its state into [PartialState]s.
     */
    private fun observeAgreementsPaging(): Flow<PartialState> = agreementsPaginator.state.map { paging ->
        PartialState.AgreementsPagingChanged(
            agreements = paging.items.map { it.toRowPR() },
            total = agreementsTotal,
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = paging.error?.toSingleLineMessage(),
        )
    }

    private fun loadContractRows(workshopId: String, branchCode: String): Flow<PartialState> = flow<PartialState> {
        // Not paged — the repository fetches one wide window for this list.
        val page = getWorkshopContractRowsUseCase(workshopId, branchCode)
        emit(PartialState.ContractRowsLoaded(page.items.map { it.toPresentation() }))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.CONTRACT_ROWS))
    }

    private fun requestTicket(): Flow<PartialState> = flow<PartialState> {
        val request = uiState.value.agreementRequest
        requestEmployerAgreementTicketUseCase(mobile = request.mobile, email = request.email)
        emit(PartialState.RequestTicketIssued(++ticketNonce))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.REQUEST_TICKET))
    }

    private fun verifyCode(): Flow<PartialState> = flow<PartialState> {
        val info = getEmployerAgreementContactInfoUseCase(uiState.value.agreementRequest.code).toPresentation()
        emit(
            PartialState.AgreementCodeVerified(
                employerName = info.fullName,
                employerNationalCode = info.nationalCode,
                currentMobile = info.currentMobile,
                currentEmail = info.currentEmail,
            ),
        )
        emitAll(loadStep2Content(name = info.fullName, nationalCode = info.nationalCode))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.VERIFY_CODE))
    }

    /** Step 2 needs both the کارگاه‌های بدون تعهدنامه list and the تعهدنامه wording; they load together. */
    private fun loadStep2Content(name: String, nationalCode: String): Flow<PartialState> =
        merge(loadWorkshopsWithoutContract(), loadAgreementDocument(name, nationalCode))
            .onStart { emit(PartialState.Step2ContentLoading(true)) }
            .onCompletion { emit(PartialState.Step2ContentLoading(false)) }

    private fun loadWorkshopsWithoutContract(): Flow<PartialState> = flow<PartialState> {
        // Not paged — the repository fetches one wide window for this list.
        val page = getWorkshopsWithoutContractUseCase()
        emit(PartialState.WorkshopsWithoutContractLoaded(page.items.map { it.toPresentation() }))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.STEP2_CONTENT))
    }

    private fun loadAgreementDocument(name: String, nationalCode: String): Flow<PartialState> = flow<PartialState> {
        val document = getLegalDocumentUseCase(LegalDocumentIds.EMPLOYER_ESERVICES_AGREEMENT)
        emit(PartialState.AgreementDocumentLoaded(document.toAgreementDocumentPR(name, nationalCode)))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.STEP2_CONTENT))
    }

    private fun submitAgreement(): Flow<PartialState> = flow<PartialState> {
        val request = uiState.value.agreementRequest
        submitEmployerAgreementUseCase(
            EmployerAgreementSubmissionDN(
                mobile = request.mobile,
                email = request.email,
                ticketCode = request.code,
            ),
        )
        emit(PartialState.AgreementSubmitted)
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.SUBMIT))
    }

    override fun reduceState(
        currentState: EmployerOnlineServicesUiState,
        partialState: PartialState,
    ): EmployerOnlineServicesUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.ScreenChanged -> currentState.copy(currentScreen = partialState.screen)

        is PartialState.ContractRowsTarget -> currentState.copy(
            contractRows = ContractRowsUiState(
                workshopName = partialState.workshopName,
                workshopCodeLabel = partialState.workshopCodeLabel,
                workshopId = partialState.workshopId,
                branchCode = partialState.branchCode,
                originScreen = partialState.originScreen,
            ),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.CONTRACT_ROWS,
        )

        is PartialState.ContractRowsLoaded -> currentState.copy(
            contractRows = currentState.contractRows.copy(rows = partialState.rows),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.CONTRACT_ROWS,
        )

        is PartialState.IdentityLoaded -> currentState.copy(
            agreementsList = currentState.agreementsList.copy(identity = partialState.identity),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.IDENTITY,
        )

        is PartialState.ContactPrefillLoaded -> currentState.copy(
            profileMobile = partialState.mobile,
            profileEmail = partialState.email,
        )

        is PartialState.AgreementsPagingChanged -> currentState.copy(
            agreementsList = currentState.agreementsList.copy(
                agreements = partialState.agreements,
                agreementCount = partialState.total,
                isLoadingFirstPage = partialState.isLoadingFirstPage,
                isLoadingNextPage = partialState.isLoadingNextPage,
                endReached = partialState.endReached,
                // A next-page failure (list already has rows) is a footer error, not a fatal one —
                // only an empty-list failure blocks the whole screen behind `errors[AGREEMENTS]`.
                paginationError = partialState.error.takeIf { partialState.agreements.isNotEmpty() },
            ),
            errors = if (partialState.error != null && partialState.agreements.isEmpty()) {
                currentState.errors + (EmployerOnlineServicesErrorSource.AGREEMENTS to partialState.error)
            } else {
                currentState.errors - EmployerOnlineServicesErrorSource.AGREEMENTS
            },
        )

        is PartialState.AgreementsSearchChanged -> currentState.copy(
            agreementsList = currentState.agreementsList.copy(searchCriteria = partialState.criteria),
        )

        PartialState.RequestWizardOpened -> currentState.copy(
            agreementRequest = AgreementRequestUiState(
                mobile = currentState.profileMobile,
                email = currentState.profileEmail,
            ),
            errors = currentState.errors -
                EmployerOnlineServicesErrorSource.REQUEST_TICKET -
                EmployerOnlineServicesErrorSource.VERIFY_CODE,
        )

        PartialState.RequestWizardClosed -> currentState.copy(
            currentScreen = EmployerOnlineServicesScreen.AGREEMENTS_LIST,
            agreementRequest = AgreementRequestUiState(),
            errors = currentState.errors -
                EmployerOnlineServicesErrorSource.REQUEST_TICKET -
                EmployerOnlineServicesErrorSource.VERIFY_CODE,
        )

        is PartialState.RequestMobileUpdated -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(mobile = partialState.mobile),
        )

        is PartialState.RequestEmailUpdated -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(email = partialState.email),
        )

        is PartialState.RequestCodeUpdated -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(code = partialState.code),
        )

        is PartialState.RequestTicketIssued -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(
                ticketRequested = true,
                ticketNonce = partialState.nonce,
                code = "",
            ),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.REQUEST_TICKET,
        )

        PartialState.RequestContactEditing -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(
                step = AgreementRequestStep.VALIDATION,
                ticketRequested = false,
                code = "",
            ),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.VERIFY_CODE,
        )

        is PartialState.RequestSubmitting -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(isSubmitting = partialState.submitting),
        )

        is PartialState.AgreementCodeVerified -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(
                step = AgreementRequestStep.ACCEPT_AGREEMENT,
                employerName = partialState.employerName,
                employerNationalCode = partialState.employerNationalCode,
                currentMobile = partialState.currentMobile,
                currentEmail = partialState.currentEmail,
                accepted = false,
                isSubmitted = false,
            ),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.VERIFY_CODE,
        )

        is PartialState.Step2ContentLoading -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(isStep2Loading = partialState.loading),
        )

        is PartialState.WorkshopsWithoutContractLoaded -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(
                workshopsWithoutContract = partialState.workshops,
            ),
        )

        is PartialState.AgreementDocumentLoaded -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(document = partialState.document),
        )

        is PartialState.AgreementAcceptedChanged -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(accepted = partialState.accepted),
        )

        PartialState.AgreementSubmitted -> currentState.copy(
            agreementRequest = currentState.agreementRequest.copy(isSubmitted = true),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.SUBMIT,
        )

        is PartialState.Error -> currentState.copy(
            errors = currentState.errors + (partialState.source to partialState.message),
        )

        is PartialState.ErrorCleared -> currentState.copy(
            errors = currentState.errors - partialState.source,
        )
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(EmployerOnlineServicesEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}

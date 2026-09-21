package com.tamin.taminhamrah.feature.retirementPension.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDialog
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentPR
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentType
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionEvent
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionIntent
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionUiState
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionUiState.PartialState
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementScreen
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementStep
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RETIREMENT_REQUEST_STATUS_CREATED
import com.tamin.taminhamrah.model.pension.retirement.RETIREMENT_REQUEST_STATUS_DOCUMENTS
import com.tamin.taminhamrah.model.pension.retirement.RetirementBranchInfoPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementDocumentDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementHistoryPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementIdentityPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.pension.AuthenticationAndGetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.CheckRetirementStatusUseCase
import com.tamin.taminhamrah.useCases.pension.CreateRetirementRequestUseCase
import com.tamin.taminhamrah.useCases.pension.GetAuthenticationCodeUseCase
import com.tamin.taminhamrah.useCases.pension.GetRetirementRequestInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.SendRetirementDocumentUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class RetirementPensionViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase,
    private val getInsuredActiveBranchUseCase: GetInsuredActiveBranchUseCase,
    private val getUserAgeUseCase: GetUserAgeUseCase,
    private val checkRetirementStatusUseCase: CheckRetirementStatusUseCase,
    private val getAuthenticationCodeUseCase: GetAuthenticationCodeUseCase,
    private val authenticationAndGetPersonalInfoUseCase: AuthenticationAndGetPersonalInfoUseCase,
    private val getRetirementRequestInfoUseCase: GetRetirementRequestInfoUseCase,
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val createRetirementRequestUseCase: CreateRetirementRequestUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val sendRetirementDocumentUseCase: SendRetirementDocumentUseCase,
) : BaseViewModel<
    RetirementPensionUiState,
    PartialState,
    RetirementPensionEvent,
    RetirementPensionIntent,
>(
    initialState = RetirementPensionUiState(),
) {
    init {
        sendIntent(RetirementPensionIntent.Init)
    }

    override fun handleIntent(intent: RetirementPensionIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { error ->
            sendEvent(RetirementPensionEvent.ShowError(error.toSingleLineMessage()))
            emit(createErrorState(error.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: RetirementPensionIntent): Flow<PartialState> = flow {
        when (intent) {
            RetirementPensionIntent.Init -> loadIntro()

            RetirementPensionIntent.StartRequest -> {
                emit(PartialState.ScreenChanged(RetirementScreen.Form))
                emit(PartialState.StepChanged(RetirementStep.Rules))
            }

            RetirementPensionIntent.OpenTrack ->
                emit(PartialState.ScreenChanged(RetirementScreen.Track))

            RetirementPensionIntent.Back -> goBack()

            RetirementPensionIntent.CloseClicked -> {
                if (uiState.value.screen == RetirementScreen.Form) {
                    emit(PartialState.DialogChanged(RetirementDialog.Leave))
                } else {
                    sendEvent(RetirementPensionEvent.NavigateBack)
                }
            }

            RetirementPensionIntent.LeaveConfirmed -> {
                emit(PartialState.DialogChanged(null))
                sendEvent(RetirementPensionEvent.NavigateBack)
            }

            // Backwards only: a wizard cannot be skipped forward from its own progress bar.
            is RetirementPensionIntent.GoToStep -> {
                if (intent.step.ordinal < uiState.value.step.ordinal) {
                    emit(PartialState.ValidationAttempted(false))
                    emit(PartialState.StepChanged(intent.step))
                }
            }

            RetirementPensionIntent.NextStep -> goForward()

            is RetirementPensionIntent.ConsentChanged ->
                emitEdit(PartialState.ConsentChanged(intent.accepted))

            RetirementPensionIntent.ViewRules ->
                sendEvent(RetirementPensionEvent.OpenRulesDocument)

            RetirementPensionIntent.RequestOtp -> requestOtp()

            is RetirementPensionIntent.OtpChanged -> onOtpChanged(intent.value)

            is RetirementPensionIntent.PhoneChanged ->
                emitEdit(PartialState.PhoneChanged(intent.value.digitsOnly().take(PHONE_LENGTH)))

            is RetirementPensionIntent.AddressChanged ->
                emitEdit(PartialState.AddressChanged(intent.value))

            is RetirementPensionIntent.IdentityConfirmedChanged ->
                emitEdit(PartialState.IdentityConfirmedChanged(intent.confirmed))

            is RetirementPensionIntent.WorkshopNameChanged ->
                emitEdit(PartialState.WorkshopNameChanged(intent.value))

            is RetirementPensionIntent.WorkshopCodeChanged ->
                emitEdit(
                    PartialState.WorkshopCodeChanged(
                        intent.value.digitsOnly().take(WORKSHOP_CODE_LENGTH),
                    ),
                )

            is RetirementPensionIntent.WorkshopAddressChanged ->
                emitEdit(PartialState.WorkshopAddressChanged(intent.value))

            // Optional fields: nothing to clear, because nothing about them can be wrong.
            is RetirementPensionIntent.EmployerNameChanged ->
                emit(PartialState.EmployerNameChanged(intent.value))

            is RetirementPensionIntent.ActivityTypeChanged ->
                emit(PartialState.ActivityTypeChanged(intent.value))

            is RetirementPensionIntent.WorkshopConfirmedChanged ->
                emitEdit(PartialState.WorkshopConfirmedChanged(intent.confirmed))

            is RetirementPensionIntent.DocumentClicked ->
                emit(PartialState.DocumentSourceRequested(intent.type))

            is RetirementPensionIntent.DocumentPicked -> uploadDocument(intent)

            RetirementPensionIntent.DismissDocumentSource ->
                emit(PartialState.DocumentSourceDismissed)

            RetirementPensionIntent.CameraDenied -> {
                emit(PartialState.DocumentSourceDismissed)
                sendEvent(RetirementPensionEvent.CameraPermissionDenied)
            }

            is RetirementPensionIntent.FinalConfirmedChanged ->
                emitEdit(PartialState.FinalConfirmedChanged(intent.confirmed))

            is RetirementPensionIntent.ShowDialog ->
                emit(PartialState.DialogChanged(intent.dialog))

            RetirementPensionIntent.DismissDialog -> dismissDialog()
        }
    }

    /**
     * Applies an edit and takes the complaint down with it.
     *
     * Validation is only shown after «مرحلهٔ بعدی» is pressed; touching the field the message is
     * about should stop shouting about it immediately, which is what the mock does.
     */
    private suspend fun FlowCollector<PartialState>.emitEdit(partialState: PartialState) {
        emit(PartialState.ValidationAttempted(false))
        emit(partialState)
    }

    // ---------------------------------------------------------------- navigation

    private suspend fun FlowCollector<PartialState>.goBack() {
        val state = uiState.value
        when {
            state.screen == RetirementScreen.Track ->
                emit(PartialState.ScreenChanged(RetirementScreen.Intro))

            state.screen == RetirementScreen.Form && state.step != RetirementStep.Rules -> {
                emit(PartialState.ValidationAttempted(false))
                state.step.previous?.let { emit(PartialState.StepChanged(it)) }
            }

            // Leaving a part-filled form throws the answers away, so it is worth a question.
            state.screen == RetirementScreen.Form ->
                emit(PartialState.DialogChanged(RetirementDialog.Leave))

            else -> sendEvent(RetirementPensionEvent.NavigateBack)
        }
    }

    private suspend fun FlowCollector<PartialState>.dismissDialog() {
        val dialog = uiState.value.dialog
        emit(PartialState.DialogChanged(null))
        when (dialog) {
            // Failing the age gate ends the service rather than stepping back inside it.
            RetirementDialog.AgeGate -> sendEvent(RetirementPensionEvent.NavigateBack)
            RetirementDialog.Done -> emit(PartialState.ScreenChanged(RetirementScreen.Track))
            else -> Unit
        }
    }

    private suspend fun FlowCollector<PartialState>.goForward() {
        val state = uiState.value
        if (state.isLoading) return

        if (state.stepError() != null) {
            emit(PartialState.ValidationAttempted(true))
            return
        }
        emit(PartialState.ValidationAttempted(false))

        when (state.step) {
            // The SMS is asked for as the step opens, so the field is never waiting on a tap.
            RetirementStep.Rules -> {
                emit(PartialState.StepChanged(RetirementStep.Authentication))
                if (!state.otpSent && !state.otpVerified) requestOtp()
            }

            RetirementStep.Authentication -> {
                emit(PartialState.StepChanged(RetirementStep.Identity))
                loadPrefill()
            }

            RetirementStep.Identity -> emit(PartialState.StepChanged(RetirementStep.Workshop))

            RetirementStep.Workshop -> {
                emit(PartialState.StepChanged(RetirementStep.History))
                loadHistory()
            }

            RetirementStep.History -> createRequest()

            RetirementStep.IdentityDocuments -> sendDocuments(
                types = RetirementDocumentType.entries.filter(RetirementDocumentType::isIdentityDocument),
                nextStep = RetirementStep.QuitLetter,
            )

            RetirementStep.QuitLetter -> sendDocuments(
                types = listOf(RetirementDocumentType.QuitLetter),
                nextStep = RetirementStep.Final,
            )

            RetirementStep.Final -> finish()
        }
    }

    // ---------------------------------------------------------------- intro

    private suspend fun FlowCollector<PartialState>.loadIntro() {
        emit(PartialState.IntroLoading(true))
        try {
            // The profile's own identity call is the one that reliably carries a name, national id
            // and birthdate; `personal-info` returns the insurance number but leaves `personal`
            // null for some accounts, which is what left the card reading "—".
            val identity = runCatching { identityInfoUseCase().first() }.getOrNull()
            val personal = runCatching {
                getPersonalInfoUseCase(refreshRemote = true).firstOrNull()
            }.getOrNull()
            val branch = runCatching {
                getInsuredActiveBranchUseCase.invoke().firstOrNull()?.firstOrNull()
            }.getOrNull()
            val status = runCatching { checkRetirementStatusUseCase().first() }.getOrNull()

            // The endpoint wants the epoch itself; without it the service answers with no age.
            val birthDate = identity?.dateOfBirth
            val age = birthDate?.let {
                runCatching {
                    getUserAgeUseCase(
                        listOf(
                            ApiFilterDN(
                                property = FilterProperty.BIRTH_DATE,
                                value = it.toString(),
                                operator = FilterOperator.EQUAL,
                            ),
                        ),
                    ).first()
                }.getOrNull()
            }

            val rawAge = age?.age.orEmpty()
            // The service reports age as "years,months,days".
            val ageParts = rawAge.split(',').map { it.trim().toIntOrNull() }
            val years = ageParts.getOrNull(0)

            emit(
                PartialState.IntroLoaded(
                    insured = RetirementInsuredPR(
                        fullName = listOfNotNull(identity?.firstName, identity?.lastName)
                            .joinToString(" ")
                            .trim(),
                        insuranceNumber = personal?.insuranceId
                            ?: identity?.ssn.orEmpty(),
                        nationalCode = identity?.nationalId.orEmpty(),
                        ageYears = years?.toString()?.toPersianDigits().orEmpty(),
                        ageMonths = ageParts.getOrNull(1)?.toString()?.toPersianDigits().orEmpty(),
                        branchName = branch?.branchName.orEmpty(),
                    ),
                    branch = RetirementBranchInfoPR(
                        branchName = branch?.branchName.orEmpty(),
                        branchCode = branch?.branchCode ?: personal?.branch.orEmpty(),
                    ),
                    rawAge = rawAge,
                    // An age the service did not report is not a refusal: the gate closes only on a
                    // reported age that is under the statutory minimum.
                    //
                    // Deliberately unlike `old_android`, which kept the age in a non-null `Int`
                    // defaulted to 0 (`RetirementDataModel.yearsAge`, also set to 0 whenever the
                    // reported value would not parse) and so refused anyone whose age the service
                    // failed to report — `0 < 42`. That refusal is final on the client: the
                    // applicant is sent back with a message about their age, and a service outage
                    // or an unparsed field reads to them as "you are too young". The branch decides
                    // eligibility for real, so a missing age is passed on rather than judged here.
                    isAgeEligible = years == null || years >= MIN_RETIREMENT_AGE_YEARS,
                    requestId = status?.requestId,
                    statusCode = status?.requestStatusCode,
                ),
            )
        } finally {
            emit(PartialState.IntroLoading(false))
        }
    }

    // ---------------------------------------------------------------- step 2

    private suspend fun FlowCollector<PartialState>.requestOtp() {
        if (uiState.value.isOtpSending) return
        emit(PartialState.OtpSending(true))
        try {
            val ticket = getAuthenticationCodeUseCase().first()
            emit(PartialState.OtpTicketReceived(ticket.mobileNumber.orEmpty()))
        } finally {
            emit(PartialState.OtpSending(false))
        }
    }

    /**
     * A six-digit code *is* the verification: the ticket is the code, so a successful
     * `pension-request/personal` call is the proof. Nothing is sent until the sixth digit lands.
     */
    private suspend fun FlowCollector<PartialState>.onOtpChanged(raw: String) {
        val digits = raw.digitsOnly().take(OTP_LENGTH)
        emit(PartialState.OtpChanged(digits))
        if (digits.length < OTP_LENGTH || uiState.value.otpVerified) return

        val ticketCode = digits.toLongOrNull() ?: return
        emit(PartialState.Loading(true))
        try {
            val personal = authenticationAndGetPersonalInfoUseCase(ticketCode).first()
            if (personal.verificationResult?.contains(TICKET_NOT_FOUND, ignoreCase = true) == true) {
                emit(PartialState.OtpRejected)
                return
            }
            emit(personal.toVerified(ticketCode))
            sendEvent(RetirementPensionEvent.AuthenticationSucceeded)
        } catch (_: Exception) {
            // A rejected code is an ordinary outcome of typing, not an error worth a toast.
            emit(PartialState.OtpRejected)
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    // ---------------------------------------------------------------- steps 3 and 4

    /**
     * Fills the identity and workshop forms from an existing request, when there is one.
     *
     * Best-effort: with no prior request the service has nothing to return, and the fields keep
     * what step 2 already established.
     */
    private suspend fun FlowCollector<PartialState>.loadPrefill() {
        val requestId = uiState.value.requestId?.takeIf(String::isNotBlank) ?: return
        val prefill = runCatching {
            getRetirementRequestInfoUseCase(
                listOf(
                    ApiFilterDN(
                        property = FilterProperty.REQUEST_ID,
                        value = requestId,
                        operator = FilterOperator.EQUAL,
                    ),
                ),
            ).first().firstOrNull()
        }.getOrNull() ?: return

        emit(prefill.toPrefill())
    }

    // ---------------------------------------------------------------- step 5

    private suspend fun FlowCollector<PartialState>.loadHistory() {
        if (uiState.value.history != null) return
        emit(PartialState.HistoryLoading(true))
        try {
            val talfigh = getTalfighInfosUseCase().list?.firstOrNull()
            val wages = getDastmozdInfosUseCase().list.orEmpty()

            val duration = RetirementHistoryCalculator.normalize(
                years = talfigh?.historyYears ?: 0,
                months = talfigh?.historyMonths ?: 0,
                days = talfigh?.historyDays ?: 0,
            )
            val totalDays = talfigh?.sumHistoryYears ?: 0
            val averageWage = RetirementHistoryCalculator.averageWage(wages)

            emit(
                PartialState.HistoryLoaded(
                    RetirementHistoryPR(
                        averageWage = averageWage.toPriceFormat(),
                        estimatedPension = RetirementHistoryCalculator
                            .estimatedPension(averageWage, totalDays)
                            .toPriceFormat(),
                        years = duration.years.toString().toPersianDigits(),
                        months = duration.months.toString().toPersianDigits(),
                        days = duration.days.toString().toPersianDigits(),
                        totalDays = totalDays.toLong().toPriceFormat(),
                        yearsValue = duration.years,
                        monthsValue = duration.months,
                    ),
                ),
            )
        } finally {
            emit(PartialState.HistoryLoading(false))
        }
    }

    // ---------------------------------------------------------------- step 5 → 6

    /**
     * Creates the request row. Everything after this addresses it by id, so a failure has to stop
     * the wizard rather than let it walk on to an upload with nothing to attach to.
     */
    private suspend fun FlowCollector<PartialState>.createRequest() {
        val state = uiState.value
        // Without the verified ticket the server has nothing to create the request against. Say so
        // rather than returning quietly: a primary button that does nothing at all reads as a
        // broken app, not as a missing precondition.
        val ticketCode = state.ticketCode
        if (ticketCode == null) {
            sendEvent(RetirementPensionEvent.RequestCreationFailed)
            return
        }

        emit(PartialState.Loading(true))
        try {
            val created = createRetirementRequestUseCase(ticketCode, state.toForm()).first()
            val requestId = created.requestId?.toString()
            if (requestId.isNullOrBlank()) {
                sendEvent(RetirementPensionEvent.RequestCreationFailed)
                return
            }
            emit(PartialState.RequestCreated(requestId))
            emit(PartialState.StepChanged(RetirementStep.IdentityDocuments))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    // ---------------------------------------------------------------- steps 6 and 7

    private suspend fun FlowCollector<PartialState>.uploadDocument(
        intent: RetirementPensionIntent.DocumentPicked,
    ) {
        if (uiState.value.isDocumentUploading) return

        emit(PartialState.DocumentSourceDismissed)
        emit(PartialState.DocumentUploadStarted(intent.type))
        try {
            val guid = uploadImageUseCase(
                UploadImageRequestDN(
                    fileName = intent.file.name,
                    bytes = intent.file.readBytes(),
                ),
            ).first()
            emit(PartialState.DocumentUploaded(intent.type, guid))
        } catch (e: Exception) {
            emit(PartialState.DocumentUploadFailed(intent.type))
            sendEvent(RetirementPensionEvent.ShowError(e.toSingleLineMessage()))
        }
    }

    private suspend fun FlowCollector<PartialState>.sendDocuments(
        types: List<RetirementDocumentType>,
        nextStep: RetirementStep,
    ) {
        val state = uiState.value
        val requestId = state.requestId?.takeIf(String::isNotBlank)
        if (requestId == null) {
            sendEvent(RetirementPensionEvent.RequestCreationFailed)
            return
        }

        emit(PartialState.Loading(true))
        try {
            sendRetirementDocumentUseCase(
                requestId = requestId,
                request = RetirementSaveDocumentDN(
                    pensionRequestDocList = types.mapNotNull { type ->
                        val guid = state.documents[type]?.guid ?: return@mapNotNull null
                        RetirementDocumentDN(documentType = type.code, guid = guid)
                    },
                    status = RETIREMENT_REQUEST_STATUS_DOCUMENTS,
                ),
            ).first()
            emit(PartialState.StepChanged(nextStep))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    // ---------------------------------------------------------------- step 8

    /**
     * Confirms the request and re-reads where the branch has taken it.
     *
     * There is no separate "finalize" endpoint: the quit-letter `PUT` on step 7 is what submits, so
     * this step confirms, refreshes the status the track screen reads, and shows the tracking code.
     */
    private suspend fun FlowCollector<PartialState>.finish() {
        emit(PartialState.Loading(true))
        try {
            val status = runCatching { checkRetirementStatusUseCase().first() }.getOrNull()
            emit(PartialState.StatusChanged(status?.requestStatusCode))
            emit(PartialState.DialogChanged(RetirementDialog.Done))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    // ---------------------------------------------------------------- reduce

    override fun reduceState(
        currentState: RetirementPensionUiState,
        partialState: PartialState,
    ): RetirementPensionUiState = currentState.apply(partialState).withStepError()

    /**
     * Recomputes the current step's first failure after every change.
     *
     * Done here, not in composition: the rules read a dozen fields, and evaluating them inside a
     * composable would re-run them on every emission rather than once per change.
     */
    private fun RetirementPensionUiState.withStepError() = copy(error = stepError())

    private fun RetirementPensionUiState.apply(partialState: PartialState) = when (partialState) {
        is PartialState.Loading -> copy(isLoading = partialState.isLoading)
        is PartialState.IntroLoading -> copy(isIntroLoading = partialState.isIntroLoading)
        is PartialState.HistoryLoading -> copy(isHistoryLoading = partialState.isHistoryLoading)
        is PartialState.OtpSending -> copy(isOtpSending = partialState.isSending)

        is PartialState.IntroLoaded -> copy(
            insured = partialState.insured,
            branch = partialState.branch,
            rawAge = partialState.rawAge,
            isAgeEligible = partialState.isAgeEligible,
            requestId = partialState.requestId,
            statusCode = partialState.statusCode,
            dialog = if (partialState.isAgeEligible) dialog else RetirementDialog.AgeGate,
        )

        is PartialState.ScreenChanged -> copy(
            screen = partialState.screen,
            validationAttempted = false,
        )

        is PartialState.StepChanged -> copy(
            step = partialState.step,
            maxReachedStep = maxOf(maxReachedStep, partialState.step),
        )

        is PartialState.ValidationAttempted -> copy(validationAttempted = partialState.attempted)
        is PartialState.ConsentChanged -> copy(consentAccepted = partialState.accepted)

        is PartialState.OtpTicketReceived -> copy(
            otpMobile = partialState.mobileNumber,
            otpSent = true,
            otpInvalid = false,
            otpValue = "",
        )

        is PartialState.OtpChanged -> copy(otpValue = partialState.value, otpInvalid = false)
        PartialState.OtpRejected -> copy(otpInvalid = true, otpVerified = false)

        is PartialState.OtpVerified -> {
            val current = insured
            copy(
                otpVerified = true,
                otpInvalid = false,
                otpSent = false,
                ticketCode = partialState.ticketCode,
                identity = partialState.identity,
                branch = partialState.branch,
                insured = current?.copy(
                    insuranceNumber = current.insuranceNumber
                        .ifBlank { partialState.insuranceNumber },
                    branchName = current.branchName.ifBlank { partialState.branch.branchName },
                ),
                // The service's own contact details fill an empty field, but never overwrite
                // something the user has already typed.
                phoneNumber = phoneNumber.ifBlank { partialState.phoneNumber },
                address = address.ifBlank { partialState.address },
                workshopCode = workshopCode.ifBlank { partialState.workshopCode },
                activityType = activityType.ifBlank { partialState.activityType },
            )
        }

        is PartialState.PrefillLoaded -> copy(
            phoneNumber = phoneNumber.ifBlank { partialState.phoneNumber },
            address = address.ifBlank { partialState.address },
            workshopName = workshopName.ifBlank { partialState.workshopName },
            workshopCode = workshopCode.ifBlank { partialState.workshopCode },
            workshopAddress = workshopAddress.ifBlank { partialState.workshopAddress },
            employerName = employerName.ifBlank { partialState.employerName },
            activityType = activityType.ifBlank { partialState.activityType },
        )

        is PartialState.PhoneChanged -> copy(phoneNumber = partialState.value)
        is PartialState.AddressChanged -> copy(address = partialState.value)
        is PartialState.IdentityConfirmedChanged -> copy(identityConfirmed = partialState.confirmed)

        is PartialState.WorkshopNameChanged -> copy(workshopName = partialState.value)
        is PartialState.WorkshopCodeChanged -> copy(workshopCode = partialState.value)
        is PartialState.WorkshopAddressChanged -> copy(workshopAddress = partialState.value)
        is PartialState.EmployerNameChanged -> copy(employerName = partialState.value)
        is PartialState.ActivityTypeChanged -> copy(activityType = partialState.value)
        is PartialState.WorkshopConfirmedChanged -> copy(workshopConfirmed = partialState.confirmed)

        is PartialState.HistoryLoaded -> copy(history = partialState.history)

        is PartialState.RequestCreated -> copy(requestId = partialState.requestId)
        is PartialState.StatusChanged -> copy(statusCode = partialState.statusCode)

        is PartialState.DocumentSourceRequested -> copy(activeDocument = partialState.type)
        PartialState.DocumentSourceDismissed -> copy(activeDocument = null)

        is PartialState.DocumentUploadStarted -> copy(
            documents = documents.put(partialState.type, RetirementDocumentPR(isUploading = true)),
        )

        is PartialState.DocumentUploaded -> copy(
            documents = documents.put(
                partialState.type,
                RetirementDocumentPR(guid = partialState.guid),
            ),
        )

        is PartialState.DocumentUploadFailed -> copy(
            documents = documents.put(partialState.type, RetirementDocumentPR(hasFailed = true)),
        )

        is PartialState.FinalConfirmedChanged -> copy(finalConfirmed = partialState.confirmed)
        is PartialState.DialogChanged -> copy(dialog = partialState.dialog)

        PartialState.Error ->
            copy(isLoading = false, isIntroLoading = false, isHistoryLoading = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error

    private companion object {
        const val OTP_LENGTH = 6

        /**
         * Statutory minimum age (42 years), below which the service refuses the request.
         *
         * Matches `old_android`'s `RetirementPensionFragment.kt:480` (`if (yearsAge < 42)`), which
         * accommodates early and special retirement categories under Iranian Social Security law
         * (e.g., hazardous and arduous jobs, or women with at least 20 years of service).
         */
        const val MIN_RETIREMENT_AGE_YEARS = 42

        /** What `verificationResult` says when the code does not match a live ticket. */
        const val TICKET_NOT_FOUND = "ticketNotFound"
    }
}

internal fun RetirementPensionUiState.toForm() = RetirementRequestFormDN(
    activityType = activityType,
    address = address.trim(),
    age = rawAge,
    birthDate = identity?.birthDateEpoch ?: 0L,
    branchCode = branch?.branchCode.orEmpty(),
    fatherName = identity?.fatherName.orEmpty(),
    firstName = identity?.firstName.orEmpty(),
    gender = identity?.genderCode.orEmpty(),
    idNumber = identity?.idNumber.orEmpty(),
    insuranceNumber = insured?.insuranceNumber.orEmpty(),
    issuePlace = identity?.issuePlace.orEmpty(),
    lastName = identity?.lastName.orEmpty(),
    managerName = employerName,
    mobileNumber = otpMobile.ifBlank { identity?.mobileNumber.orEmpty() },
    nationalCode = identity?.nationalCode.orEmpty(),
    phoneNumber = phoneNumber,
    status = RETIREMENT_REQUEST_STATUS_CREATED,
    workshopAddress = workshopAddress.trim(),
    workshopCode = workshopCode,
    workshopName = workshopName,
)

private fun RetirementPersonalDN.toVerified(ticketCode: Long) =
    PartialState.OtpVerified(
        ticketCode = ticketCode,
        identity = RetirementIdentityPR(
            fatherName = personal?.fatherName.orEmpty(),
            idNumber = personal?.idCardNumber.orEmpty(),
            gender = personal?.genderDesc.orEmpty(),
            birthDate = PersianDateFormatter.formatTimestamp(personal?.dateOfBirth),
            issuePlace = provinceName.orEmpty(),
            mobileNumber = mobileNumber.orEmpty(),
            birthDateEpoch = personal?.dateOfBirth ?: 0L,
            genderCode = personal?.genderCode.orEmpty(),
            firstName = personal?.firstName.orEmpty(),
            lastName = personal?.lastName.orEmpty(),
            nationalCode = personal?.nationalId.orEmpty(),
        ),
        branch = RetirementBranchInfoPR(
            branchName = branchName.orEmpty(),
            branchCode = branch.orEmpty(),
        ),
        insuranceNumber = insuranceId.orEmpty(),
        workshopCode = work?.workshopId.orEmpty(),
        activityType = work?.job?.jobDescription.orEmpty(),
        phoneNumber = personal?.contactPhoneNumber.orEmpty(),
        address = personal?.contactAddress.orEmpty(),
    )

private fun RetirementRequestDN.toPrefill() =
    PartialState.PrefillLoaded(
        phoneNumber = phoneNumber.orEmpty(),
        address = address.orEmpty(),
        workshopName = workshopName.orEmpty(),
        workshopCode = workshopCode.orEmpty(),
        workshopAddress = workshopAddress.orEmpty(),
        employerName = managerName.orEmpty(),
        activityType = activityType.orEmpty(),
    )

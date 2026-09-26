package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoDialog
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoEvent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoIntent
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoPartialState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoScreenState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoUiState
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.VerifyPath
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.mapper.contracts.toBranchPresentation
import com.tamin.taminhamrah.mapper.employerInfo.toWorkshopItemPRs
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.paging.PaginationConfig
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.query.city.CityByProvinceQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.common.GetCitiesByProvincePageUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesPageUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.employerInfo.GetLegalWorkshopCeoUseCase
import com.tamin.taminhamrah.useCases.employerInfo.GetLegalWorkshopUseCase
import com.tamin.taminhamrah.useCases.employerInfo.RequestLegalTicketUseCase
import com.tamin.taminhamrah.useCases.employerInfo.RequestRealTicketUseCase
import com.tamin.taminhamrah.useCases.employerInfo.SubmitLegalWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.employerInfo.SubmitRealWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

class CompleteEmployerInfoViewModel(
    private val getEmployerAgreements: GetEmployerAgreementsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLegalWorkshopUseCase: GetLegalWorkshopUseCase,
    private val getLegalWorkshopCeoUseCase: GetLegalWorkshopCeoUseCase,
    private val requestLegalTicketUseCase: RequestLegalTicketUseCase,
    private val submitLegalWorkshopInfoUseCase: SubmitLegalWorkshopInfoUseCase,
    private val requestRealTicketUseCase: RequestRealTicketUseCase,
    private val submitRealWorkshopInfoUseCase: SubmitRealWorkshopInfoUseCase,
    private val getProvincesPageUseCase: GetProvincesPageUseCase,
    private val getCitiesByProvincePageUseCase: GetCitiesByProvincePageUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
) : BaseViewModel<
    CompleteEmployerInfoUiState,
    CompleteEmployerInfoPartialState,
    CompleteEmployerInfoEvent,
    CompleteEmployerInfoIntent,
>(CompleteEmployerInfoUiState()) {

    private var currentProvinceCode: String = ""

    private val provincePaginator = Paginator(
        loadPage = { query -> getProvincesPageUseCase(query).first() },
    )
    // One large page (matching the pre-pagination code's `limit = 200` for this endpoint) so the
    // sheet's local text filter has full coverage of a province's cities on first load.
    private val cityPaginator = Paginator(
        config = PaginationConfig(pageSize = CITIES_PER_PROVINCE_PAGE_SIZE),
        loadPage = { query -> getCitiesByProvincePageUseCase(currentProvinceCode, query).first() },
    )

    init {
        sendIntent(CompleteEmployerInfoIntent.LoadInitialData)
    }

    override fun handleIntent(intent: CompleteEmployerInfoIntent): Flow<CompleteEmployerInfoPartialState> =
        when (intent) {
            is CompleteEmployerInfoIntent.LoadInitialData -> handleLoadInitialData()
            is CompleteEmployerInfoIntent.SelectTab -> flow {
                emit(CompleteEmployerInfoPartialState.SwitchTab(intent.tab))
            }
            is CompleteEmployerInfoIntent.ToggleWorkshopExpanded -> flow {
                emit(CompleteEmployerInfoPartialState.WorkshopExpandedToggled(intent.workshopId))
            }
            is CompleteEmployerInfoIntent.SelectWorkshopForLegalForm -> flow {
                emit(CompleteEmployerInfoPartialState.OpenLegalForm(intent.workshop))
            }
            is CompleteEmployerInfoIntent.ChangeLegalNationalId -> handleChangeLegalNationalId(intent.nid)
            is CompleteEmployerInfoIntent.SelectCompanyType -> flow {
                emit(CompleteEmployerInfoPartialState.CompanyTypeSelected(intent.type))
            }
            is CompleteEmployerInfoIntent.ChangeCeoNationalId -> handleChangeCeoNationalId(intent.nid)
            is CompleteEmployerInfoIntent.SelectCeoBirthDate -> handleSelectCeoBirthDate(intent.millis, intent.persianDate)
            is CompleteEmployerInfoIntent.ChangeTelephone -> flow {
                emit(CompleteEmployerInfoPartialState.TelephoneChanged(intent.telephone))
            }
            is CompleteEmployerInfoIntent.ChangeLegalMobile -> flow {
                emit(CompleteEmployerInfoPartialState.LegalMobileChanged(intent.mobile))
            }
            is CompleteEmployerInfoIntent.ChangeLegalEmail -> flow {
                emit(CompleteEmployerInfoPartialState.LegalEmailChanged(intent.email))
            }
            is CompleteEmployerInfoIntent.SubmitLegalForm -> handleSubmitLegalForm()
            is CompleteEmployerInfoIntent.ChangeRealWorkshopCode -> flow {
                emit(CompleteEmployerInfoPartialState.RealWorkshopCodeChanged(intent.code))
            }
            is CompleteEmployerInfoIntent.SelectProvince -> handleSelectProvince(intent.province)
            is CompleteEmployerInfoIntent.ProvincePickerLoadMore -> flow { provincePaginator.loadNext() }
            is CompleteEmployerInfoIntent.SelectCity -> handleSelectCity(intent.city)
            is CompleteEmployerInfoIntent.CityPickerLoadMore -> flow { cityPaginator.loadNext() }
            is CompleteEmployerInfoIntent.SelectBranch -> flow {
                emit(CompleteEmployerInfoPartialState.BranchSelected(intent.branch))
            }
            is CompleteEmployerInfoIntent.SubmitRealForm -> handleSubmitRealForm()
            is CompleteEmployerInfoIntent.OpenBottomSheet -> flow {
                emit(CompleteEmployerInfoPartialState.OpenSheet(intent.sheet))
            }
            is CompleteEmployerInfoIntent.CloseBottomSheet -> flow {
                emit(CompleteEmployerInfoPartialState.DismissSheet)
            }
            is CompleteEmployerInfoIntent.ChangeOtpCode -> flow {
                emit(CompleteEmployerInfoPartialState.OtpCodeChanged(intent.code))
            }
            is CompleteEmployerInfoIntent.SubmitOtpVerification -> handleSubmitOtp()
            is CompleteEmployerInfoIntent.BackFromOtp -> flow {
                emit(CompleteEmployerInfoPartialState.BackFromVerification)
            }
            is CompleteEmployerInfoIntent.BackToWorkshopList -> flow {
                emit(CompleteEmployerInfoPartialState.BackToList)
            }
            is CompleteEmployerInfoIntent.OnTimerExpired -> flow {
                emit(CompleteEmployerInfoPartialState.ShowDialog(CompleteEmployerInfoDialog.TIMER_EXPIRED))
            }
            is CompleteEmployerInfoIntent.CloseDialog -> flow {
                emit(CompleteEmployerInfoPartialState.DismissDialog)
            }
            is CompleteEmployerInfoIntent.DismissGeneralError -> flow {
                emit(CompleteEmployerInfoPartialState.ErrorDismissed)
            }
        }

    /**
     * Provinces are collected alongside rather than in sequence: the repository ends in a database
     * flow that never completes, so awaiting it here meant `Loading(false)` was never reached and
     * the page stayed loading for as long as it was open.
     */
    private fun handleLoadInitialData(): Flow<CompleteEmployerInfoPartialState> =
        merge(loadUserAndWorkshops(), loadProvinces(), observeProvincePaging(), observeCityPaging())

    private fun loadUserAndWorkshops(): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.Loading(true))

        // Not swallowed: the real path sends its ticket to the mobile and email that come from
        // here, so a silent failure would post a ticket request with two empty contacts and leave
        // the person staring at a server error that names nothing. `catch` rather than a raw
        // try/catch around `collect`, so cancelling the screen stays a cancellation instead of
        // being reported as a failure.
        getUserProfileUseCase()
            .catch { error ->
                sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
            }
            .collect { userProfile ->
                emit(
                    CompleteEmployerInfoPartialState.UserInfoLoaded(
                        fullName = listOfNotNull(userProfile.firstName, userProfile.lastName).joinToString(" ").trim(),
                        nationalCode = userProfile.nationalCode.orEmpty(),
                        mobile = userProfile.mobile.orEmpty(),
                        email = userProfile.email.orEmpty(),
                    )
                )
            }

        try {
            val agreements = getEmployerAgreements(WorkshopListQuery())
            val items = agreements.items.toWorkshopItemPRs()
            emit(CompleteEmployerInfoPartialState.WorkshopsLoaded(items.toImmutableList()))
        } catch (e: CancellationException) {
            // A suspend call, so there is no `catch` operator to lean on: cancellation has to be
            // let through by hand or leaving the screen mid-load reports itself as an error.
            throw e
        } catch (e: Exception) {
            emit(CompleteEmployerInfoPartialState.Error(e.toSingleLineMessage()))
        }

        emit(CompleteEmployerInfoPartialState.Loading(false))
    }

    private fun loadProvinces(): Flow<CompleteEmployerInfoPartialState> = flow {
        provincePaginator.refresh(ApiQueryParamDN())
    }

    private fun observeProvincePaging(): Flow<CompleteEmployerInfoPartialState> =
        provincePaginator.state.transform { paging ->
            emit(
                CompleteEmployerInfoPartialState.ProvincePagingChanged(
                    items = paging.items.toProvincePresentation().toImmutableList(),
                    isLoadingFirstPage = paging.isLoadingFirstPage,
                    isLoadingNextPage = paging.isLoadingNextPage,
                    endReached = paging.endReached,
                ),
            )
            // A toast rather than the list's error state: the province picker sits behind the
            // second tab, and blanking the workshop list for it would hide working content.
            paging.error?.let { sendEvent(CompleteEmployerInfoEvent.ShowToast(it.toSingleLineMessage())) }
        }

    private fun observeCityPaging(): Flow<CompleteEmployerInfoPartialState> =
        cityPaginator.state.transform { paging ->
            emit(
                CompleteEmployerInfoPartialState.CityPagingChanged(
                    items = paging.items.toCityPresentation().toImmutableList(),
                    isLoadingFirstPage = paging.isLoadingFirstPage,
                    isLoadingNextPage = paging.isLoadingNextPage,
                    endReached = paging.endReached,
                ),
            )
            paging.error?.let { sendEvent(CompleteEmployerInfoEvent.ShowToast(it.toSingleLineMessage())) }
        }

    // `special-insured-services/cities` only ever filtered by province — the pre-pagination code
    // never sent a city-name filter to it (city text search was purely a client-side filter over
    // the fetched list, same as province's search box below). Server-side name search on this
    // endpoint is unverified, so the search box stays local rather than risking the same 404 the
    // sibling `proxy/models/city/` name filter hit for illDays.
    private fun cityBaseQuery(): ApiQueryParamDN = ApiQueryParamDN(
        filters = CityByProvinceQuery.filters(currentProvinceCode),
    )

    private fun handleChangeLegalNationalId(nid: String): Flow<CompleteEmployerInfoPartialState> = flow {
        val digits = nid.digitsOnly()
        emit(CompleteEmployerInfoPartialState.LegalNationalIdChanged(digits))
        if (digits.length == 11) {
            collectWhileLoading(
                source = getLegalWorkshopUseCase(digits),
                loading = CompleteEmployerInfoPartialState::LegalWorkshopInquiryLoading,
                onSuccess = {
                    CompleteEmployerInfoPartialState.LegalWorkshopInquiryResult(it.name, isError = false)
                },
                onFailure = {
                    CompleteEmployerInfoPartialState.LegalWorkshopInquiryResult(null, isError = true)
                },
            )
        }
    }

    private fun handleChangeCeoNationalId(nid: String): Flow<CompleteEmployerInfoPartialState> = flow {
        val digits = nid.digitsOnly()
        emit(CompleteEmployerInfoPartialState.CeoNationalIdChanged(digits))
        val currentBirthMillis = uiState.value.ceoBirthDateMillis
        if (digits.length == 10 && currentBirthMillis != null) {
            collectCeoInquiry(getLegalWorkshopCeoUseCase(digits, currentBirthMillis))
        }
    }

    private fun handleSelectCeoBirthDate(millis: Long, persianDate: String): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.CeoBirthDateSelected(millis, persianDate))
        val currentCeoNid = uiState.value.ceoNationalId.digitsOnly()
        if (currentCeoNid.length == 10) {
            collectCeoInquiry(getLegalWorkshopCeoUseCase(currentCeoNid, millis))
        }
    }

    private fun handleSelectProvince(province: ProvincePR): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.ProvinceSelected(province))
        currentProvinceCode = province.provinceCode
        cityPaginator.refresh(cityBaseQuery())
    }

    private fun handleSelectCity(city: CityPR): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.CitySelected(city))
        collectWhileLoading(
            source = getBranchesUseCase(city.cityCode),
            loading = CompleteEmployerInfoPartialState::BranchesLoading,
            onSuccess = {
                CompleteEmployerInfoPartialState.BranchesLoaded(it.items.toBranchPresentation().toImmutableList())
            },
            onFailure = { CompleteEmployerInfoPartialState.BranchesLoaded(persistentListOf()) },
        )
    }

    private fun handleSubmitLegalForm(): Flow<CompleteEmployerInfoPartialState> = flow {
        val state = uiState.value
        // Each field prints its own reason, and the button is disabled while any of them does,
        // so there is nothing left to announce here.
        if (state.legalBlockingError != null) return@flow
        if (state.isSubmitting) return@flow
        emit(CompleteEmployerInfoPartialState.Submitting(true))

        requestLegalTicketUseCase(
            mobile = state.legalMobile.digitsOnly(),
            email = state.legalEmail.trim(),
            ceoNationalCode = state.ceoNationalId.digitsOnly(),
        )
            .catch { e ->
                emit(CompleteEmployerInfoPartialState.Submitting(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
            }
            .collect {
                emit(CompleteEmployerInfoPartialState.Submitting(false))
                emit(CompleteEmployerInfoPartialState.StartVerification(VerifyPath.LEGAL))
            }
    }

    private fun handleSubmitRealForm(): Flow<CompleteEmployerInfoPartialState> = flow {
        val state = uiState.value
        // Each field prints its own reason, and the button is disabled while any of them does,
        // so there is nothing left to announce here.
        if (state.realBlockingError != null) return@flow
        if (state.isSubmitting) return@flow
        emit(CompleteEmployerInfoPartialState.Submitting(true))

        requestRealTicketUseCase(
            mobile = state.userMobile.digitsOnly(),
            email = state.userEmail.trim().ifBlank { FALLBACK_TICKET_EMAIL },
        )
            .catch { e ->
                emit(CompleteEmployerInfoPartialState.Submitting(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
            }
            .collect {
                emit(CompleteEmployerInfoPartialState.Submitting(false))
                emit(CompleteEmployerInfoPartialState.StartVerification(VerifyPath.REAL))
            }
    }

    private fun handleSubmitOtp(): Flow<CompleteEmployerInfoPartialState> = flow {
        val state = uiState.value
        val otpDigits = state.otpCode.digitsOnly()

        val blocking = state.otpBlockingError
        if (blocking != null) {
            emit(CompleteEmployerInfoPartialState.OtpValidationFailed(blocking))
            return@flow
        }

        if (state.isSubmitting) return@flow
        emit(CompleteEmployerInfoPartialState.Submitting(true))

        if (state.verifyPath == VerifyPath.LEGAL) {
            val selectedWs = state.selectedWorkshop
            val req = LegalWorkshopInfoRequestDN(
                // `id` is the row's composite key, not a workshop number — the service is
                // addressed with the workshop code and its branch code.
                workshopId = selectedWs?.code.orEmpty(),
                branchCode = selectedWs?.bcode.orEmpty(),
                workshopNationalCode = state.legalNationalId.digitsOnly(),
                legalWorkshopTypeCode = state.selectedCompanyType?.code.orEmpty(),
                ceoNationalId = state.ceoNationalId.digitsOnly(),
                ceoBirthDateMillis = state.ceoBirthDateMillis ?: 0L,
                telephone = state.telephone.digitsOnly(),
                mobile = state.legalMobile.digitsOnly(),
                email = state.legalEmail.trim(),
                ticketCode = otpDigits,
            )
            submitLegalWorkshopInfoUseCase(req)
                .catch { e ->
                    emit(CompleteEmployerInfoPartialState.Submitting(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
                }
                .collect {
                    emit(CompleteEmployerInfoPartialState.Submitting(false))
                    emit(CompleteEmployerInfoPartialState.ShowDialog(CompleteEmployerInfoDialog.SUCCESS_LEGAL))
                }
        } else {
            val req = RealWorkshopInfoRequestDN(
                branchCode = state.selectedBranch?.code.orEmpty(),
                workshopCode = state.realWorkshopCode.digitsOnly(),
                ticketCode = otpDigits,
            )
            submitRealWorkshopInfoUseCase(req)
                .catch { e ->
                    emit(CompleteEmployerInfoPartialState.Submitting(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
                }
                .collect {
                    emit(CompleteEmployerInfoPartialState.Submitting(false))
                    emit(CompleteEmployerInfoPartialState.ShowDialog(CompleteEmployerInfoDialog.SUCCESS_REAL))
                }
        }
    }

    /**
     * The shape every lookup on this screen shares: raise a loading flag, replace it with the
     * result, and on failure fall back to an empty answer while the parsed message goes to the
     * toast host - an empty picker with no explanation reads as a broken screen.
     *
     * `catch` rather than a try/catch around `collect`, so cancelling the screen mid-lookup stays
     * a cancellation instead of being reported as a failure.
     */
    private suspend fun <T> FlowCollector<CompleteEmployerInfoPartialState>.collectWhileLoading(
        source: Flow<T>,
        loading: (Boolean) -> CompleteEmployerInfoPartialState,
        onSuccess: (T) -> CompleteEmployerInfoPartialState,
        onFailure: () -> CompleteEmployerInfoPartialState,
    ) {
        val downstream = this
        downstream.emit(loading(true))
        source
            .catch { error ->
                downstream.emit(onFailure())
                downstream.emit(loading(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
            }
            .collect { value ->
                downstream.emit(onSuccess(value))
                downstream.emit(loading(false))
            }
    }

    /** The manager's identity is looked up from two different fields, on the same terms. */
    private suspend fun FlowCollector<CompleteEmployerInfoPartialState>.collectCeoInquiry(
        source: Flow<LegalWorkshopCeoDN>,
    ) = collectWhileLoading(
        source = source,
        loading = CompleteEmployerInfoPartialState::CeoInquiryLoading,
        onSuccess = { CompleteEmployerInfoPartialState.CeoInquiryResult(it.fullName, isError = false) },
        onFailure = { CompleteEmployerInfoPartialState.CeoInquiryResult(null, isError = true) },
    )

    override fun reduceState(
        currentState: CompleteEmployerInfoUiState,
        partialState: CompleteEmployerInfoPartialState,
    ): CompleteEmployerInfoUiState = when (partialState) {
        is CompleteEmployerInfoPartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is CompleteEmployerInfoPartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is CompleteEmployerInfoPartialState.UserInfoLoaded -> currentState.copy(
            userFullName = partialState.fullName,
            userNationalCode = partialState.nationalCode,
            userMobile = partialState.mobile,
            userEmail = partialState.email,
        )
        is CompleteEmployerInfoPartialState.WorkshopsLoaded -> currentState.copy(workshops = partialState.workshops)
        is CompleteEmployerInfoPartialState.WorkshopExpandedToggled -> {
            val current = currentState.expandedWorkshopIds
            val updated = if (current.contains(partialState.workshopId)) {
                current - partialState.workshopId
            } else {
                current + partialState.workshopId
            }
            currentState.copy(expandedWorkshopIds = updated.toImmutableSet())
        }
        is CompleteEmployerInfoPartialState.OpenLegalForm -> currentState.copy(
            screen = CompleteEmployerInfoScreenState.LEGAL_FORM,
            selectedWorkshop = partialState.workshop,
            legalNationalId = "",
            legalWorkshopName = null,
            selectedCompanyType = null,
            ceoNationalId = "",
            ceoBirthDateMillis = null,
            ceoBirthDatePersian = "",
            ceoFullName = null,
            telephone = "",
            legalMobile = "",
            legalEmail = "",
        )
        is CompleteEmployerInfoPartialState.SwitchTab -> currentState.copy(
            tab = partialState.tab,
        )
        is CompleteEmployerInfoPartialState.LegalNationalIdChanged -> currentState.copy(
            legalNationalId = partialState.nid,
            legalWorkshopName = if (partialState.nid.length < 11) null else currentState.legalWorkshopName,
        )
        is CompleteEmployerInfoPartialState.LegalWorkshopInquiryLoading -> currentState.copy(
            isLegalWorkshopInquiring = partialState.isLoading,
        )
        is CompleteEmployerInfoPartialState.LegalWorkshopInquiryResult -> currentState.copy(
            legalWorkshopName = partialState.name,
        )
        is CompleteEmployerInfoPartialState.CompanyTypeSelected -> currentState.copy(
            selectedCompanyType = partialState.companyType,
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.CeoNationalIdChanged -> currentState.copy(
            ceoNationalId = partialState.nid,
            ceoFullName = if (partialState.nid.length < 10) null else currentState.ceoFullName,
        )
        is CompleteEmployerInfoPartialState.CeoBirthDateSelected -> currentState.copy(
            ceoBirthDateMillis = partialState.millis,
            ceoBirthDatePersian = partialState.persianDate,
        )
        is CompleteEmployerInfoPartialState.CeoInquiryLoading -> currentState.copy(
            isCeoInquiring = partialState.isLoading,
        )
        is CompleteEmployerInfoPartialState.CeoInquiryResult -> currentState.copy(
            ceoFullName = partialState.fullName,
        )
        is CompleteEmployerInfoPartialState.TelephoneChanged -> currentState.copy(
            telephone = partialState.tel,
        )
        is CompleteEmployerInfoPartialState.LegalMobileChanged -> currentState.copy(
            legalMobile = partialState.mobile,
        )
        is CompleteEmployerInfoPartialState.LegalEmailChanged -> currentState.copy(
            legalEmail = partialState.email,
        )
        is CompleteEmployerInfoPartialState.RealWorkshopCodeChanged -> currentState.copy(
            realWorkshopCode = partialState.code,
        )
        is CompleteEmployerInfoPartialState.ProvincePagingChanged -> currentState.copy(
            provinces = partialState.items,
            isProvincesLoading = partialState.isLoadingFirstPage,
            isProvincesLoadingMore = partialState.isLoadingNextPage,
            canLoadMoreProvinces = !partialState.endReached,
        )
        is CompleteEmployerInfoPartialState.ProvinceSelected -> currentState.copy(
            selectedProvince = partialState.province,
            selectedCity = null,
            selectedBranch = null,
            cities = persistentListOf(),
            branches = persistentListOf(),
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.CityPagingChanged -> currentState.copy(
            cities = partialState.items,
            isCitiesLoading = partialState.isLoadingFirstPage,
            isCitiesLoadingMore = partialState.isLoadingNextPage,
            canLoadMoreCities = !partialState.endReached,
        )
        is CompleteEmployerInfoPartialState.CitySelected -> currentState.copy(
            selectedCity = partialState.city,
            selectedBranch = null,
            branches = persistentListOf(),
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.BranchesLoading -> currentState.copy(
            isBranchesLoading = partialState.isLoading,
        )
        is CompleteEmployerInfoPartialState.BranchesLoaded -> currentState.copy(
            branches = partialState.branches,
            isBranchesLoading = false,
        )
        is CompleteEmployerInfoPartialState.BranchSelected -> currentState.copy(
            selectedBranch = partialState.branch,
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.OpenSheet -> currentState.copy(
            activeBottomSheet = partialState.sheet,
        )
        is CompleteEmployerInfoPartialState.DismissSheet -> currentState.copy(
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.StartVerification -> currentState.copy(
            isVerifying = true,
            verifyPath = partialState.path,
            otpCode = "",
            hasAttemptedOtpSubmit = false,
            otpValidationError = null,
        )
        is CompleteEmployerInfoPartialState.OtpCodeChanged -> currentState.copy(
            otpCode = partialState.code,
            otpValidationError = null,
        )
        is CompleteEmployerInfoPartialState.OtpValidationFailed -> currentState.copy(
            hasAttemptedOtpSubmit = true,
            otpValidationError = partialState.error,
        )
        is CompleteEmployerInfoPartialState.BackFromVerification -> currentState.copy(
            isVerifying = false,
            otpCode = "",
            hasAttemptedOtpSubmit = false,
            otpValidationError = null,
        )
        is CompleteEmployerInfoPartialState.BackToList -> currentState.copy(
            screen = CompleteEmployerInfoScreenState.LIST,
            isVerifying = false,
            selectedWorkshop = null,
        )
        is CompleteEmployerInfoPartialState.ShowDialog -> currentState.copy(
            dialogState = partialState.dialog,
            isVerifying = false,
        )
        is CompleteEmployerInfoPartialState.DismissDialog -> {
            val wasLegalSuccess = currentState.dialogState == CompleteEmployerInfoDialog.SUCCESS_LEGAL
            currentState.copy(
                dialogState = null,
                screen = if (wasLegalSuccess) CompleteEmployerInfoScreenState.LIST else currentState.screen,
            )
        }
        is CompleteEmployerInfoPartialState.Error -> currentState.copy(
            generalError = partialState.message,
            isLoading = false,
        )
        is CompleteEmployerInfoPartialState.ErrorDismissed -> currentState.copy(generalError = null)
    }

    override fun createErrorState(message: String): CompleteEmployerInfoPartialState =
        CompleteEmployerInfoPartialState.Error(message)
}

/** What the old app sends when the account carries no email; the ticket service rejects a blank. */
private const val FALLBACK_TICKET_EMAIL = "tamin@tamin.ir"

private const val CITIES_PER_PROVINCE_PAGE_SIZE = 200

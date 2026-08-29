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
import com.tamin.taminhamrah.mapper.employerInfo.toWorkshopItemPR
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.useCases.common.GetCitiesByProvinceUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.employerInfo.GetLegalWorkshopCeoUseCase
import com.tamin.taminhamrah.useCases.employerInfo.GetLegalWorkshopUseCase
import com.tamin.taminhamrah.useCases.employerInfo.RequestLegalTicketUseCase
import com.tamin.taminhamrah.useCases.employerInfo.RequestRealTicketUseCase
import com.tamin.taminhamrah.useCases.employerInfo.SubmitLegalWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.employerInfo.SubmitRealWorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_err_ceo_birth
import taminx.core.core_ui.employer_info_err_ceo_nid
import taminx.core.core_ui.employer_info_err_company_type
import taminx.core.core_ui.employer_info_err_email
import taminx.core.core_ui.employer_info_err_legal_nid
import taminx.core.core_ui.employer_info_err_mobile
import taminx.core.core_ui.employer_info_err_otp_code
import taminx.core.core_ui.employer_info_err_province_city_branch
import taminx.core.core_ui.employer_info_err_ws_code

class CompleteEmployerInfoViewModel(
    private val getAllEmployerAgreementUseCase: GetAllEmployerAgreementByNationalIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLegalWorkshopUseCase: GetLegalWorkshopUseCase,
    private val getLegalWorkshopCeoUseCase: GetLegalWorkshopCeoUseCase,
    private val requestLegalTicketUseCase: RequestLegalTicketUseCase,
    private val submitLegalWorkshopInfoUseCase: SubmitLegalWorkshopInfoUseCase,
    private val requestRealTicketUseCase: RequestRealTicketUseCase,
    private val submitRealWorkshopInfoUseCase: SubmitRealWorkshopInfoUseCase,
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getCitiesByProvinceUseCase: GetCitiesByProvinceUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
) : BaseViewModel<
    CompleteEmployerInfoUiState,
    CompleteEmployerInfoPartialState,
    CompleteEmployerInfoEvent,
    CompleteEmployerInfoIntent,
>(CompleteEmployerInfoUiState()) {

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
            is CompleteEmployerInfoIntent.SelectCity -> handleSelectCity(intent.city)
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

    private fun handleLoadInitialData(): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.Loading(true))

        // Not swallowed: the real path sends its ticket to the mobile and email that come from
        // here, so a silent failure would post a ticket request with two empty contacts and leave
        // the person staring at a server error that names nothing.
        try {
            getUserProfileUseCase().collect { userProfile ->
                emit(
                    CompleteEmployerInfoPartialState.UserInfoLoaded(
                        fullName = listOfNotNull(userProfile.firstName, userProfile.lastName).joinToString(" ").trim(),
                        nationalCode = userProfile.nationalCode.orEmpty(),
                        mobile = userProfile.mobile.orEmpty(),
                        email = userProfile.email.orEmpty(),
                    )
                )
            }
        } catch (e: Exception) {
            sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
        }

        try {
            val agreements = getAllEmployerAgreementUseCase(emptyList())
            val items = agreements?.list?.map { it.toWorkshopItemPR() }.orEmpty()
            emit(CompleteEmployerInfoPartialState.WorkshopsLoaded(items.toImmutableList()))
        } catch (e: Exception) {
            emit(CompleteEmployerInfoPartialState.Error(e.toSingleLineMessage()))
        }

        try {
            getProvincesUseCase().collect { provinces ->
                emit(
                    CompleteEmployerInfoPartialState.ProvincesLoaded(
                        provinces.toProvincePresentation().toImmutableList()
                    )
                )
            }
        } catch (e: Exception) {
            // A toast rather than the list's error state: the province picker sits behind the
            // second tab, and blanking the workshop list for it would hide working content.
            sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
        }

        emit(CompleteEmployerInfoPartialState.Loading(false))
    }

    private fun handleChangeLegalNationalId(nid: String): Flow<CompleteEmployerInfoPartialState> = flow {
        val digits = nid.digitsOnly()
        emit(CompleteEmployerInfoPartialState.LegalNationalIdChanged(digits))
        if (digits.length == 11) {
            emit(CompleteEmployerInfoPartialState.LegalWorkshopInquiryLoading(true))
            getLegalWorkshopUseCase(digits)
                .catch { error ->
                    emit(CompleteEmployerInfoPartialState.LegalWorkshopInquiryResult(null, isError = true))
                    emit(CompleteEmployerInfoPartialState.LegalWorkshopInquiryLoading(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
                }
                .collect { result ->
                    emit(CompleteEmployerInfoPartialState.LegalWorkshopInquiryResult(result.name, isError = false))
                    emit(CompleteEmployerInfoPartialState.LegalWorkshopInquiryLoading(false))
                }
        }
    }

    private fun handleChangeCeoNationalId(nid: String): Flow<CompleteEmployerInfoPartialState> = flow {
        val digits = nid.digitsOnly()
        emit(CompleteEmployerInfoPartialState.CeoNationalIdChanged(digits))
        val currentBirthMillis = uiState.value.ceoBirthDateMillis
        if (digits.length == 10 && currentBirthMillis != null) {
            emit(CompleteEmployerInfoPartialState.CeoInquiryLoading(true))
            getLegalWorkshopCeoUseCase(digits, currentBirthMillis)
                .catch { error ->
                    emit(CompleteEmployerInfoPartialState.CeoInquiryResult(null, isError = true))
                    emit(CompleteEmployerInfoPartialState.CeoInquiryLoading(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
                }
                .collect { result ->
                    emit(CompleteEmployerInfoPartialState.CeoInquiryResult(result.fullName, isError = false))
                    emit(CompleteEmployerInfoPartialState.CeoInquiryLoading(false))
                }
        }
    }

    private fun handleSelectCeoBirthDate(millis: Long, persianDate: String): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.CeoBirthDateSelected(millis, persianDate))
        val currentCeoNid = uiState.value.ceoNationalId.digitsOnly()
        if (currentCeoNid.length == 10) {
            emit(CompleteEmployerInfoPartialState.CeoInquiryLoading(true))
            getLegalWorkshopCeoUseCase(currentCeoNid, millis)
                .catch { error ->
                    emit(CompleteEmployerInfoPartialState.CeoInquiryResult(null, isError = true))
                    emit(CompleteEmployerInfoPartialState.CeoInquiryLoading(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
                }
                .collect { result ->
                    emit(CompleteEmployerInfoPartialState.CeoInquiryResult(result.fullName, isError = false))
                    emit(CompleteEmployerInfoPartialState.CeoInquiryLoading(false))
                }
        }
    }

    private fun handleSelectProvince(province: com.tamin.taminhamrah.model.common.ProvincePR): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.ProvinceSelected(province))
        emit(CompleteEmployerInfoPartialState.CitiesLoading(true))
        getCitiesByProvinceUseCase(province.provinceCode)
            .catch { error ->
                // An empty picker with no explanation reads as a broken screen, so the parsed
                // message goes to the toast host rather than being dropped with the list.
                emit(CompleteEmployerInfoPartialState.CitiesLoaded(persistentListOf()))
                emit(CompleteEmployerInfoPartialState.CitiesLoading(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
            }
            .collect { result ->
                emit(CompleteEmployerInfoPartialState.CitiesLoaded(result.cities.toCityPresentation().toImmutableList()))
                emit(CompleteEmployerInfoPartialState.CitiesLoading(false))
            }
    }

    private fun handleSelectCity(city: com.tamin.taminhamrah.model.common.CityPR): Flow<CompleteEmployerInfoPartialState> = flow {
        emit(CompleteEmployerInfoPartialState.CitySelected(city))
        emit(CompleteEmployerInfoPartialState.BranchesLoading(true))
        getBranchesUseCase(city.cityCode)
            .catch { error ->
                emit(CompleteEmployerInfoPartialState.BranchesLoaded(persistentListOf()))
                emit(CompleteEmployerInfoPartialState.BranchesLoading(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(error.toSingleLineMessage()))
            }
            .collect { branches ->
                emit(CompleteEmployerInfoPartialState.BranchesLoaded(branches.toBranchPresentation().toImmutableList()))
                emit(CompleteEmployerInfoPartialState.BranchesLoading(false))
            }
    }

    private fun handleSubmitLegalForm(): Flow<CompleteEmployerInfoPartialState> = flow {
        val state = uiState.value
        val nidDigits = state.legalNationalId.digitsOnly()
        val ceoDigits = state.ceoNationalId.digitsOnly()
        val mobileDigits = state.legalMobile.digitsOnly()
        val email = state.legalEmail.trim()

        if (nidDigits.length != 11) {
            emit(CompleteEmployerInfoPartialState.LegalValidationFailed(Res.string.employer_info_err_legal_nid))
            return@flow
        }
        if (state.selectedCompanyType == null) {
            emit(CompleteEmployerInfoPartialState.LegalValidationFailed(Res.string.employer_info_err_company_type))
            return@flow
        }
        if (ceoDigits.length != 10) {
            emit(CompleteEmployerInfoPartialState.LegalValidationFailed(Res.string.employer_info_err_ceo_nid))
            return@flow
        }
        if (state.ceoBirthDateMillis == null) {
            emit(CompleteEmployerInfoPartialState.LegalValidationFailed(Res.string.employer_info_err_ceo_birth))
            return@flow
        }
        if (!mobileDigits.matches(Regex("^09\\d{9}$"))) {
            emit(CompleteEmployerInfoPartialState.LegalValidationFailed(Res.string.employer_info_err_mobile))
            return@flow
        }
        if (!email.matches(Regex("^\\S+@\\S+\\.\\S+$"))) {
            emit(CompleteEmployerInfoPartialState.LegalValidationFailed(Res.string.employer_info_err_email))
            return@flow
        }

        if (state.isLoading) return@flow
        emit(CompleteEmployerInfoPartialState.Loading(true))

        requestLegalTicketUseCase(mobile = mobileDigits, email = email, ceoNationalCode = ceoDigits)
            .catch { e ->
                emit(CompleteEmployerInfoPartialState.Loading(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
            }
            .collect {
                emit(CompleteEmployerInfoPartialState.Loading(false))
                emit(CompleteEmployerInfoPartialState.StartVerification(VerifyPath.LEGAL))
            }
    }

    private fun handleSubmitRealForm(): Flow<CompleteEmployerInfoPartialState> = flow {
        val state = uiState.value
        val wsDigits = state.realWorkshopCode.digitsOnly()

        if (wsDigits.length != 10) {
            emit(CompleteEmployerInfoPartialState.RealValidationFailed(Res.string.employer_info_err_ws_code))
            return@flow
        }
        if (state.selectedProvince == null || state.selectedCity == null || state.selectedBranch == null) {
            emit(CompleteEmployerInfoPartialState.RealValidationFailed(Res.string.employer_info_err_province_city_branch))
            return@flow
        }

        if (state.isLoading) return@flow
        emit(CompleteEmployerInfoPartialState.Loading(true))

        val email = state.userEmail.ifBlank { "tamin@tamin.ir" }
        val mobile = state.userMobile

        requestRealTicketUseCase(mobile = mobile, email = email)
            .catch { e ->
                emit(CompleteEmployerInfoPartialState.Loading(false))
                sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
            }
            .collect {
                emit(CompleteEmployerInfoPartialState.Loading(false))
                emit(CompleteEmployerInfoPartialState.StartVerification(VerifyPath.REAL))
            }
    }

    private fun handleSubmitOtp(): Flow<CompleteEmployerInfoPartialState> = flow {
        val state = uiState.value
        val otpDigits = state.otpCode.digitsOnly()

        if (otpDigits.length < 5) {
            emit(CompleteEmployerInfoPartialState.OtpValidationFailed(Res.string.employer_info_err_otp_code))
            return@flow
        }

        if (state.isLoading) return@flow
        emit(CompleteEmployerInfoPartialState.Loading(true))

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
                    emit(CompleteEmployerInfoPartialState.Loading(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
                }
                .collect {
                    emit(CompleteEmployerInfoPartialState.Loading(false))
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
                    emit(CompleteEmployerInfoPartialState.Loading(false))
                    sendEvent(CompleteEmployerInfoEvent.ShowToast(e.toSingleLineMessage()))
                }
                .collect {
                    emit(CompleteEmployerInfoPartialState.Loading(false))
                    emit(CompleteEmployerInfoPartialState.ShowDialog(CompleteEmployerInfoDialog.SUCCESS_REAL))
                }
        }
    }

    override fun reduceState(
        currentState: CompleteEmployerInfoUiState,
        partialState: CompleteEmployerInfoPartialState,
    ): CompleteEmployerInfoUiState = when (partialState) {
        is CompleteEmployerInfoPartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
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
            hasAttemptedLegalSubmit = false,
            legalValidationError = null,
        )
        is CompleteEmployerInfoPartialState.SwitchTab -> currentState.copy(
            tab = partialState.tab,
            realValidationError = null,
            hasAttemptedRealSubmit = false,
        )
        is CompleteEmployerInfoPartialState.LegalNationalIdChanged -> currentState.copy(
            legalNationalId = partialState.nid,
            legalValidationError = null,
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
            legalValidationError = null,
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.CeoNationalIdChanged -> currentState.copy(
            ceoNationalId = partialState.nid,
            legalValidationError = null,
            ceoFullName = if (partialState.nid.length < 10) null else currentState.ceoFullName,
        )
        is CompleteEmployerInfoPartialState.CeoBirthDateSelected -> currentState.copy(
            ceoBirthDateMillis = partialState.millis,
            ceoBirthDatePersian = partialState.persianDate,
            legalValidationError = null,
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
            legalValidationError = null,
        )
        is CompleteEmployerInfoPartialState.LegalEmailChanged -> currentState.copy(
            legalEmail = partialState.email,
            legalValidationError = null,
        )
        is CompleteEmployerInfoPartialState.LegalValidationFailed -> currentState.copy(
            hasAttemptedLegalSubmit = true,
            legalValidationError = partialState.error,
        )
        is CompleteEmployerInfoPartialState.RealWorkshopCodeChanged -> currentState.copy(
            realWorkshopCode = partialState.code,
            realValidationError = null,
        )
        is CompleteEmployerInfoPartialState.ProvincesLoaded -> currentState.copy(
            provinces = partialState.provinces,
        )
        is CompleteEmployerInfoPartialState.ProvinceSelected -> currentState.copy(
            selectedProvince = partialState.province,
            selectedCity = null,
            selectedBranch = null,
            cities = persistentListOf(),
            branches = persistentListOf(),
            realValidationError = null,
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.CitiesLoading -> currentState.copy(
            isCitiesLoading = partialState.isLoading,
        )
        is CompleteEmployerInfoPartialState.CitiesLoaded -> currentState.copy(
            cities = partialState.cities,
            isCitiesLoading = false,
        )
        is CompleteEmployerInfoPartialState.CitySelected -> currentState.copy(
            selectedCity = partialState.city,
            selectedBranch = null,
            branches = persistentListOf(),
            realValidationError = null,
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
            realValidationError = null,
            activeBottomSheet = null,
        )
        is CompleteEmployerInfoPartialState.RealValidationFailed -> currentState.copy(
            hasAttemptedRealSubmit = true,
            realValidationError = partialState.error,
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
            hasAttemptedLegalSubmit = false,
            legalValidationError = null,
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

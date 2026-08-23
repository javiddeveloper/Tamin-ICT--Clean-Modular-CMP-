package com.tamin.taminhamrah.feature.girlSurvivor.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorUiState.PartialState
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.*
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.personal.CheckGirlSurvivorConditionsUseCase
import com.tamin.taminhamrah.useCases.personal.ConfirmGirlSurvivorUseCase
import com.tamin.taminhamrah.useCases.personal.GetGirlSurvivorReportUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GirlSurvivorViewModel(
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase,
    private val checkGirlSurvivorConditionsUseCase: CheckGirlSurvivorConditionsUseCase,
    private val getGirlSurvivorReportUseCase: GetGirlSurvivorReportUseCase,
    private val confirmGirlSurvivorUseCase: ConfirmGirlSurvivorUseCase,
) : BaseViewModel<GirlSurvivorUiState, PartialState, GirlSurvivorEvent, GirlSurvivorIntent>(
    initialState = GirlSurvivorUiState()
) {

    init {
        sendIntent(GirlSurvivorIntent.Init)
    }

    override fun handleIntent(intent: GirlSurvivorIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(GirlSurvivorEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: GirlSurvivorIntent): Flow<PartialState> = flow {
        when (intent) {
            GirlSurvivorIntent.Init -> loadPersonalInfo()
            is GirlSurvivorIntent.AddressChanged -> {
                emit(PartialState.AddressChanged(intent.value))
                clearFieldError { it.copy(address = null) }
            }
            is GirlSurvivorIntent.ZipCodeChanged -> {
                val value = intent.value.filter(Char::isDigit).take(10)
                emit(PartialState.ZipCodeChanged(value))
                if (value.length == 10) clearFieldError { it.copy(zipCode = null) }
            }
            is GirlSurvivorIntent.PhoneNumberChanged -> {
                val value = intent.value.filter(Char::isDigit).take(11)
                emit(PartialState.PhoneNumberChanged(value))
                if (value.length == 11) clearFieldError { it.copy(phoneNumber = null) }
            }
            is GirlSurvivorIntent.DeceasedNationalCodeChanged -> {
                val value = intent.value.filter(Char::isDigit).take(10)
                emit(PartialState.DeceasedNationalCodeChanged(value))
                if (value.length == 10) clearFieldError { it.copy(deceasedNationalCode = null) }
            }
            is GirlSurvivorIntent.DeceasedPensionIdChanged -> {
                val value = intent.value.filter(Char::isDigit).take(10)
                emit(PartialState.DeceasedPensionIdChanged(value))
                if (value.length == 10) clearFieldError { it.copy(deceasedPensionId = null) }
            }
            is GirlSurvivorIntent.UsePensionIdModeChanged -> {
                emit(PartialState.UsePensionIdModeChanged(intent.enabled))
                emit(PartialState.DeceasedNationalCodeChanged(""))
                emit(PartialState.DeceasedPensionIdChanged(""))
                emit(PartialState.FieldErrorsChanged(GirlSurvivorFieldErrors()))
            }
            GirlSurvivorIntent.DownloadAndViewForm -> downloadAndViewForm()
            GirlSurvivorIntent.RetryPdfDownload -> downloadReportPdf()
            GirlSurvivorIntent.DismissPdfViewer -> {
                val hadPdf = uiState.value.viewerPdf != null
                emit(PartialState.ViewerPdfChanged(null))
                if (hadPdf) {
                    emit(PartialState.StepChanged(GirlSurvivorStep.Commitment))
                }
            }
            is GirlSurvivorIntent.PdfConfirmedChanged -> {
                emit(PartialState.PdfConfirmedChanged(intent.confirmed))
            }
            GirlSurvivorIntent.SubmitRequest -> submitRequest()
            GirlSurvivorIntent.GoToPreviousStep -> {
                emit(PartialState.StepChanged(GirlSurvivorStep.Details))
            }
            GirlSurvivorIntent.DismissSuccessDialog -> {
                emit(PartialState.ShowSuccessDialog(false))
                sendEvent(GirlSurvivorEvent.NavigateBack)
            }
        }
    }

    override fun reduceState(
        currentState: GirlSurvivorUiState,
        partialState: PartialState
    ): GirlSurvivorUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, viewerDownloadFailed = false)
        is PartialState.ProfileLoading -> currentState.copy(isProfileLoading = partialState.isProfileLoading)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.ProfileLoaded -> currentState.copy(
            profileRows = partialState.rows,
            fullName = partialState.fullName,
            fatherName = partialState.fatherName,
            nationalId = partialState.nationalId,
            phoneNumber = partialState.phoneNumber,
            confirmPayload = partialState.confirmPayloadBase,
            isProfileLoading = false,
            isLoading = false,
        )
        is PartialState.AddressChanged -> currentState.copy(address = partialState.value)
        is PartialState.ZipCodeChanged -> currentState.copy(zipCode = partialState.value)
        is PartialState.PhoneNumberChanged -> currentState.copy(phoneNumber = partialState.value)
        is PartialState.DeceasedNationalCodeChanged -> currentState.copy(deceasedNationalCode = partialState.value)
        is PartialState.DeceasedPensionIdChanged -> currentState.copy(deceasedPensionId = partialState.value)
        is PartialState.UsePensionIdModeChanged -> currentState.copy(usePensionIdMode = partialState.enabled)
        is PartialState.FieldErrorsChanged -> currentState.copy(fieldErrors = partialState.errors)
        is PartialState.ViewerPdfChanged -> currentState.copy(
            viewerPdf = partialState.pdf,
            isLoading = false,
            viewerDownloadFailed = false,
        )
        PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
        is PartialState.PdfConfirmedChanged -> currentState.copy(isPdfConfirmed = partialState.confirmed)
        is PartialState.ConfirmPayloadUpdated -> currentState.copy(confirmPayload = partialState.payload)
        is PartialState.ShowSuccessDialog -> currentState.copy(showSuccessDialog = partialState.show, isSubmitting = false)
        is PartialState.Error -> currentState.copy(
            isProfileLoading = false,
            isLoading = false,
            isSubmitting = false,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.loadPersonalInfo() {
        emit(PartialState.ProfileLoading(true))
        try {
            getPersonalInfoUseCase(refreshRemote = true).collect { info ->
                if (info == null) return@collect
                val personal = info.personal
                val fullName = listOfNotNull(personal?.firstName, personal?.lastName)
                    .joinToString(" ")
                    .ifBlank { "-" }
                val rows = buildProfileRows(info)
                emit(
                    PartialState.ProfileLoaded(
                        rows = rows,
                        fullName = fullName,
                        fatherName = personal?.fatherName.orEmpty(),
                        nationalId = personal?.nationalId.orEmpty(),
                        phoneNumber = info.mobileNumber.orEmpty(),
                        confirmPayloadBase = buildConfirmPayload(info),
                    )
                )
            }
        } catch (e: Exception) {
            emit(PartialState.ProfileLoading(false))
            sendEvent(GirlSurvivorEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private suspend fun FlowCollector<PartialState>.downloadAndViewForm() {
        val state = uiState.value
        if (state.isLoading) return

        val validation = validateInput(state)
        if (validation != null) {
            emit(PartialState.FieldErrorsChanged(validation))
            return
        }

        emit(PartialState.Loading(true))
        checkGirlSurvivorConditionsUseCase(
            nationalCode = state.deceasedNationalCode,
            pensionerId = state.deceasedPensionId,
        ).collect {
            val updatedPayload = state.confirmPayload?.copy(
                address = state.address,
                phoneNumber = state.phoneNumber,
                nationalCode = state.deceasedNationalCode.ifBlank { null },
                pensionId = state.deceasedPensionId.ifBlank { null },
            )
            if (updatedPayload != null) {
                emit(PartialState.ConfirmPayloadUpdated(updatedPayload))
            }
            downloadReportPdf()
        }
    }

    private suspend fun FlowCollector<PartialState>.downloadReportPdf() {
        val state = uiState.value
        if (state.isLoading && state.viewerPdf == null) {
            emit(PartialState.Loading(true))
        }
        emit(PartialState.ViewerPdfChanged(null))
        try {
            val params = GirlSurvivorReportParamsDN(
                address = state.address,
                tel = state.phoneNumber,
                postalCode = state.zipCode,
                fatherName = state.fatherName.ifBlank { null },
                birthDate = state.confirmPayload?.birthDate,
                insuranceId = state.confirmPayload?.insuranceNumber,
                parentCode = state.deceasedNationalCode,
                pensionerId = state.deceasedPensionId,
            )
            getGirlSurvivorReportUseCase(params).collect { pdf ->
                emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
                sendEvent(GirlSurvivorEvent.OpenPdfViewer)
            }
        } catch (e: Exception) {
            emit(PartialState.ViewerDownloadFailed)
            sendEvent(GirlSurvivorEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private suspend fun FlowCollector<PartialState>.submitRequest() {
        val state = uiState.value
        if (state.isSubmitting || state.isLoading) return
        if (!state.isPdfConfirmed) return

        val payload = state.confirmPayload?.copy(
            address = state.address,
            phoneNumber = state.phoneNumber,
            nationalCode = state.deceasedNationalCode.ifBlank { null },
            pensionId = state.deceasedPensionId.ifBlank { null },
        ) ?: return

        emit(PartialState.Submitting(true))
        confirmGirlSurvivorUseCase(payload).collect {
            emit(PartialState.ShowSuccessDialog(true))
        }
    }

    private fun buildProfileRows(info: PersonalInfoDN): ImmutableList<GirlSurvivorProfileRowPR> {
        val personal = info.personal
        val fullName = listOfNotNull(personal?.firstName, personal?.lastName).joinToString(" ").ifBlank { "-" }
        return persistentListOf(
            GirlSurvivorProfileRowPR("نام و نام خانوادگی", fullName),
            GirlSurvivorProfileRowPR("نام پدر", personal?.fatherName ?: "-"),
            GirlSurvivorProfileRowPR("کد ملی", personal?.nationalId ?: "-"),
            GirlSurvivorProfileRowPR("شماره بیمه", info.insuranceId ?: "-"),
            GirlSurvivorProfileRowPR(
                "تاریخ تولد",
                PersianDateFormatter.formatTimestamp(personal?.dateOfBirth).ifBlank { "-" }
            ),
            GirlSurvivorProfileRowPR("شماره همراه", info.mobileNumber.orEmpty()),
        )
    }

    private fun buildConfirmPayload(info: PersonalInfoDN): ConfirmGirlSurvivorDN {
        val personal = info.personal
        return ConfirmGirlSurvivorDN(
            age = HARDCODED_AGE,
            birthDate = personal?.dateOfBirth,
            childInsuranceId = info.insuranceId,
            childNationalId = personal?.nationalId,
            deathDate = personal?.dateOfDead,
            deathType = null,
            dependencyType = DependencyTypeDN(code = DAUGHTER_DEPENDENCY_CODE),
            firstName = personal?.firstName,
            gender = personal?.genderCode,
            idNumber = personal?.idCardNumber,
            insuranceNumber = info.insuranceId,
            lastName = personal?.lastName,
            mobileNumber = info.mobileNumber,
            status = HARDCODED_STATUS,
        )
    }

    private fun validateInput(state: GirlSurvivorUiState): GirlSurvivorFieldErrors? {
        if (state.address.isBlank()) {
            return GirlSurvivorFieldErrors(address = ERROR_ENTER_ADDRESS)
        }
        if (state.zipCode.isBlank()) {
            return GirlSurvivorFieldErrors(zipCode = ERROR_ENTER_ZIP_CODE)
        }
        if (state.zipCode.length < 10) {
            return GirlSurvivorFieldErrors(zipCode = ERROR_NOT_VALID_ZIP_CODE)
        }
        if (state.phoneNumber.isBlank()) {
            return GirlSurvivorFieldErrors(phoneNumber = ERROR_ENTER_PHONE)
        }
        if (!state.phoneNumber.startsWith('0') || state.phoneNumber.length > 11) {
            return GirlSurvivorFieldErrors(phoneNumber = ERROR_NOT_VALID_PHONE)
        }
        if (state.usePensionIdMode) {
            if (state.deceasedPensionId.length < 10) {
                return GirlSurvivorFieldErrors(
                    deceasedPensionId = if (state.deceasedPensionId.isBlank()) {
                        ERROR_ENTER_PENSION_NUM
                    } else {
                        ERROR_NOT_VALID_PENSION_ID
                    }
                )
            }
        } else if (state.deceasedNationalCode.isBlank()) {
            return GirlSurvivorFieldErrors(deceasedNationalCode = ERROR_NOT_VALID_NATIONAL_ID)
        }
        return null
    }

    private suspend fun FlowCollector<PartialState>.clearFieldError(
        transform: (GirlSurvivorFieldErrors) -> GirlSurvivorFieldErrors
    ) {
        emit(PartialState.FieldErrorsChanged(transform(uiState.value.fieldErrors)))
    }

    private companion object {
        const val HARDCODED_AGE = "33"
        const val HARDCODED_STATUS = "0"
        const val DAUGHTER_DEPENDENCY_CODE = "04"

        const val ERROR_ENTER_ADDRESS = "لطفا آدرس را وارد نمایید"
        const val ERROR_ENTER_ZIP_CODE = "لطفا کد پستی را وارد نمایید"
        const val ERROR_NOT_VALID_ZIP_CODE = "کدپستی معتبر نیست"
        const val ERROR_ENTER_PHONE = "لطفا شماره تلفن را وارد نمایید"
        const val ERROR_NOT_VALID_PHONE = "شماره تلفن معتبر نیست"
        const val ERROR_NOT_VALID_PENSION_ID = "شماره مستمری معتبر نیست"
        const val ERROR_NOT_VALID_NATIONAL_ID = "کد ملی معتبر نیست"
        const val ERROR_ENTER_PENSION_NUM = "لطفا شماره مستمری را وارد نمایید"
    }
}

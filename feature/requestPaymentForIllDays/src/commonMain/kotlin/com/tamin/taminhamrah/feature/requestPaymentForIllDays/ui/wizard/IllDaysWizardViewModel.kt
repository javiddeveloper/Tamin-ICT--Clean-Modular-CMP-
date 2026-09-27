package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardUiState.PartialState
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.requestPaymentForIllDays.toPresentation
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestFileDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.query.city.CityListQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.common.GetCitiesPageUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetCovidResultUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetIllDaysInsuredMainInfoUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.SendRequestForIllDayUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.ill_days_doc_duplicate_error
import taminx.core.core_ui.ill_days_doc_format_error
import taminx.core.core_ui.ill_days_doc_max_count_error
import taminx.core.core_ui.ill_days_doc_min_count_error
import taminx.core.core_ui.ill_days_doc_title
import taminx.core.core_ui.ill_days_error_covid_dates_missing
import taminx.core.core_ui.ill_days_error_info_not_loaded
import taminx.core.core_ui.ill_days_error_rest_days_too_long
import taminx.core.core_ui.ill_days_submit_missing_data
import taminx.core.core_ui.ill_days_submit_success_fallback
import kotlin.math.max

class IllDaysWizardViewModel(
    private val getIllDaysInsuredMainInfoUseCase: GetIllDaysInsuredMainInfoUseCase,
    private val getCitiesPageUseCase: GetCitiesPageUseCase,
    private val getCovidResultUseCase: GetCovidResultUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val sendRequestForIllDayUseCase: SendRequestForIllDayUseCase,
) : BaseViewModel<IllDaysWizardUiState, PartialState, IllDaysWizardEvent, IllDaysWizardIntent>(
    initialState = IllDaysWizardUiState(),
) {
    // Offline-first: collect the whole flow (cached page, then network page) — not `.first()`.
    private val cityPaginator = Paginator(
        loadPages = { query -> getCitiesPageUseCase(query) },
    )

    init {
        sendIntent(IllDaysWizardIntent.Load)
    }

    override fun handleIntent(intent: IllDaysWizardIntent): Flow<PartialState> = flow {
        when (intent) {
            IllDaysWizardIntent.Load,
            IllDaysWizardIntent.Retry -> emitAll(merge(loadMainInfo(), observeCityPaging()))
            IllDaysWizardIntent.OpenBranchPicker ->
                emit(PartialState.PickerChanged(IllDaysWizardPicker.Branch))
            IllDaysWizardIntent.OpenCityPicker ->
                emit(PartialState.PickerChanged(IllDaysWizardPicker.City))
            IllDaysWizardIntent.DismissPicker ->
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            is IllDaysWizardIntent.BranchPicked -> {
                emit(PartialState.BranchSelected(intent.branch))
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            is IllDaysWizardIntent.CityPicked -> {
                emit(PartialState.CitySelected(intent.city))
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            is IllDaysWizardIntent.CitySearchQueryChanged ->
                cityPaginator.refresh(cityBaseQuery(intent.query))
            IllDaysWizardIntent.CityPickerLoadMore -> cityPaginator.loadNext()
            IllDaysWizardIntent.NextStep -> handleNext()
            IllDaysWizardIntent.PreviousStep -> handlePrevious()
            is IllDaysWizardIntent.CovidChanged -> handleCovidToggle(intent.enabled)
            IllDaysWizardIntent.OpenStartDatePicker -> {
                if (!uiState.value.isCovid) {
                    emit(PartialState.PickerChanged(IllDaysWizardPicker.StartDate))
                }
            }
            IllDaysWizardIntent.OpenEndDatePicker -> {
                if (!uiState.value.isCovid) {
                    emit(PartialState.PickerChanged(IllDaysWizardPicker.EndDate))
                }
            }
            is IllDaysWizardIntent.StartDatePicked -> {
                val end = uiState.value.endDateMillis
                emit(
                    PartialState.RestDatesSet(
                        startMillis = intent.millis,
                        startLabel = intent.label,
                        endMillis = end,
                        endLabel = uiState.value.endDateLabel,
                        dayCount = computeDayCount(intent.millis, end),
                    )
                )
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            is IllDaysWizardIntent.EndDatePicked -> {
                val start = uiState.value.startDateMillis
                emit(
                    PartialState.RestDatesSet(
                        startMillis = start,
                        startLabel = uiState.value.startDateLabel,
                        endMillis = intent.millis,
                        endLabel = intent.label,
                        dayCount = computeDayCount(start, intent.millis),
                    )
                )
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            is IllDaysWizardIntent.DoctorNameChanged ->
                emit(PartialState.DoctorNameChanged(intent.value))
            is IllDaysWizardIntent.DoctorCodeChanged ->
                emit(PartialState.DoctorCodeChanged(intent.value.filter { it.isDigit() }.take(DOCTOR_CODE_MAX_LENGTH)))
            is IllDaysWizardIntent.MedicalRecordChanged ->
                emit(PartialState.MedicalRecordToggled(intent.enabled))
            IllDaysWizardIntent.OpenDocumentSource -> {
                if (uiState.value.documents.size >= MAX_DOCUMENTS) {
                    sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_doc_max_count_error)))
                } else if (!uiState.value.isUploadingDocument) {
                    emit(PartialState.PickerChanged(IllDaysWizardPicker.DocumentSource))
                }
            }
            IllDaysWizardIntent.OpenCamera -> {
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
                sendEvent(IllDaysWizardEvent.LaunchCamera)
            }
            IllDaysWizardIntent.OpenGallery -> {
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
                sendEvent(IllDaysWizardEvent.LaunchGallery)
            }
            is IllDaysWizardIntent.DocumentImagePicked -> uploadDocument(intent.fileName, intent.bytes)
            is IllDaysWizardIntent.RemoveDocument -> {
                val updated = uiState.value.documents
                    .filterNot { it.localId == intent.localId }
                    .toImmutableList()
                emit(PartialState.DocumentsChanged(updated))
            }
            is IllDaysWizardIntent.PreviewDocument ->
                emit(PartialState.PreviewDocument(intent.localId))
            IllDaysWizardIntent.DismissPreview ->
                emit(PartialState.PreviewDocument(null))
            IllDaysWizardIntent.Submit -> submitRequest()
            IllDaysWizardIntent.OpenCalculate -> sendEvent(IllDaysWizardEvent.NavigateToCalculate)
            IllDaysWizardIntent.Back -> {
                when (uiState.value.currentStep) {
                    IllDaysWizardStep.BranchCity -> sendEvent(IllDaysWizardEvent.NavigateBack)
                    IllDaysWizardStep.RestDays ->
                        emit(PartialState.StepChanged(IllDaysWizardStep.BranchCity))
                    IllDaysWizardStep.Doctor ->
                        emit(PartialState.StepChanged(IllDaysWizardStep.RestDays))
                    IllDaysWizardStep.Documents ->
                        emit(PartialState.StepChanged(IllDaysWizardStep.Doctor))
                }
            }
        }
    }.catch { error ->
        sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        emit(createErrorState(error.toSingleLineMessage()))
    }

    private fun loadMainInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val info = getIllDaysInsuredMainInfoUseCase().first()?.toPresentation()
            if (info == null) {
                emit(PartialState.Loading(false))
                emit(PartialState.Error(getString(Res.string.ill_days_error_info_not_loaded)))
                return@flow
            }
            emit(PartialState.InsuredLoaded(info))
            val branches = info.branchWorkshops.toImmutableList()
            val selected = branches.singleOrNull()
            emit(PartialState.BranchesLoaded(branches = branches, selected = selected))
            emit(PartialState.Loading(false))
            cityPaginator.refresh(cityBaseQuery(""))
        } catch (error: Throwable) {
            emit(PartialState.Loading(false))
            emit(PartialState.Error(error.toSingleLineMessage()))
            sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        }
    }

    private fun observeCityPaging(): Flow<PartialState> = cityPaginator.state.transform { paging ->
        emit(
            PartialState.CityPagingChanged(
                items = paging.items.toCityPresentation().toImmutableList(),
                isLoadingFirstPage = paging.isLoadingFirstPage,
                isLoadingNextPage = paging.isLoadingNextPage,
                endReached = paging.endReached,
            ),
        )
        paging.error?.let { sendEvent(IllDaysWizardEvent.ShowToast(it.toSingleLineMessage())) }
    }

    private fun cityBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = CityListQuery.filters(cityName = query.takeIf { it.isNotBlank() }),
    )

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.handleNext() {
        when (uiState.value.currentStep) {
            IllDaysWizardStep.BranchCity -> {
                if (!uiState.value.canGoNextFromStep1) return
                emit(PartialState.StepChanged(IllDaysWizardStep.RestDays))
            }
            IllDaysWizardStep.RestDays -> {
                if (!uiState.value.canGoNextFromStep2) return
                val days = uiState.value.dayCount
                if (days != null && days > MAX_REST_DAYS) {
                    sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_error_rest_days_too_long)))
                    return
                }
                emit(PartialState.StepChanged(IllDaysWizardStep.Doctor))
            }
            IllDaysWizardStep.Doctor -> {
                if (!uiState.value.canGoNextFromStep3) return
                emit(PartialState.StepChanged(IllDaysWizardStep.Documents))
            }
            IllDaysWizardStep.Documents -> Unit
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.handlePrevious() {
        when (uiState.value.currentStep) {
            IllDaysWizardStep.BranchCity -> sendEvent(IllDaysWizardEvent.NavigateBack)
            IllDaysWizardStep.RestDays ->
                emit(PartialState.StepChanged(IllDaysWizardStep.BranchCity))
            IllDaysWizardStep.Doctor ->
                emit(PartialState.StepChanged(IllDaysWizardStep.RestDays))
            IllDaysWizardStep.Documents ->
                emit(PartialState.StepChanged(IllDaysWizardStep.Doctor))
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.handleCovidToggle(
        enabled: Boolean,
    ) {
        emit(PartialState.CovidToggled(enabled))
        if (!enabled) {
            emit(
                PartialState.RestDatesSet(
                    startMillis = null,
                    startLabel = "",
                    endMillis = null,
                    endLabel = "",
                    dayCount = null,
                )
            )
            return
        }
        emit(PartialState.CovidLoading(true))
        try {
            val covid = getCovidResultUseCase().first().toPresentation()
            val startMillis = covid.startDateTimeStamp.toEpochMillisOrNull()
            val endMillis = covid.endDateTimeStamp.toEpochMillisOrNull()
            emit(
                PartialState.RestDatesSet(
                    startMillis = startMillis,
                    startLabel = startMillis?.let { PersianDateFormatter.formatTimestamp(it) }.orEmpty(),
                    endMillis = endMillis,
                    endLabel = endMillis?.let { PersianDateFormatter.formatTimestamp(it) }.orEmpty(),
                    dayCount = computeDayCount(startMillis, endMillis),
                )
            )
            emit(PartialState.CovidLoading(false))
            if (startMillis == null || endMillis == null) {
                sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_error_covid_dates_missing)))
            }
        } catch (error: Throwable) {
            emit(PartialState.CovidLoading(false))
            emit(PartialState.CovidToggled(false))
            sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.uploadDocument(
        fileName: String,
        bytes: ByteArray,
    ) {
        if (uiState.value.documents.size >= MAX_DOCUMENTS) {
            sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_doc_max_count_error)))
            return
        }
        if (!isJpegFileName(fileName)) {
            sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_doc_format_error)))
            return
        }
        if (bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
            sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_doc_format_error)))
            return
        }
        val isDuplicate = uiState.value.documents.any { it.bytes.contentEquals(bytes) }
        if (isDuplicate) {
            sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_doc_duplicate_error)))
            return
        }

        val nextIndex = uiState.value.documents.size
        // Legacy always posts documentType "0101" for every ill-days image.
        val documentType = DOCUMENT_TYPE_ILL_DAY
        val title = getString(Res.string.ill_days_doc_title, (nextIndex + 1).toString())

        emit(PartialState.UploadProgress(fileName = fileName, bytes = bytes))
        emit(PartialState.UploadingDocument(true))
        try {
            val guid = uploadImageUseCase(
                UploadImageRequestDN(fileName = fileName, bytes = bytes),
            ).first()
            val document = IllDaysUploadedDocumentUi(
                localId = guid,
                guid = guid,
                documentType = documentType,
                title = title,
                fileName = fileName,
                bytes = bytes,
            )
            val updated = (uiState.value.documents + document).toImmutableList()
            emit(PartialState.DocumentsChanged(updated))
            emit(PartialState.UploadingDocument(false))
            emit(PartialState.UploadProgress(fileName = "", bytes = null))
        } catch (error: Throwable) {
            emit(PartialState.UploadingDocument(false))
            emit(PartialState.UploadProgress(fileName = "", bytes = null))
            sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.submitRequest() {
        val state = uiState.value
        if (state.documents.isEmpty()) {
            sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_doc_min_count_error)))
            return
        }
        val info = state.insuredMainInfo
        val branch = state.selectedBranch
        val city = state.selectedCity
        if (info == null || branch == null || city == null ||
            state.startDateMillis == null || state.endDateMillis == null
        ) {
            sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_submit_missing_data)))
            return
        }

        val request = SaveShortTermIllnessRequestDN(
            doctorId = state.doctorCode,
            doctorName = state.doctorName.trim(),
            startDateTimeStamp = state.startDateMillis,
            endDateTimeStamp = state.endDateMillis,
            illnessKind = if (state.isCovid) ILLNESS_KIND_COVID else ILLNESS_KIND_NORMAL,
            workStatus = if (state.hasMedicalRecord) WORK_STATUS_WITH_RECORD else WORK_STATUS_WITHOUT_RECORD,
            provinceCode = city.provinceCode.orEmpty(),
            cityCode = city.cityCode,
            branchCode = branch.branchCode.ifBlank { branch.id },
            branchName = branch.label.ifBlank { branch.branchName },
            insuranceFirstName = info.firstName,
            insuranceLastName = info.lastName,
            mobileNumber = info.mobileNumber,
            nationalCode = info.nationalCode,
            risuid = info.risuid,
            serviceDateTimeStamp = info.serviceDateTimeStamp,
            requestFileList = state.documents.map {
                IllDaysRequestFileDN(
                    documentFile = it.guid,
                    documentType = it.documentType,
                )
            },
        )

        emit(PartialState.Submitting(true))
        try {
            val message = sendRequestForIllDayUseCase(request).first()
                ?.takeIf { it.isNotBlank() }
                ?: getString(Res.string.ill_days_submit_success_fallback)
            emit(PartialState.Submitting(false))
            sendEvent(IllDaysWizardEvent.ShowSuccess(message))
            sendEvent(IllDaysWizardEvent.NavigateBack)
        } catch (error: Throwable) {
            emit(PartialState.Submitting(false))
            sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: IllDaysWizardUiState,
        partialState: PartialState,
    ): IllDaysWizardUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            errorMessage = if (partialState.isLoading) null else currentState.errorMessage,
        )
        is PartialState.CovidLoading -> currentState.copy(isCovidLoading = partialState.isLoading)
        is PartialState.UploadingDocument -> currentState.copy(isUploadingDocument = partialState.isUploading)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.InsuredLoaded -> currentState.copy(insuredMainInfo = partialState.info)
        is PartialState.BranchesLoaded -> currentState.copy(
            branchOptions = partialState.branches,
            selectedBranch = partialState.selected,
        )
        is PartialState.CityPagingChanged -> currentState.copy(
            cityOptions = partialState.items,
            isCitiesLoading = partialState.isLoadingFirstPage,
            isCitiesLoadingMore = partialState.isLoadingNextPage,
            canLoadMoreCities = !partialState.endReached,
        )
        is PartialState.BranchSelected -> currentState.copy(selectedBranch = partialState.branch)
        is PartialState.CitySelected -> currentState.copy(selectedCity = partialState.city)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.PickerChanged -> currentState.copy(picker = partialState.picker)
        is PartialState.CovidToggled -> currentState.copy(isCovid = partialState.enabled)
        is PartialState.RestDatesSet -> currentState.copy(
            startDateMillis = partialState.startMillis,
            startDateLabel = partialState.startLabel,
            endDateMillis = partialState.endMillis,
            endDateLabel = partialState.endLabel,
            dayCount = partialState.dayCount,
        )
        is PartialState.DoctorNameChanged -> currentState.copy(doctorName = partialState.value)
        is PartialState.DoctorCodeChanged -> currentState.copy(doctorCode = partialState.value)
        is PartialState.MedicalRecordToggled -> currentState.copy(hasMedicalRecord = partialState.enabled)
        is PartialState.DocumentsChanged -> currentState.copy(documents = partialState.documents)
        is PartialState.UploadProgress -> currentState.copy(
            uploadingFileName = partialState.fileName,
            uploadingBytes = partialState.bytes,
        )
        is PartialState.PreviewDocument -> currentState.copy(previewDocumentLocalId = partialState.localId)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errorMessage = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private fun computeDayCount(startMillis: Long?, endMillis: Long?): Int? {
        if (startMillis == null || endMillis == null || endMillis < startMillis) return null
        val days = ((endMillis - startMillis) / MILLIS_PER_DAY).toInt() + 1
        return max(days, 1)
    }

    private fun String?.toEpochMillisOrNull(): Long? {
        val value = this?.toLongOrNull() ?: return null
        return if (value < SECONDS_THRESHOLD) value * 1000L else value
    }

    private fun isJpegFileName(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
        const val SECONDS_THRESHOLD = 10_000_000_000L
        const val MAX_DOCUMENTS = 5
        const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024
        const val DOCTOR_CODE_MAX_LENGTH = 8
        const val MAX_REST_DAYS = 365
        const val ILLNESS_KIND_COVID = "1"
        const val ILLNESS_KIND_NORMAL = "2"
        const val WORK_STATUS_WITH_RECORD = "1"
        const val WORK_STATUS_WITHOUT_RECORD = "2"
        const val DOCUMENT_TYPE_ILL_DAY = "0101"
    }
}

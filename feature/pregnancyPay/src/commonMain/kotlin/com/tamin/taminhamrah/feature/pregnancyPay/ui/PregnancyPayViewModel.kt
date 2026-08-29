package com.tamin.taminhamrah.feature.pregnancyPay.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PREGNANCY_TYPE_TRIPLET_OR_MORE
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayDocumentState
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayEstimateResultUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayEvent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayImageSource
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayIntent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayMainInfoUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayOptionUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayPicker
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayRequiredDocumentIds
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayUiState
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayUiState.PartialState
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.REQUEST_TYPE_SIX_MONTHS
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.REQUEST_TYPE_SIX_TO_NINE_MONTHS
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.REQUEST_TYPE_UP_TO_ONE_YEAR
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.bytesOrNull
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.platformFileOrNull
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyBranchWorkshopDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyRequestFileDN
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.CalculatePregnancyPayEstimateUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyMainInfoUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyStatusListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyTypeListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.SendPregnancyPayRequestUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.pregnancy_pay_document_duplicate_error
import taminx.core.core_ui.pregnancy_pay_document_format_error
import taminx.core.core_ui.pregnancy_pay_document_pick_read_error
import taminx.core.core_ui.pregnancy_pay_document_upload_error
import taminx.core.core_ui.pregnancy_pay_document_validation_min_count
import taminx.core.core_ui.pregnancy_pay_error_baby_birth_diff
import taminx.core.core_ui.pregnancy_pay_error_pregnancy_type_mismatch
import taminx.core.core_ui.pregnancy_pay_error_rest_days_over_12_months
import taminx.core.core_ui.pregnancy_pay_error_rest_days_over_6_months
import taminx.core.core_ui.pregnancy_pay_error_rest_days_over_9_months
import taminx.core.core_ui.pregnancy_pay_request_type_option_1
import taminx.core.core_ui.pregnancy_pay_request_type_option_2
import taminx.core.core_ui.pregnancy_pay_request_type_option_3
import taminx.core.core_ui.pregnancy_pay_submit_missing_data_error

class PregnancyPayViewModel(
    private val getPregnancyMainInfoUseCase: GetPregnancyMainInfoUseCase,
    private val getPregnancyStatusListUseCase: GetPregnancyStatusListUseCase,
    private val getPregnancyTypeListUseCase: GetPregnancyTypeListUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val sendPregnancyPayRequestUseCase: SendPregnancyPayRequestUseCase,
    private val calculatePregnancyPayEstimateUseCase: CalculatePregnancyPayEstimateUseCase,
) : BaseViewModel<PregnancyPayUiState, PartialState, PregnancyPayEvent, PregnancyPayIntent>(
    initialState = PregnancyPayUiState()
) {

    init {
        sendIntent(PregnancyPayIntent.LoadInitialData)
    }

    override fun handleIntent(intent: PregnancyPayIntent): Flow<PartialState> = when (intent) {
        is PregnancyPayIntent.LoadInitialData -> loadInitialData()

        is PregnancyPayIntent.OnGenderBlockAcknowledged -> flow {
            sendEvent(PregnancyPayEvent.NavigateBack)
        }

        is PregnancyPayIntent.OnLandingStartClicked -> flow {
            emit(PartialState.StepChanged(PregnancyPayStep.BranchAndRest))
        }

        is PregnancyPayIntent.OnCalculateEstimateClicked -> flow {
            emit(PartialState.StepChanged(PregnancyPayStep.CalculateEstimate))
        }

        is PregnancyPayIntent.OnPickerRequested -> flow {
            emit(PartialState.PickerChanged(intent.picker))
        }

        is PregnancyPayIntent.OnPickerDismissed -> flow {
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnBranchPicked -> flow {
            emit(PartialState.BranchSelected(intent.option))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnPregnancyStatusPicked -> flow {
            emit(PartialState.PregnancyStatusSelected(intent.option))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnPregnancyTypePicked -> flow {
            emit(PartialState.PregnancyTypeSelected(intent.option))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnRequestTypePicked -> flow {
            emit(PartialState.RequestTypeSelected(intent.option))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnRestStartDatePicked -> flow {
            emit(PartialState.RestStartDateSelected(intent.millis, intent.label))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnRestEndDatePicked -> flow {
            emit(PartialState.RestEndDateSelected(intent.millis, intent.label))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnBabyBirthDatePicked -> flow {
            emit(PartialState.BabyBirthDateSelected(intent.millis, intent.label))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnDoctorNameChanged -> flow {
            emit(PartialState.DoctorNameChanged(intent.value))
        }

        is PregnancyPayIntent.OnDoctorCodeChanged -> flow {
            emit(PartialState.DoctorCodeChanged(intent.value))
        }

        is PregnancyPayIntent.OnChildNationalCodeChanged -> flow {
            emit(PartialState.ChildNationalCodeChanged(intent.slot, intent.value))
        }

        is PregnancyPayIntent.OnNextFromBranchAndRestClicked -> flow {
            if (uiState.value.canGoNextFromBranchAndRest) {
                emit(PartialState.StepChanged(PregnancyPayStep.PregnancyAndNewborn))
            }
        }

        is PregnancyPayIntent.OnNextFromPregnancyAndNewbornClicked -> flow {
            if (uiState.value.canGoNextFromPregnancyAndNewborn) {
                emit(PartialState.StepChanged(PregnancyPayStep.DoctorAndRequest))
            }
        }

        is PregnancyPayIntent.OnNextFromDoctorAndRequestClicked -> handleNextFromDoctorAndRequestClicked()

        is PregnancyPayIntent.OnDocumentCardClicked -> flow {
            emit(PartialState.DocumentSourceRequested(intent.documentId))
        }

        is PregnancyPayIntent.OnDocumentSourceSelected -> flow {
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
            sendEvent(PregnancyPayEvent.LaunchImagePicker(intent.documentId, intent.source))
        }

        is PregnancyPayIntent.OnDocumentRemoveClicked -> handleDocumentRemoveClicked(intent.documentId)

        is PregnancyPayIntent.OnDocumentImagePicked -> handleDocumentImagePicked(intent.documentId, intent.file)

        is PregnancyPayIntent.OnDocumentImagePickFailed -> flow {
            emit(PartialState.DocumentPickRejected(intent.message))
        }

        is PregnancyPayIntent.OnSubmitDocumentsClicked -> handleSubmitDocumentsClicked()

        is PregnancyPayIntent.OnSubmitSuccessAcknowledged -> flow {
            sendEvent(PregnancyPayEvent.NavigateBack)
        }

        is PregnancyPayIntent.OnEstimateRestStartDatePicked -> flow {
            emit(PartialState.EstimateRestStartDateSelected(intent.millis, intent.label))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnEstimateRestEndDatePicked -> flow {
            emit(PartialState.EstimateRestEndDateSelected(intent.millis, intent.label))
            emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
        }

        is PregnancyPayIntent.OnCalculateEstimateSubmitClicked -> handleCalculateEstimateClicked()

        is PregnancyPayIntent.BackToPreviousStep -> handleBackStep()
    }

    private fun loadInitialData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val mainInfoDN = getPregnancyMainInfoUseCase().first()
            val isMaleBlocked = mainInfoDN?.genderCode.isMaleGenderCode()
            val branchOptions = mainInfoDN?.branchWorkshops
                ?.map { it.toOptionUi() }
                ?.toPersistentList()
                ?: persistentListOf()

            emit(
                PartialState.MainInfoLoaded(
                    mainInfo = mainInfoDN?.toMainInfoUi(),
                    isMaleBlocked = isMaleBlocked,
                    branch = branchOptions.singleOrNull(),
                    branchOptions = branchOptions,
                )
            )

            if (!isMaleBlocked) {
                val statusOptions = getPregnancyStatusListUseCase().first()
                    .map { it.toOptionUi() }
                    .toPersistentList()
                val typeOptions = getPregnancyTypeListUseCase().first()
                    .map { it.toOptionUi() }
                    .toPersistentList()
                val requestTypeOptions = persistentListOf(
                    PregnancyPayOptionUi(
                        id = REQUEST_TYPE_SIX_MONTHS,
                        label = getString(Res.string.pregnancy_pay_request_type_option_1),
                    ),
                    PregnancyPayOptionUi(
                        id = REQUEST_TYPE_SIX_TO_NINE_MONTHS,
                        label = getString(Res.string.pregnancy_pay_request_type_option_2),
                    ),
                    PregnancyPayOptionUi(
                        id = REQUEST_TYPE_UP_TO_ONE_YEAR,
                        label = getString(Res.string.pregnancy_pay_request_type_option_3),
                    ),
                )
                emit(
                    PartialState.OptionsLoaded(
                        pregnancyStatusOptions = statusOptions,
                        pregnancyStatus = statusOptions.singleOrNull(),
                        pregnancyTypeOptions = typeOptions,
                        pregnancyType = null,
                        requestTypeOptions = requestTypeOptions,
                    )
                )
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    private fun handleNextFromDoctorAndRequestClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        if (!state.canGoNextFromDoctorAndRequest) return@flow

        val message = validateCrossFieldRules(state)
        if (message != null) {
            emit(PartialState.DoctorAndRequestValidationFailed(message))
            return@flow
        }
        emit(PartialState.StepChanged(PregnancyPayStep.Documents))
    }

    /**
     * Rules that only make sense once every field they compare across steps is known — i.e. once
     * the user reaches the last input step (request type + doctor info). Presence of each
     * individual field is instead gated per-step via `canGoNextFrom*`.
     */
    private suspend fun validateCrossFieldRules(state: PregnancyPayUiState): String? {
        val requestType = state.requestType ?: return null
        val pregnancyType = state.pregnancyType ?: return null

        if (requestType.id == REQUEST_TYPE_UP_TO_ONE_YEAR && pregnancyType.id != PREGNANCY_TYPE_TRIPLET_OR_MORE) {
            return getString(Res.string.pregnancy_pay_error_pregnancy_type_mismatch)
        }

        val restDays = state.restDaysCount
        val dayCapExceeded = when (requestType.id) {
            REQUEST_TYPE_SIX_MONTHS -> restDays != null && restDays > SIX_MONTHS_DAY_CAP
            REQUEST_TYPE_SIX_TO_NINE_MONTHS -> restDays != null && restDays > NINE_MONTHS_DAY_CAP
            REQUEST_TYPE_UP_TO_ONE_YEAR -> restDays != null && restDays > TWELVE_MONTHS_DAY_CAP
            else -> false
        }
        if (dayCapExceeded) {
            return when (requestType.id) {
                REQUEST_TYPE_SIX_MONTHS -> getString(Res.string.pregnancy_pay_error_rest_days_over_6_months)
                REQUEST_TYPE_SIX_TO_NINE_MONTHS -> getString(Res.string.pregnancy_pay_error_rest_days_over_9_months)
                else -> getString(Res.string.pregnancy_pay_error_rest_days_over_12_months)
            }
        }

        val babyDiff = state.babyBirthToRestStartDiffDays
        if (babyDiff != null && babyDiff > BABY_BIRTH_TO_REST_START_MAX_DIFF_DAYS) {
            return getString(Res.string.pregnancy_pay_error_baby_birth_diff)
        }
        return null
    }

    private fun handleDocumentImagePicked(documentId: String, file: PlatformFile): Flow<PartialState> = flow {
        val fileName = file.name
        if (!isJpegFileName(fileName)) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.pregnancy_pay_document_format_error)))
            return@flow
        }

        val bytes = try {
            file.readBytes()
        } catch (e: Exception) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.pregnancy_pay_document_pick_read_error)))
            return@flow
        }

        if (bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.pregnancy_pay_document_format_error)))
            return@flow
        }

        val duplicateOfId = findDuplicateDocumentId(excludeId = documentId, bytes = bytes)
        if (duplicateOfId != null) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.pregnancy_pay_document_duplicate_error)))
            return@flow
        }

        uiState.value.documents[documentId]?.platformFileOrNull()?.let { deleteFileQuietly(it) }

        emit(PartialState.DocumentStateChanged(documentId, PregnancyPayDocumentState.Uploading(file, bytes)))
        try {
            val guid = uploadImageUseCase(UploadImageRequestDN(fileName = fileName, bytes = bytes)).first()
            emit(PartialState.DocumentStateChanged(documentId, PregnancyPayDocumentState.Uploaded(guid, file, bytes)))
        } catch (e: Exception) {
            val message = e.toSingleLineMessage().ifBlank { getString(Res.string.pregnancy_pay_document_upload_error) }
            emit(PartialState.DocumentStateChanged(documentId, PregnancyPayDocumentState.Failed(message, file, bytes)))
        }
    }

    private fun handleDocumentRemoveClicked(documentId: String): Flow<PartialState> = flow {
        uiState.value.documents[documentId]?.platformFileOrNull()?.let { deleteFileQuietly(it) }
        emit(PartialState.DocumentStateChanged(documentId, PregnancyPayDocumentState.Empty))
        emit(PartialState.PickerChanged(PregnancyPayPicker.NONE))
    }

    private fun handleSubmitDocumentsClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isSubmitting || state.hasSubmitted) return@flow

        if (state.requiredDocumentsUploadedCount < PregnancyPayRequiredDocumentIds.size) {
            emit(PartialState.DocumentValidationFailed(getString(Res.string.pregnancy_pay_document_validation_min_count)))
            return@flow
        }

        val request = state.toSendPregnancyPayRequestDN()
        if (request == null) {
            emit(PartialState.SubmitFailed(getString(Res.string.pregnancy_pay_submit_missing_data_error)))
            return@flow
        }

        emit(PartialState.DocumentsReadyForSubmission)
        emit(PartialState.Submitting(true))
        try {
            val resultMessage = sendPregnancyPayRequestUseCase(request).first()
            emit(PartialState.SubmitSucceeded(resultMessage))
        } catch (e: Exception) {
            emit(PartialState.SubmitFailed(e.toSingleLineMessage()))
        }
    }

    private fun handleCalculateEstimateClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        val start = state.estimateRestStartDateTimeStamp
        val end = state.estimateRestEndDateTimeStamp
        val restDays = state.estimateRestDaysCount
        if (!state.canCalculateEstimate || start == null || end == null || restDays == null) return@flow

        emit(PartialState.EstimateCalculating(true))
        try {
            val estimateDN = calculatePregnancyPayEstimateUseCase(start, end).first()
            emit(
                PartialState.EstimateCalculated(
                    PregnancyPayEstimateResultUi(
                        restDaysCount = restDays,
                        averageSalaryLast90Days = estimateDN.averageSalaryLast90Days,
                        amountPayable = estimateDN.amountPayable,
                    )
                )
            )
        } catch (e: Exception) {
            emit(PartialState.EstimateFailed(e.toSingleLineMessage()))
        }
    }

    private fun PregnancyPayUiState.toSendPregnancyPayRequestDN(): SendPregnancyPayRequestDN? {
        val info = mainInfo ?: return null
        val branch = branch ?: return null
        val pregnancyStatus = pregnancyStatus ?: return null
        val pregnancyType = pregnancyType ?: return null
        val requestType = requestType ?: return null
        val restStart = restStartDateTimeStamp ?: return null
        val restEnd = restEndDateTimeStamp ?: return null
        val babyBirth = babyBirthDateTimeStamp ?: return null
        val restDays = restDaysCount ?: return null

        return SendPregnancyPayRequestDN(
            branchCode = branch.id,
            branchName = branch.label,
            insuranceFirstName = info.firstName,
            insuranceLastName = info.lastName,
            mobileNumber = info.mobileNumber,
            nationalCode = info.nationalCode,
            risuid = info.risuid,
            serviceDateTimeStamp = info.serviceDateTimeStamp,
            pregnancyStatusCode = pregnancyStatus.id,
            pregnancyTypeCode = pregnancyType.id,
            requestTypeCode = requestType.id,
            restStartDateTimeStamp = restStart,
            restEndDateTimeStamp = restEnd,
            restDaysCount = restDays.toString(),
            babyBirthDateTimeStamp = babyBirth,
            doctorName = doctorName,
            doctorCode = doctorCode,
            childNationalId = childNationalCode,
            childNationalId2 = childNationalCode2.ifBlank { null },
            childNationalId3 = childNationalCode3.ifBlank { null },
            requestFileList = documentSubmissionPayload.map {
                PregnancyRequestFileDN(documentFile = it.documentFile, documentType = it.documentType)
            },
        )
    }

    private fun findDuplicateDocumentId(excludeId: String, bytes: ByteArray): String? =
        uiState.value.documents.entries.firstOrNull { (id, state) ->
            id != excludeId && state.bytesOrNull()?.contentEquals(bytes) == true
        }?.key

    private suspend fun deleteFileQuietly(file: PlatformFile) {
        runCatching { file.delete(mustExist = false) }
    }

    private fun isJpegFileName(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }

    private fun String?.isMaleGenderCode(): Boolean {
        val normalized = this?.trim()?.lowercase()
        return normalized == "01" || normalized == "m"
    }

    override fun onCleared() {
        super.onCleared()
        val filesToDelete = uiState.value.documents.values.mapNotNull { it.platformFileOrNull() }
        if (filesToDelete.isEmpty()) return
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            filesToDelete.forEach { file -> runCatching { file.delete(mustExist = false) } }
        }
    }

    private fun handleBackStep(): Flow<PartialState> = flow {
        when (uiState.value.currentStep) {
            PregnancyPayStep.Documents -> emit(PartialState.StepChanged(PregnancyPayStep.DoctorAndRequest))
            PregnancyPayStep.DoctorAndRequest -> emit(PartialState.StepChanged(PregnancyPayStep.PregnancyAndNewborn))
            PregnancyPayStep.PregnancyAndNewborn -> emit(PartialState.StepChanged(PregnancyPayStep.BranchAndRest))
            PregnancyPayStep.BranchAndRest -> emit(PartialState.StepChanged(PregnancyPayStep.Landing))
            PregnancyPayStep.CalculateEstimate -> emit(PartialState.StepChanged(PregnancyPayStep.Landing))
            PregnancyPayStep.Landing -> sendEvent(PregnancyPayEvent.NavigateBack)
        }
    }

    private fun PregnancyMainInfoDN.toMainInfoUi() = PregnancyPayMainInfoUi(
        risuid = risuid,
        nationalCode = nationalCode,
        firstName = firstName,
        lastName = lastName,
        mobileNumber = mobileNumber,
        serviceDateTimeStamp = serviceDateTimeStamp,
        bankAccount = bankAccount,
        bankName = bankName,
        insuranceTypeDesc = insuranceTypeDesc,
        insuranceStatusDesc = insuranceStatusDesc,
    )

    private fun PregnancyBranchWorkshopDN.toOptionUi() = PregnancyPayOptionUi(
        id = branchCode.orEmpty(),
        label = "${branchName.orEmpty()}-${workshopName.orEmpty()}",
    )

    private fun PregnancyOptionDN.toOptionUi() = PregnancyPayOptionUi(id = code, label = name)

    override fun reduceState(
        currentState: PregnancyPayUiState,
        partialState: PartialState
    ): PregnancyPayUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = if (partialState.isLoading) null else currentState.error,
        )
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.MainInfoLoaded -> currentState.copy(
            mainInfo = partialState.mainInfo,
            isMaleBlocked = partialState.isMaleBlocked,
            branch = partialState.branch,
            branchOptions = partialState.branchOptions,
        )
        is PartialState.OptionsLoaded -> currentState.copy(
            pregnancyStatusOptions = partialState.pregnancyStatusOptions,
            pregnancyStatus = partialState.pregnancyStatus,
            pregnancyTypeOptions = partialState.pregnancyTypeOptions,
            pregnancyType = partialState.pregnancyType,
            requestTypeOptions = partialState.requestTypeOptions,
        )
        is PartialState.PickerChanged -> currentState.copy(
            picker = partialState.picker,
            activeDocumentId = if (partialState.picker == PregnancyPayPicker.DOCUMENT_SOURCE) {
                currentState.activeDocumentId
            } else {
                null
            },
        )
        is PartialState.DocumentSourceRequested -> currentState.copy(
            picker = PregnancyPayPicker.DOCUMENT_SOURCE,
            activeDocumentId = partialState.documentId,
        )
        is PartialState.BranchSelected -> currentState.copy(branch = partialState.branch)
        is PartialState.PregnancyStatusSelected -> currentState.copy(pregnancyStatus = partialState.option)
        is PartialState.PregnancyTypeSelected -> {
            val updated = currentState.copy(pregnancyType = partialState.option)
            val requiredCount = updated.requiredChildNationalCodeCount
            updated.copy(
                childNationalCode2 = if (requiredCount >= 2) updated.childNationalCode2 else "",
                childNationalCode3 = if (requiredCount >= 3) updated.childNationalCode3 else "",
            )
        }
        is PartialState.RequestTypeSelected -> currentState.copy(requestType = partialState.option)
        is PartialState.RestStartDateSelected -> currentState.copy(
            restStartDateTimeStamp = partialState.millis,
            restStartDateLabel = partialState.label,
        )
        is PartialState.RestEndDateSelected -> currentState.copy(
            restEndDateTimeStamp = partialState.millis,
            restEndDateLabel = partialState.label,
        )
        is PartialState.BabyBirthDateSelected -> currentState.copy(
            babyBirthDateTimeStamp = partialState.millis,
            babyBirthDateLabel = partialState.label,
        )
        is PartialState.DoctorNameChanged -> currentState.copy(doctorName = partialState.value)
        is PartialState.DoctorCodeChanged -> currentState.copy(doctorCode = partialState.value)
        is PartialState.ChildNationalCodeChanged -> when (partialState.slot) {
            1 -> currentState.copy(childNationalCode = partialState.value)
            2 -> currentState.copy(childNationalCode2 = partialState.value)
            3 -> currentState.copy(childNationalCode3 = partialState.value)
            else -> currentState
        }
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step, doctorAndRequestError = null)
        is PartialState.DoctorAndRequestValidationFailed -> currentState.copy(doctorAndRequestError = partialState.message)
        is PartialState.DocumentStateChanged -> currentState.copy(
            documents = currentState.documents.toPersistentMap().put(partialState.documentId, partialState.state),
            documentPickError = null,
            documentValidationError = null,
        )
        is PartialState.DocumentPickRejected -> currentState.copy(documentPickError = partialState.message)
        is PartialState.DocumentValidationFailed -> currentState.copy(documentValidationError = partialState.message)
        PartialState.DocumentsReadyForSubmission -> currentState.copy(documentValidationError = null)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting, submitError = null)
        is PartialState.SubmitSucceeded -> currentState.copy(
            isSubmitting = false,
            hasSubmitted = true,
            submittedResultMessage = partialState.resultMessage,
            submitError = null,
        )
        is PartialState.SubmitFailed -> currentState.copy(isSubmitting = false, submitError = partialState.message)
        is PartialState.EstimateRestStartDateSelected -> currentState.copy(
            estimateRestStartDateTimeStamp = partialState.millis,
            estimateRestStartDateLabel = partialState.label,
            estimateResult = null,
            estimateError = null,
        )
        is PartialState.EstimateRestEndDateSelected -> currentState.copy(
            estimateRestEndDateTimeStamp = partialState.millis,
            estimateRestEndDateLabel = partialState.label,
            estimateResult = null,
            estimateError = null,
        )
        is PartialState.EstimateCalculating -> currentState.copy(
            isCalculatingEstimate = partialState.isCalculating,
            estimateError = null,
        )
        is PartialState.EstimateCalculated -> currentState.copy(
            isCalculatingEstimate = false,
            estimateResult = partialState.result,
            estimateError = null,
        )
        is PartialState.EstimateFailed -> currentState.copy(
            isCalculatingEstimate = false,
            estimateError = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private companion object {
        const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024
        const val SIX_MONTHS_DAY_CAP = 186
        const val NINE_MONTHS_DAY_CAP = 276
        const val TWELVE_MONTHS_DAY_CAP = 365
        const val BABY_BIRTH_TO_REST_START_MAX_DIFF_DAYS = 63
    }
}

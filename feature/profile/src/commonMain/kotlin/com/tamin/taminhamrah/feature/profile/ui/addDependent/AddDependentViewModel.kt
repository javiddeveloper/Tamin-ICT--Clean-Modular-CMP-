package com.tamin.taminhamrah.feature.profile.ui.addDependent

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentEvent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.DocType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.StepperMode
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.UploadedDocument
import com.tamin.taminhamrah.mapper.addDependent.toDomain
import com.tamin.taminhamrah.mapper.addDependent.toPresentation
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentPR
import com.tamin.taminhamrah.model.addDependent.RequestFilePR
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.useCases.addDependent.AddNewDependentUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetActiveBranchesUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetFamilyRelationshipsUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryEducationCodeUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryRegistryUseCase
import com.tamin.taminhamrah.useCases.addDependent.UploadDependentImageUseCase
import com.tamin.taminhamrah.util.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AddDependentViewModel(
    private val getActiveBranchesUseCase: GetActiveBranchesUseCase,
    private val getFamilyRelationshipsUseCase: GetFamilyRelationshipsUseCase,
    private val inquiryRegistryUseCase: InquiryRegistryUseCase,
    private val inquiryEducationCodeUseCase: InquiryEducationCodeUseCase,
    private val uploadDependentImageUseCase: UploadDependentImageUseCase,
    private val addNewDependentUseCase: AddNewDependentUseCase
) : BaseViewModel<AddDependentState, PartialState, AddDependentEvent, AddDependentIntent>(
    initialState = AddDependentState()
) {

    override fun handleIntent(intent: AddDependentIntent): Flow<PartialState> {
        return when (intent) {
            is AddDependentIntent.InitData -> initData()
            is AddDependentIntent.OnNationalIdChanged -> flow {
                emit(PartialState.NationalIdChanged(intent.id))
            }
            is AddDependentIntent.OnBirthDateSelected -> flow {
                emit(PartialState.BirthDateSelected(intent.persianDate, intent.gregorianDate, intent.timestamp))
            }
            is AddDependentIntent.OnRelationshipSelected -> flow {
                emit(PartialState.RelationshipSelected(intent.relationship))
            }
            is AddDependentIntent.SubmitInquiryRegistry -> submitInquiryRegistry()
            is AddDependentIntent.OnEducationCodeChanged -> flow {
                emit(PartialState.EducationCodeChanged(intent.code))
            }
            is AddDependentIntent.SubmitInquiryEducation -> submitInquiryEducation()
            is AddDependentIntent.OnDaughterCommitmentToggled -> flow {
                emit(PartialState.DaughterCommitmentToggled(intent.isChecked))
            }
            is AddDependentIntent.OnCityBirthSelected -> flow {
                emit(PartialState.CityBirthSelected(intent.city))
            }
            is AddDependentIntent.OnCityIssuanceSelected -> flow {
                emit(PartialState.CityIssuanceSelected(intent.city))
            }
            is AddDependentIntent.OnBranchSelected -> flow {
                emit(PartialState.BranchSelected(intent.branch))
            }
            is AddDependentIntent.UploadDocument -> uploadDocument(intent.fileBytes, intent.fileName, intent.docType)
            is AddDependentIntent.DeleteDocument -> flow {
                emit(PartialState.DocumentDeleted(intent.docType))
            }
            is AddDependentIntent.OnNextStepClicked -> onNextStepClicked()
            is AddDependentIntent.OnPreviousStepClicked -> flow {
                val prevStep = (uiState.value.currentStep - 1).coerceAtLeast(1)
                emit(PartialState.StepChanged(prevStep))
            }
            is AddDependentIntent.SubmitFinalRequest -> submitFinalRequest()
        }
    }

    private fun initData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        getActiveBranchesUseCase()
            .map { branchList ->
                val prBranches = branchList.map { it.toPresentation() }
                val autoSelect = if (prBranches.size == 1) prBranches.first() else null
                PartialState.ActiveBranchesLoaded(prBranches, autoSelect) as PartialState
            }
            .catch { emit(PartialState.Error(it.message ?: "خطا در دریافت لیست شعب")) }
            .collect { emit(it) }
    }

    private fun submitInquiryRegistry(): Flow<PartialState> = flow {
        val state = uiState.value

        if (!ValidationUtils.isNationalIdValid(state.dependentNationalId)) {
            sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "کد ملی معتبر نیست"))
            return@flow
        }
        if (state.birthDateTimeStamp.isBlank() && state.birthDatePersian.isBlank()) {
            sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "لطفا تاریخ تولد را انتخاب کنید"))
            return@flow
        }
        if (state.selectedRelationship == null) {
            sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "لطفا نسبت خانوادگی را انتخاب کنید"))
            return@flow
        }

        emit(PartialState.Loading(true))

        val relationshipCode = state.selectedRelationship.relationCode.orEmpty()
        inquiryRegistryUseCase(
            dependentNationalId = state.dependentNationalId,
            birthDateTimeStamp = state.birthDateTimeStamp,
            dependencyCode = relationshipCode
        ).map { registryDataDN ->
            val registryPR = registryDataDN.toPresentation()
            val stepperMode = evaluateStepperMode(registryPR, relationshipCode)
            val requiredDocTypes = evaluateDocumentRequirements(registryPR, relationshipCode)
            PartialState.RegistryInquirySuccess(registryPR, stepperMode, requiredDocTypes) as PartialState
        }.catch {
            emit(PartialState.Error(it.message ?: "خطا در استعلام ثبت احوال"))
        }.collect {
            emit(it)
        }
    }

    private fun submitInquiryEducation(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.educationCode.isBlank()) {
            sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "لطفا کد استعلام تحصیلی را وارد کنید"))
            return@flow
        }

        emit(PartialState.Loading(true))
        inquiryEducationCodeUseCase(
            nationalId = state.dependentNationalId,
            educationCode = state.educationCode
        ).map { resultString ->
            PartialState.EducationInquirySuccess(resultString) as PartialState
        }.catch {
            emit(PartialState.Error(it.message ?: "خطا در استعلام کد تحصیلی"))
        }.collect {
            emit(it)
        }
    }

    private fun uploadDocument(fileBytes: ByteArray, fileName: String, docType: String): Flow<PartialState> = flow {
        val bytesToUpload = if (fileBytes.size >= 2_000_000) {
            compressBytes(fileBytes)
        } else {
            fileBytes
        }

        val mimeType = when {
            fileName.endsWith(".png", true) -> "image/png"
            fileName.endsWith(".pdf", true) -> "application/pdf"
            else -> "image/jpeg"
        }

        emit(PartialState.Loading(true))

        uploadDependentImageUseCase(
            imageBytes = bytesToUpload,
            fileName = fileName,
            mimeType = mimeType
        ).map { uploadDN ->
            val uploadPR = uploadDN.toPresentation()
            val doc = UploadedDocument(
                guid = uploadPR.guid,
                docType = docType,
                fileName = fileName
            )
            PartialState.DocumentUploaded(doc) as PartialState
        }.catch {
            emit(PartialState.Error(it.message ?: "خطا در آپلود مدرک"))
        }.collect {
            emit(it)
        }
    }

    private fun onNextStepClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        when (state.currentStep) {
            1 -> {
                if (state.needCallInquiryRegistry) {
                    submitInquiryRegistry().collect { emit(it) }
                } else {
                    emit(PartialState.StepChanged(2))
                }
            }
            2 -> {
                when (state.stepperMode) {
                    StepperMode.SON_MODE -> {
                        if (state.needCallInquiryEducation) {
                            submitInquiryEducation().collect { emit(it) }
                        } else {
                            emit(PartialState.StepChanged(3))
                        }
                    }
                    StepperMode.DAUGHTER_MODE -> {
                        if (!state.isDaughterCommitmentChecked) {
                            sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "تایید تعهدنامه الزامی است"))
                        } else {
                            emit(PartialState.StepChanged(3))
                        }
                    }
                    StepperMode.DEFAULT_MODE -> {
                        emit(PartialState.StepChanged(3))
                    }
                }
            }
            3 -> {
                if (state.selectedCityBirth == null || state.selectedCityIssuance == null || state.selectedBranch == null) {
                    sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "لطفا اطلاعات محل تولد، صدور و شعبه را تکمیل کنید"))
                } else {
                    emit(PartialState.StepChanged(4))
                }
            }
            4 -> {
                val activeDocTypes = state.requiredDocTypes.filter { !it.isDisabled }
                val uploadedTypes = state.uploadedDocuments.map { it.docType }.toSet()
                val isAllUploaded = activeDocTypes.all { docType -> uploadedTypes.contains(docType.code) }

                if (!isAllUploaded) {
                    sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "لطفا تمامی مدارک الزامی را بارگذاری کنید"))
                } else {
                    submitFinalRequest().collect { emit(it) }
                }
            }
        }
    }

    private fun submitFinalRequest(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Loading(true))

        val requestFiles = state.uploadedDocuments.map { doc ->
            RequestFilePR(
                documentFileId = doc.guid,
                documentType = doc.docType
            )
        }

        val requestPR = RequestAddDependentPR(
            branchCode = state.selectedBranch?.branchCode,
            cityOfBirthId = state.selectedCityBirth?.cityCode,
            cityOfIssueId = state.selectedCityIssuance?.cityCode,
            dateOfBirth = state.birthDateGregorian.ifBlank { state.birthDatePersian },
            dependencyId = state.selectedRelationship?.id,
            dependentTypeCode = state.selectedRelationship?.relationCode,
            firstName = state.registryData?.firstName,
            lastName = state.registryData?.lastName,
            nationalId = state.dependentNationalId,
            requestFileList = requestFiles
        )

        addNewDependentUseCase(requestPR.toDomain())
            .map { resultDN ->
                sendEvent(AddDependentEvent.ShowSuccessDialog(resultDN.message ?: "کفالت با موفقیت ثبت شد"))
                sendEvent(AddDependentEvent.NavigateBack)
                PartialState.Loading(false) as PartialState
            }
            .catch { emit(PartialState.Error(it.message ?: "خطا در ثبت نهایی درخواست")) }
            .collect { emit(it) }
    }

    private fun evaluateStepperMode(data: RegistryDataPR, relationCode: String): StepperMode {
        val isMale = data.gender.equals("MAN", true) || data.gender == "1" || data.gender == "M" || data.gender.contains("مرد")
        val isFemale = data.gender.equals("WOMAN", true) || data.gender == "2" || data.gender == "F" || data.gender.contains("زن")

        val isSon = relationCode == "02" || relationCode.contains("پسر", true) || relationCode.contains("فرزند پسر", true)
        val isDaughter = relationCode == "03" || relationCode.contains("دختر", true) || relationCode.contains("فرزند دختر", true)

        return when {
            data.age >= 19 && isMale && isSon -> StepperMode.SON_MODE
            data.age >= 18 && isFemale && isDaughter -> StepperMode.DAUGHTER_MODE
            else -> StepperMode.DEFAULT_MODE
        }
    }

    private fun evaluateDocumentRequirements(data: RegistryDataPR, relationCode: String): List<DocType> {
        val isSpouse = relationCode == "01" || relationCode.contains("همسر", true)
        val isSonOrDaughter = relationCode == "02" || relationCode == "03" || relationCode.contains("فرزند", true)

        return if (isSpouse) {
            listOf(
                DocType("ID_CARD_PAGE_1", "صفحه اول شناسنامه", isDisabled = false),
                DocType("ID_CARD_SPOUSE", "صفحه مشخصات همسر شناسنامه", isDisabled = false),
                DocType("MARRIAGE_CERT", "عقدنامه", isDisabled = false)
            )
        } else if (isSonOrDaughter) {
            val isDisabledIdCard = data.registryConfirmState == "1"
            listOf(
                DocType("ID_CARD_PAGE_1", "صفحه اول شناسنامه", isDisabled = isDisabledIdCard),
                DocType("MARRIAGE_CERT", "عقدنامه", isDisabled = true)
            )
        } else {
            listOf(
                DocType("ID_CARD_PAGE_1", "صفحه اول شناسنامه", isDisabled = false)
            )
        }
    }

    private fun compressBytes(bytes: ByteArray): ByteArray {
        return if (bytes.size > 2_000_000) {
            bytes.copyOf(2_000_000)
        } else {
            bytes
        }
    }

    override fun reduceState(
        currentState: AddDependentState,
        partialState: PartialState
    ): AddDependentState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.ActiveBranchesLoaded -> currentState.copy(
            isLoading = false,
            activeBranches = partialState.branches,
            selectedBranch = currentState.selectedBranch ?: partialState.autoSelectedBranch,
            error = null
        )
        is PartialState.NationalIdChanged -> {
            val hasInquired = !currentState.needCallInquiryRegistry || currentState.registryData != null
            currentState.copy(
                dependentNationalId = partialState.id,
                needCallInquiryRegistry = true,
                registryData = if (hasInquired) null else currentState.registryData,
                uploadedDocuments = if (hasInquired) emptyList() else currentState.uploadedDocuments
            )
        }
        is PartialState.BirthDateSelected -> {
            val hasInquired = !currentState.needCallInquiryRegistry || currentState.registryData != null
            currentState.copy(
                birthDatePersian = partialState.persianDate,
                birthDateGregorian = partialState.gregorianDate,
                birthDateTimeStamp = partialState.timestamp,
                needCallInquiryRegistry = true,
                registryData = if (hasInquired) null else currentState.registryData,
                uploadedDocuments = if (hasInquired) emptyList() else currentState.uploadedDocuments
            )
        }
        is PartialState.RelationshipSelected -> {
            val hasInquired = !currentState.needCallInquiryRegistry || currentState.registryData != null
            currentState.copy(
                selectedRelationship = partialState.relationship,
                needCallInquiryRegistry = true,
                registryData = if (hasInquired) null else currentState.registryData,
                uploadedDocuments = if (hasInquired) emptyList() else currentState.uploadedDocuments
            )
        }
        is PartialState.RegistryInquirySuccess -> currentState.copy(
            isLoading = false,
            registryData = partialState.registryData,
            stepperMode = partialState.stepperMode,
            requiredDocTypes = partialState.requiredDocTypes,
            needCallInquiryRegistry = false,
            error = null
        )
        is PartialState.EducationCodeChanged -> currentState.copy(
            educationCode = partialState.code,
            needCallInquiryEducation = true
        )
        is PartialState.EducationInquirySuccess -> currentState.copy(
            isLoading = false,
            universityName = partialState.universityName,
            needCallInquiryEducation = false,
            error = null
        )
        is PartialState.DaughterCommitmentToggled -> currentState.copy(
            isDaughterCommitmentChecked = partialState.isChecked
        )
        is PartialState.CityBirthSelected -> currentState.copy(selectedCityBirth = partialState.city)
        is PartialState.CityIssuanceSelected -> currentState.copy(selectedCityIssuance = partialState.city)
        is PartialState.BranchSelected -> currentState.copy(selectedBranch = partialState.branch)
        is PartialState.DocumentUploaded -> {
            val filteredDocs = currentState.uploadedDocuments.filter { it.docType != partialState.document.docType }
            currentState.copy(
                isLoading = false,
                uploadedDocuments = filteredDocs + partialState.document,
                error = null
            )
        }
        is PartialState.DocumentDeleted -> currentState.copy(
            uploadedDocuments = currentState.uploadedDocuments.filter { it.docType != partialState.docType }
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

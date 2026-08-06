package com.tamin.taminhamrah.feature.profile.ui.addDependent

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentEvent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.BottomSheetTarget
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.DocType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_DOCUMENTS
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_INQUIRY
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_SUCCESS
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.STEP_VERIFICATION
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.StepperMode
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.UploadedDocument
import com.tamin.taminhamrah.mapper.addDependent.toDomain
import com.tamin.taminhamrah.mapper.addDependent.toPresentation
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentPR
import com.tamin.taminhamrah.model.addDependent.RequestFilePR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.useCases.addDependent.AddNewDependentUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetActiveBranchesUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetFamilyRelationshipsFromProxyUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryEducationCodeUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryRegistryUseCase
import com.tamin.taminhamrah.useCases.addDependent.UploadDependentImageUseCase
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.util.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** Documents larger than this are rejected outright — see [uploadDocument]. */
private const val MAX_UPLOAD_SIZE_BYTES = 2_000_000

private const val RELATION_CODE_SPOUSE = "01"
private const val RELATION_CODE_SON = "02"
private const val RELATION_CODE_DAUGHTER = "03"

private const val SON_EDUCATION_AGE_THRESHOLD = 19
private const val DAUGHTER_COMMITMENT_AGE_THRESHOLD = 18

/** Registry flag meaning the birth certificate is already on file, so re-uploading it is not required. */
private const val REGISTRY_STATE_ID_CARD_ON_FILE = "1"

/**
 * The relationship dropdown fetches everything ("**" LIKE match) rather than filtering by a
 * user-typed term — see [showRelationshipPicker].
 */
private const val RELATIONSHIP_FILTER_WILDCARD = "**"

class AddDependentViewModel(
    private val getActiveBranchesUseCase: GetActiveBranchesUseCase,
    private val getFamilyRelationshipsFromProxyUseCase: GetFamilyRelationshipsFromProxyUseCase,
    private val inquiryRegistryUseCase: InquiryRegistryUseCase,
    private val inquiryEducationCodeUseCase: InquiryEducationCodeUseCase,
    private val uploadDependentImageUseCase: UploadDependentImageUseCase,
    private val addNewDependentUseCase: AddNewDependentUseCase,
    private val getCitiesUseCase: GetCitiesUseCase
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
                emit(
                    PartialState.BirthDateSelected(
                        intent.persianDate,
                        intent.gregorianDate,
                        intent.timestamp
                    )
                )
            }

            is AddDependentIntent.OnRelationshipSelected -> flow {
                emit(PartialState.RelationshipSelected(intent.relationship))
                emit(dismissBottomSheet())
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
                emit(dismissBottomSheet())
            }

            is AddDependentIntent.OnCityIssuanceSelected -> flow {
                emit(PartialState.CityIssuanceSelected(intent.city))
                emit(dismissBottomSheet())
            }

            is AddDependentIntent.OnBranchSelected -> flow {
                emit(PartialState.BranchSelected(intent.branch))
                emit(dismissBottomSheet())
            }

            is AddDependentIntent.ShowRelationshipPicker -> showRelationshipPicker()
            is AddDependentIntent.ShowCityBirthPicker -> showCityPicker(
                title = "انتخاب محل تولد",
                target = BottomSheetTarget.CITY_BIRTH,
                selectedCityCode = { it.selectedCityBirth?.cityCode }
            )

            is AddDependentIntent.ShowCityIssuancePicker -> showCityPicker(
                title = "انتخاب محل صدور",
                target = BottomSheetTarget.CITY_ISSUANCE,
                selectedCityCode = { it.selectedCityIssuance?.cityCode }
            )

            is AddDependentIntent.ShowBranchPicker -> flow {
                val state = uiState.value
                emit(
                    PartialState.BottomSheetStateChanged(
                        config = TaminBottomSheetConfig(
                            title = "انتخاب شعبه",
                            type = TaminBottomSheetType.CUSTOM,
                            items = state.activeBranches.mapIndexed { index, branch ->
                                TaminBottomSheetItem(
                                    id = index,
                                    title = branch.branchName.ifBlank { branch.branchCode },
                                    isSelected = branch.branchCode == state.selectedBranch?.branchCode
                                )
                            },
                            singleSelection = true,
                            showSearchInput = true
                        ),
                        target = BottomSheetTarget.BRANCH
                    )
                )
            }

            is AddDependentIntent.DismissBottomSheet -> flow {
                emit(dismissBottomSheet())
            }

            is AddDependentIntent.UploadDocument -> uploadDocument(
                intent.fileBytes,
                intent.fileName,
                intent.docType
            )

            is AddDependentIntent.DeleteDocument -> flow {
                emit(PartialState.DocumentDeleted(intent.docType))
            }

            is AddDependentIntent.OnNextStepClicked -> onNextStepClicked()
            is AddDependentIntent.OnPreviousStepClicked -> flow {
                val prevStep = (uiState.value.currentStep - 1).coerceAtLeast(STEP_INQUIRY)
                emit(PartialState.StepChanged(prevStep))
            }

            is AddDependentIntent.SubmitFinalRequest -> submitFinalRequest()
        }
    }

    private fun dismissBottomSheet() =
        PartialState.BottomSheetStateChanged(config = null, target = null)

    /**
     * Active branches are the only lookup fetched eagerly on screen open. Family relationships
     * and cities are intentionally NOT loaded here — both dropdowns are fetched lazily when the
     * user opens them, see [showRelationshipPicker] and [showCityPicker].
     */
    private fun initData(): Flow<PartialState> = loadActiveBranches()
        .onStart { emit(PartialState.Loading(true)) }

    private fun loadActiveBranches(): Flow<PartialState> = getActiveBranchesUseCase()
        .map { branchList ->
            val prBranches = branchList.map { it.toPresentation() }
            val autoSelect = prBranches.singleOrNull()
            PartialState.ActiveBranchesLoaded(prBranches, autoSelect) as PartialState
        }
        .catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadCities(): Flow<PartialState> = getCitiesUseCase()
        .map { cities -> PartialState.CitiesLoaded(cities.toCityPresentation()) as PartialState }
        .catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    /**
     * Fetches the city list on demand, when the user opens the birth/issuance city dropdown,
     * instead of eagerly on screen init. Cities are matched back by list index rather than by
     * numeric city code: codes are zero-padded strings ("0311") and round-tripping them through
     * [Int] loses the padding.
     *
     * NOTE: the shared [TaminBottomSheetConfig] search box only filters the already-fetched
     * `items` client-side (see `TaminBottomSheet.kt`) — it has no callback wired back to a
     * ViewModel. Re-issuing the network request with a `cityName LIKE` filter on every keystroke,
     * as the spec describes, would require extending that shared component (used by every other
     * search picker in the app), so it's intentionally out of scope here pending confirmation.
     */
    private fun showCityPicker(
        title: String,
        target: BottomSheetTarget,
        selectedCityCode: (AddDependentState) -> String?
    ): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Loading(true))

        loadCities().collect { partialState ->
            emit(partialState)
            if (partialState is PartialState.CitiesLoaded) {
                emit(
                    PartialState.BottomSheetStateChanged(
                        config = TaminBottomSheetConfig(
                            title = title,
                            type = TaminBottomSheetType.CITY,
                            items = partialState.cities.mapIndexed { index, city ->
                                TaminBottomSheetItem(
                                    id = index,
                                    title = city.cityName,
                                    isSelected = city.cityCode == selectedCityCode(state)
                                )
                            },
                            singleSelection = true,
                            showSearchInput = true
                        ),
                        target = target
                    )
                )
            }
        }
    }

    /**
     * Fetches the relationship list on demand, when the user opens the "Select Dependent"
     * dropdown, instead of eagerly on screen init. Mirrors `GET proxy/models/dependency` with a
     * `dependencyDesc LIKE "**"` filter (i.e. "match everything") and `page = 1`, matching the
     * backend's real request shape. The bottom sheet only opens once the fresh data has arrived;
     * a failure surfaces as an error instead of opening an empty sheet.
     */
    private fun showRelationshipPicker(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Loading(true))

        getFamilyRelationshipsFromProxyUseCase(
            filter = listOf(
                ApiFilterDN(
                    property = FilterProperty.DEPENDENCY_DESC,
                    value = RELATIONSHIP_FILTER_WILDCARD,
                    operator = FilterOperator.LIKE
                )
            )
        ).map { relationships ->
            PartialState.FamilyRelationshipsLoaded(relationships.map { it.toPresentation() }) as PartialState
        }.catch {
            emit(PartialState.Error(it.toSingleLineMessage()))
        }.collect { partialState ->
            emit(partialState)
            if (partialState is PartialState.FamilyRelationshipsLoaded) {
                emit(
                    PartialState.BottomSheetStateChanged(
                        config = TaminBottomSheetConfig(
                            title = "انتخاب نسبت خانوادگی",
                            type = TaminBottomSheetType.CUSTOM,
                            items = partialState.relationships.map {
                                TaminBottomSheetItem(
                                    id = it.id ?: 0,
                                    title = it.relationDesc.orEmpty(),
                                    isSelected = it.id == state.selectedRelationship?.id
                                )
                            },
                            singleSelection = true
                        ),
                        target = BottomSheetTarget.RELATIONSHIP
                    )
                )
            }
        }
    }

    private fun submitInquiryRegistry(): Flow<PartialState> = flow {
        val state = uiState.value

//        if (!ValidationUtils.isNationalIdValid(state.dependentNationalId)) {
//            sendEvent(AddDependentEvent.ShowErrorDialog("خطا", "کد ملی معتبر نیست"))
//            return@flow
//        }
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
            PartialState.RegistryInquirySuccess(
                registryData = registryPR,
                stepperMode = evaluateStepperMode(registryPR, relationshipCode),
                requiredDocTypes = evaluateDocumentRequirements(registryPR, relationshipCode)
            ) as PartialState
        }.catch {
            emit(PartialState.Error(it.toSingleLineMessage()))
        }.collect {
            emit(it)
            if (it is PartialState.RegistryInquirySuccess) {
                emit(PartialState.StepChanged(STEP_VERIFICATION))
            }
        }
    }

    private fun submitInquiryEducation(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.educationCode.isBlank()) {
            sendEvent(
                AddDependentEvent.ShowErrorDialog(
                    "خطا",
                    "لطفا کد استعلام تحصیلی را وارد کنید"
                )
            )
            return@flow
        }

        emit(PartialState.Loading(true))
        inquiryEducationCodeUseCase(
            nationalId = state.dependentNationalId,
            educationCode = state.educationCode
        ).map { resultString ->
            PartialState.EducationInquirySuccess(resultString) as PartialState
        }.catch {
            emit(PartialState.Error(it.toSingleLineMessage()))
        }.collect {
            emit(it)
        }
    }

    private fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        docType: String
    ): Flow<PartialState> = flow {
        // Previously oversized files were truncated with copyOf(), which silently produced a
        // corrupt image. Reject them instead and let the user pick a smaller file.
        if (fileBytes.size > MAX_UPLOAD_SIZE_BYTES) {
            sendEvent(
                AddDependentEvent.ShowErrorDialog(
                    "خطا",
                    "حجم فایل انتخاب شده بیش از ۲ مگابایت است. لطفا فایل کوچک‌تری انتخاب کنید."
                )
            )
            return@flow
        }

        emit(PartialState.Loading(true))

        uploadDependentImageUseCase(
            imageBytes = fileBytes,
            fileName = fileName,
            mimeType = resolveMimeType(fileName)
        ).map { uploadDN ->
            val uploadPR = uploadDN.toPresentation()
            PartialState.DocumentUploaded(
                UploadedDocument(
                    guid = uploadPR.guid,
                    docType = docType,
                    fileName = fileName
                )
            ) as PartialState
        }.catch {
            emit(PartialState.Error(it.toSingleLineMessage()))
        }.collect {
            emit(it)
        }
    }

    private fun resolveMimeType(fileName: String): String = when {
        fileName.endsWith(".png", ignoreCase = true) -> "image/png"
        fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
        else -> "image/jpeg"
    }

    private fun onNextStepClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        when (state.currentStep) {
            STEP_VERIFICATION -> {
                if (state.selectedCityBirth == null || state.selectedCityIssuance == null || state.selectedBranch == null) {
                    sendEvent(
                        AddDependentEvent.ShowErrorDialog(
                            "خطا",
                            "لطفا اطلاعات محل تولد، صدور و شعبه را تکمیل کنید"
                        )
                    )
                    return@flow
                }
                when (state.stepperMode) {
                    StepperMode.SON_MODE -> {
                        // First tap runs the education inquiry so the university name can be
                        // confirmed; the following tap advances to the documents step.
                        if (state.needCallInquiryEducation) {
                            submitInquiryEducation().collect { emit(it) }
                        } else {
                            emit(PartialState.StepChanged(STEP_DOCUMENTS))
                        }
                    }

                    StepperMode.DAUGHTER_MODE -> {
                        if (!state.isDaughterCommitmentChecked) {
                            sendEvent(
                                AddDependentEvent.ShowErrorDialog(
                                    "خطا",
                                    "تایید تعهدنامه الزامی است"
                                )
                            )
                        } else {
                            emit(PartialState.StepChanged(STEP_DOCUMENTS))
                        }
                    }

                    StepperMode.DEFAULT_MODE -> emit(PartialState.StepChanged(STEP_DOCUMENTS))
                }
            }

            STEP_DOCUMENTS -> {
                if (!areRequiredDocumentsUploaded(state)) {
                    sendEvent(
                        AddDependentEvent.ShowErrorDialog(
                            "خطا",
                            "لطفا تمامی مدارک الزامی را بارگذاری کنید"
                        )
                    )
                } else {
                    submitFinalRequest().collect { emit(it) }
                }
            }
        }
    }

    private fun areRequiredDocumentsUploaded(state: AddDependentState): Boolean {
        val uploadedTypes = state.uploadedDocuments.mapTo(mutableSetOf()) { it.docType }
        return state.requiredDocTypes
            .filterNot { it.isDisabled }
            .all { uploadedTypes.contains(it.code) }
    }

    private fun submitFinalRequest(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Loading(true))

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
            requestFileList = state.uploadedDocuments.map { doc ->
                RequestFilePR(
                    documentFileId = doc.guid,
                    documentType = doc.docType
                )
            }
        )

        addNewDependentUseCase(requestPR.toDomain())
            .map { PartialState.StepChanged(STEP_SUCCESS) as PartialState }
            .catch { emit(PartialState.Error(it.toSingleLineMessage())) }
            .collect { emit(it) }
    }

    private fun evaluateStepperMode(data: RegistryDataPR, relationCode: String): StepperMode =
        if (data.age.toIntOrNull() != null) {
            when {
                relationCode == RELATION_CODE_SON && data.age.toInt() >= SON_EDUCATION_AGE_THRESHOLD -> StepperMode.SON_MODE
                relationCode == RELATION_CODE_DAUGHTER && data.age.toInt() >= DAUGHTER_COMMITMENT_AGE_THRESHOLD -> StepperMode.DAUGHTER_MODE
                else -> StepperMode.DEFAULT_MODE
            }
        } else {
            StepperMode.DEFAULT_MODE
        }

    private fun evaluateDocumentRequirements(
        data: RegistryDataPR,
        relationCode: String
    ): List<DocType> = when (relationCode) {
        RELATION_CODE_SPOUSE -> listOf(
            DocType("ID_CARD_PAGE_1", "صفحه اول شناسنامه"),
            DocType("ID_CARD_SPOUSE", "صفحه مشخصات همسر شناسنامه"),
            DocType("MARRIAGE_CERT", "عقدنامه")
        )

        RELATION_CODE_SON, RELATION_CODE_DAUGHTER -> listOf(
            DocType(
                code = "ID_CARD_PAGE_1",
                title = "صفحه اول شناسنامه",
                isDisabled = data.registryConfirmState == REGISTRY_STATE_ID_CARD_ON_FILE
            ),
            DocType("MARRIAGE_CERT", "عقدنامه", isDisabled = true)
        )

        else -> listOf(DocType("ID_CARD_PAGE_1", "صفحه اول شناسنامه"))
    }

    override fun reduceState(
        currentState: AddDependentState,
        partialState: PartialState
    ): AddDependentState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )

        is PartialState.ActiveBranchesLoaded -> currentState.copy(
            isLoading = false,
            activeBranches = partialState.branches,
            selectedBranch = currentState.selectedBranch ?: partialState.autoSelectedBranch,
            error = null
        )

        is PartialState.FamilyRelationshipsLoaded -> currentState.copy(
            isLoading = false,
            familyRelationships = partialState.relationships,
            error = null
        )

        is PartialState.CitiesLoaded -> currentState.copy(
            isLoading = false,
            cities = partialState.cities,
            error = null
        )

        is PartialState.NationalIdChanged -> currentState.resetInquiry().copy(
            dependentNationalId = partialState.id
        )

        is PartialState.BirthDateSelected -> currentState.resetInquiry().copy(
            birthDatePersian = partialState.persianDate,
            birthDateGregorian = partialState.gregorianDate,
            birthDateTimeStamp = partialState.timestamp
        )

        is PartialState.RelationshipSelected -> currentState.resetInquiry().copy(
            selectedRelationship = partialState.relationship
        )

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
        is PartialState.DocumentUploaded -> currentState.copy(
            isLoading = false,
            uploadedDocuments = currentState.uploadedDocuments
                .filterNot { it.docType == partialState.document.docType } + partialState.document,
            error = null
        )

        is PartialState.DocumentDeleted -> currentState.copy(
            uploadedDocuments = currentState.uploadedDocuments.filterNot { it.docType == partialState.docType }
        )

        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
            isLoading = false
        )

        is PartialState.BottomSheetStateChanged -> currentState.copy(
            bottomSheetConfig = partialState.config,
            bottomSheetTarget = partialState.target
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    /**
     * Any change to the inquiry inputs invalidates a previously fetched registry result and
     * the documents that were chosen based on it.
     */
    private fun AddDependentState.resetInquiry(): AddDependentState {
        val hasInquired = !needCallInquiryRegistry || registryData != null
        return copy(
            needCallInquiryRegistry = true,
            registryData = if (hasInquired) null else registryData,
            uploadedDocuments = if (hasInquired) emptyList() else uploadedDocuments
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

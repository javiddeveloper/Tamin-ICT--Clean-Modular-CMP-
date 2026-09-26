package com.tamin.taminhamrah.feature.addDependent.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentEvent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState.PartialState
import com.tamin.taminhamrah.feature.addDependent.ui.contract.BottomSheetTarget
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DOC_TYPE_ID_FIRST_PAGE
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DOC_TYPE_MARRIAGE_CERTIFICATE
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DOC_TYPE_SPOUSE_ID
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DocType
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_DOCUMENTS
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_INQUIRY
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_SUCCESS
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_VERIFICATION
import com.tamin.taminhamrah.feature.addDependent.ui.contract.StepperMode
import com.tamin.taminhamrah.feature.addDependent.ui.contract.UploadedDocument
import com.tamin.taminhamrah.feature.addDependent.ui.mapper.toDomain
import com.tamin.taminhamrah.feature.addDependent.ui.mapper.toPresentation
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.feature.addDependent.ui.model.RegistryDataPR
import com.tamin.taminhamrah.feature.addDependent.ui.model.RequestAddDependentPR
import com.tamin.taminhamrah.feature.addDependent.ui.model.RequestFilePR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.query.city.CityListQuery
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
import com.tamin.taminhamrah.useCases.common.GetCitiesPageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_title
import taminx.core.core_ui.error_file_too_large
import taminx.core.core_ui.error_image_duplicate
import taminx.core.core_ui.branch_picker_title
import taminx.core.core_ui.picker_relationship_title
import taminx.core.core_ui.error_select_birth_date
import taminx.core.core_ui.error_select_relationship
import taminx.core.core_ui.error_enter_education_code
import taminx.core.core_ui.error_education_inquiry_required
import taminx.core.core_ui.error_complete_additional_info
import taminx.core.core_ui.error_daughter_commitment_required
import taminx.core.core_ui.error_upload_all_docs

private const val MAX_UPLOAD_SIZE_BYTES = 2_000_000
private const val RELATION_CODE_SPOUSE = "01"
private const val RELATION_CODE_SON = "02"
private const val RELATION_CODE_DAUGHTER = "03"
private const val SON_EDUCATION_AGE_THRESHOLD = 19
private const val DAUGHTER_COMMITMENT_AGE_THRESHOLD = 18
private const val REGISTRY_STATE_ID_CARD_ON_FILE = "1"
private const val RELATIONSHIP_FILTER_WILDCARD = "**"

class AddDependentViewModel(
    private val getActiveBranchesUseCase: GetActiveBranchesUseCase,
    private val getFamilyRelationshipsFromProxyUseCase: GetFamilyRelationshipsFromProxyUseCase,
    private val inquiryRegistryUseCase: InquiryRegistryUseCase,
    private val inquiryEducationCodeUseCase: InquiryEducationCodeUseCase,
    private val uploadDependentImageUseCase: UploadDependentImageUseCase,
    private val addNewDependentUseCase: AddNewDependentUseCase,
    private val getCitiesPageUseCase: GetCitiesPageUseCase
) : BaseViewModel<AddDependentState, PartialState, AddDependentEvent, AddDependentIntent>(
    initialState = AddDependentState()
) {

    private val cityPaginator = Paginator(
        loadPage = { query -> getCitiesPageUseCase(query).first() },
    )

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
                emit(PartialState.CityPickerOpened(null))
            }
            is AddDependentIntent.OnCityIssuanceSelected -> flow {
                emit(PartialState.CityIssuanceSelected(intent.city))
                emit(PartialState.CityPickerOpened(null))
            }
            is AddDependentIntent.OnBranchSelected -> flow {
                emit(PartialState.BranchSelected(intent.branch))
                emit(dismissBottomSheet())
            }
            is AddDependentIntent.ShowRelationshipPicker -> showRelationshipPicker()
            is AddDependentIntent.ShowCityBirthPicker -> openCityPicker(BottomSheetTarget.CITY_BIRTH)
            is AddDependentIntent.ShowCityIssuancePicker -> openCityPicker(BottomSheetTarget.CITY_ISSUANCE)
            is AddDependentIntent.DismissCityPicker -> flow { emit(PartialState.CityPickerOpened(null)) }
            is AddDependentIntent.CitySearchQueryChanged -> flow {
                cityPaginator.refresh(cityBaseQuery(intent.query))
            }
            is AddDependentIntent.CityPickerLoadMore -> flow { cityPaginator.loadNext() }
            is AddDependentIntent.ShowBranchPicker -> flow {
                val state = uiState.value
                val titleString = getString(Res.string.branch_picker_title)
                emit(PartialState.BottomSheetStateChanged(
                    config = TaminBottomSheetConfig(
                        title = titleString,
                        type = TaminBottomSheetType.CUSTOM,
                        items = state.activeBranches.mapIndexed { index, branch ->
                            TaminBottomSheetItem(id = index, title = branch.branchName.ifBlank { branch.branchCode }, isSelected = branch.branchCode == state.selectedBranch?.branchCode)
                        },
                        singleSelection = true,
                        showSearchInput = true
                    ),
                    target = BottomSheetTarget.BRANCH
                ))
            }
            is AddDependentIntent.DismissBottomSheet -> flow { emit(dismissBottomSheet()) }
            is AddDependentIntent.UploadDocument -> uploadDocument(intent.fileBytes, intent.fileName, intent.docType)
            is AddDependentIntent.DeleteDocument -> flow { emit(PartialState.DocumentDeleted(intent.docType)) }
            is AddDependentIntent.OnFileReadError -> flow {
                val errorTitle = getString(Res.string.error_title)
                sendEvent(AddDependentEvent.ShowErrorDialog(errorTitle, intent.message))
            }
            is AddDependentIntent.OnNextStepClicked -> onNextStepClicked()
            is AddDependentIntent.OnPreviousStepClicked -> flow {
                val prevStep = (uiState.value.currentStep - 1).coerceAtLeast(STEP_INQUIRY)
                emit(PartialState.StepChanged(prevStep))
            }
            is AddDependentIntent.SubmitFinalRequest -> submitFinalRequest()
        }
    }

    private fun dismissBottomSheet() = PartialState.BottomSheetStateChanged(null, null)

    private fun initData(): Flow<PartialState> =
        merge(loadActiveBranches(), observeCityPaging()).onStart { emit(PartialState.Loading(true)) }

    private fun loadActiveBranches(): Flow<PartialState> = getActiveBranchesUseCase()
        .map { branchList ->
            val prBranches = branchList.map { it.toPresentation() }
            PartialState.ActiveBranchesLoaded(prBranches, prBranches.singleOrNull()) as PartialState
        }
        .catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun openCityPicker(target: BottomSheetTarget): Flow<PartialState> = flow {
        emit(PartialState.CityPickerOpened(target))
        cityPaginator.refresh(cityBaseQuery(""))
    }

    private fun observeCityPaging(): Flow<PartialState> = cityPaginator.state.transform { paging ->
        emit(
            PartialState.CityPagingChanged(
                items = paging.items.toCityPresentation(),
                isLoadingFirstPage = paging.isLoadingFirstPage,
                isLoadingNextPage = paging.isLoadingNextPage,
                endReached = paging.endReached,
            ),
        )
        paging.error?.let { emit(PartialState.Error(it.toSingleLineMessage())) }
    }

    private fun cityBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = CityListQuery.filters(cityName = query.takeIf { it.isNotBlank() }),
    )

    private fun showRelationshipPicker(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Loading(true))
        getFamilyRelationshipsFromProxyUseCase(listOf(ApiFilterDN(FilterProperty.DEPENDENCY_DESC, RELATIONSHIP_FILTER_WILDCARD, FilterOperator.LIKE)))
            .map { it.map { item -> item.toPresentation() } }
            .catch { e -> this@flow.emit(PartialState.Error(e.toSingleLineMessage())) }
            .collect { relationships ->
                emit(PartialState.FamilyRelationshipsLoaded(relationships))
                emit(PartialState.BottomSheetStateChanged(
                    config = TaminBottomSheetConfig(
                        title = getString(Res.string.picker_relationship_title),
                        type = TaminBottomSheetType.CUSTOM,
                        items = relationships.map {
                            TaminBottomSheetItem(id = it.id ?: 0, title = it.relationDesc.orEmpty(), isSelected = it.id == state.selectedRelationship?.id)
                        },
                        singleSelection = true
                    ),
                    target = BottomSheetTarget.RELATIONSHIP
                ))
            }
    }

    private fun submitInquiryRegistry(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isLoading) return@flow
        if (state.birthDateTimeStamp.isBlank() && state.birthDatePersian.isBlank()) {
            showErrorDialog(getString(Res.string.error_select_birth_date))
            return@flow
        }
        if (state.selectedRelationship == null) {
            showErrorDialog(getString(Res.string.error_select_relationship))
            return@flow
        }
        emit(PartialState.Loading(true))
        val relationshipCode = state.selectedRelationship.relationCode.orEmpty()
        inquiryRegistryUseCase(state.dependentNationalId, state.birthDateTimeStamp, relationshipCode)
            .map { it.toPresentation() }
            .catch { e -> this@flow.emit(PartialState.Error(e.toSingleLineMessage())) }
            .collect { registryPR ->
                emit(PartialState.RegistryInquirySuccess(registryPR, evaluateStepperMode(registryPR, relationshipCode), evaluateDocumentRequirements(registryPR, relationshipCode)))
                emit(PartialState.StepChanged(STEP_VERIFICATION))
            }
    }

    private fun submitInquiryEducation(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isLoading) return@flow
        if (state.educationCode.isBlank()) {
            showErrorDialog(getString(Res.string.error_enter_education_code))
            return@flow
        }
        emit(PartialState.Loading(true))
        inquiryEducationCodeUseCase(state.dependentNationalId, state.educationCode)
            .map { PartialState.EducationInquirySuccess(it) as PartialState }
            .catch { e -> this@flow.emit(PartialState.Error(e.toSingleLineMessage())) }
            .collect { emit(it) }
    }

    private fun uploadDocument(fileBytes: ByteArray, fileName: String, docType: String): Flow<PartialState> = flow {
        if (uiState.value.isLoading) return@flow
        if (fileBytes.size > MAX_UPLOAD_SIZE_BYTES) {
            showErrorDialog(getString(Res.string.error_file_too_large))
            return@flow
        }
        val newFileHash = fileBytes.contentHashCode()
        if (uiState.value.uploadedDocuments.any { it.contentHash == newFileHash }) {
            showErrorDialog(getString(Res.string.error_image_duplicate))
            return@flow
        }
        emit(PartialState.Loading(true))
        uploadDependentImageUseCase(fileBytes, fileName, resolveMimeType(fileName))
            .map { it.toPresentation() }
            .catch { e -> this@flow.emit(PartialState.Error(e.toSingleLineMessage())) }
            .collect { uploadPR ->
                emit(PartialState.DocumentUploaded(UploadedDocument(uploadPR.guid, docType, fileName, newFileHash)))
            }
    }

    private fun resolveMimeType(fileName: String): String = when {
        fileName.endsWith(".png", ignoreCase = true) -> "image/png"
        fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
        else -> "image/jpeg"
    }

    private fun onNextStepClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isLoading) return@flow
        when (state.currentStep) {
            STEP_VERIFICATION -> {
                if (state.selectedCityBirth == null || state.selectedCityIssuance == null || state.selectedBranch == null) {
                    showErrorDialog(getString(Res.string.error_complete_additional_info))
                    return@flow
                }
                when (state.stepperMode) {
                    StepperMode.SON_MODE -> if (state.needCallInquiryEducation) {
                        val educationError = if (state.educationCode.isBlank()) {
                            getString(Res.string.error_enter_education_code)
                        } else {
                            getString(Res.string.error_education_inquiry_required)
                        }
                        showErrorDialog(educationError)
                    } else emit(PartialState.StepChanged(STEP_DOCUMENTS))
                    StepperMode.DAUGHTER_MODE -> if (!state.isDaughterCommitmentChecked) {
                        showErrorDialog(getString(Res.string.error_daughter_commitment_required))
                    } else emit(PartialState.StepChanged(STEP_DOCUMENTS))
                    StepperMode.DEFAULT_MODE -> emit(PartialState.StepChanged(STEP_DOCUMENTS))
                }
            }
            STEP_DOCUMENTS -> if (!areRequiredDocumentsUploaded(state)) {
                showErrorDialog(getString(Res.string.error_upload_all_docs))
            } else submitFinalRequest().collect { emit(it) }
        }
    }

    private fun areRequiredDocumentsUploaded(state: AddDependentState): Boolean {
        val uploadedTypes = state.uploadedDocuments.mapTo(mutableSetOf()) { it.docType }
        return state.requiredDocTypes.filterNot { it.isDisabled }.all { uploadedTypes.contains(it.code) }
    }

    private fun submitFinalRequest(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isLoading) return@flow
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
            requestFileList = state.uploadedDocuments.map { RequestFilePR(it.guid, it.docType) }
        )
        addNewDependentUseCase(requestPR.toDomain())
            .map { PartialState.StepChanged(STEP_SUCCESS) as PartialState }
            .catch { e -> this@flow.emit(PartialState.Error(e.toSingleLineMessage())) }
            .collect { emit(it) }
    }

    private fun evaluateStepperMode(data: RegistryDataPR, relationCode: String): StepperMode =
        data.age.toIntOrNull()?.let { age ->
            when {
                relationCode == RELATION_CODE_SON && age >= SON_EDUCATION_AGE_THRESHOLD -> StepperMode.SON_MODE
                relationCode == RELATION_CODE_DAUGHTER && age >= DAUGHTER_COMMITMENT_AGE_THRESHOLD -> StepperMode.DAUGHTER_MODE
                else -> StepperMode.DEFAULT_MODE
            }
        } ?: StepperMode.DEFAULT_MODE

    private fun evaluateDocumentRequirements(data: RegistryDataPR, relationCode: String): List<DocType> =
        when (relationCode) {
            RELATION_CODE_SPOUSE -> listOf(
                DocType(DOC_TYPE_ID_FIRST_PAGE),
                DocType(DOC_TYPE_SPOUSE_ID),
                DocType(DOC_TYPE_MARRIAGE_CERTIFICATE)
            )
            RELATION_CODE_SON, RELATION_CODE_DAUGHTER -> listOf(
                DocType(DOC_TYPE_ID_FIRST_PAGE, data.registryConfirmState == REGISTRY_STATE_ID_CARD_ON_FILE),
                DocType(DOC_TYPE_MARRIAGE_CERTIFICATE, isDisabled = true)
            )
            else -> listOf(DocType(DOC_TYPE_ID_FIRST_PAGE))
        }

    private suspend fun showErrorDialog(message: String) {
        sendEvent(AddDependentEvent.ShowErrorDialog(getString(Res.string.error_title), message))
    }

    override fun reduceState(currentState: AddDependentState, partialState: PartialState): AddDependentState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.ActiveBranchesLoaded -> currentState.copy(isLoading = false, activeBranches = partialState.branches, selectedBranch = currentState.selectedBranch ?: partialState.autoSelectedBranch, error = null)
        is PartialState.FamilyRelationshipsLoaded -> currentState.copy(isLoading = false, familyRelationships = partialState.relationships, error = null)
        is PartialState.CityPagingChanged -> currentState.copy(
            cities = partialState.items,
            isCitiesLoading = partialState.isLoadingFirstPage,
            isCitiesLoadingMore = partialState.isLoadingNextPage,
            canLoadMoreCities = !partialState.endReached,
        )
        is PartialState.CityPickerOpened -> currentState.copy(activeCityPicker = partialState.target)
        is PartialState.NationalIdChanged -> currentState.resetInquiry().copy(dependentNationalId = partialState.id)
        is PartialState.BirthDateSelected -> currentState.resetInquiry().copy(birthDatePersian = partialState.persianDate, birthDateGregorian = partialState.gregorianDate, birthDateTimeStamp = partialState.timestamp)
        is PartialState.RelationshipSelected -> currentState.resetInquiry().copy(selectedRelationship = partialState.relationship)
        is PartialState.RegistryInquirySuccess -> currentState.copy(isLoading = false, registryData = partialState.registryData, stepperMode = partialState.stepperMode, requiredDocTypes = partialState.requiredDocTypes, needCallInquiryRegistry = false, error = null)
        is PartialState.EducationCodeChanged -> currentState.copy(educationCode = partialState.code, universityName = "", needCallInquiryEducation = true)
        is PartialState.EducationInquirySuccess -> currentState.copy(isLoading = false, universityName = partialState.universityName, needCallInquiryEducation = false, error = null)
        is PartialState.DaughterCommitmentToggled -> currentState.copy(isDaughterCommitmentChecked = partialState.isChecked)
        is PartialState.CityBirthSelected -> currentState.copy(selectedCityBirth = partialState.city)
        is PartialState.CityIssuanceSelected -> currentState.copy(selectedCityIssuance = partialState.city)
        is PartialState.BranchSelected -> currentState.copy(selectedBranch = partialState.branch)
        is PartialState.DocumentUploaded -> currentState.copy(isLoading = false, uploadedDocuments = currentState.uploadedDocuments.filterNot { it.docType == partialState.document.docType } + partialState.document, error = null)
        is PartialState.DocumentDeleted -> currentState.copy(uploadedDocuments = currentState.uploadedDocuments.filterNot { it.docType == partialState.docType })
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step, isLoading = false)
        is PartialState.BottomSheetStateChanged -> currentState.copy(bottomSheetConfig = partialState.config, bottomSheetTarget = partialState.target)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    private fun AddDependentState.resetInquiry(): AddDependentState {
        val hasInquired = !needCallInquiryRegistry
        return copy(needCallInquiryRegistry = true, registryData = if (hasInquired) null else registryData, uploadedDocuments = if (hasInquired) emptyList() else uploadedDocuments)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

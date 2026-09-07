package com.tamin.taminhamrah.feature.contracts.flow.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.preflight.ContractPreflightBlock
import com.tamin.taminhamrah.feature.contracts.flow.preflight.resolvePreflightBlock
import com.tamin.taminhamrah.feature.contracts.flow.specialjob.SpecialFreeJobDecision
import com.tamin.taminhamrah.feature.contracts.flow.specialjob.SpecialFreeJobRejectReason
import com.tamin.taminhamrah.feature.contracts.flow.specialjob.resolveMedicalStudentSelection
import com.tamin.taminhamrah.feature.contracts.flow.specialjob.resolveRedCrescentSelection
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceSpecialJobCode
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowEvent
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowIntent
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowUiState
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowUiState.PartialState
import com.tamin.taminhamrah.mapper.common.filterByProvinceCode
import com.tamin.taminhamrah.mapper.contracts.resolveEligibility
import com.tamin.taminhamrah.mapper.contracts.toPresentation as toContractResultPresentation
import com.tamin.taminhamrah.mapper.contracts.toPresentation as toPremiumRangePresentation
import com.tamin.taminhamrah.mapper.contracts.toSpcPremiumRateOptions
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.mapper.contracts.toBranchPresentation
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.contractFlow.isEditableFromSummary
import com.tamin.taminhamrah.model.contractFlow.GuardianFormPR
import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contractFlow.UploadImagePR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.useCases.contracts.CalculateFreelanceSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.CalculateOptionalSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.CheckMedicalStudentUseCase
import com.tamin.taminhamrah.useCases.contracts.CheckRedCrossStatusUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreeJobWagesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreelancePremiumRangeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetOptionalPremiumRangeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.GetSpcPremiumRatesUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeContractUseCase
import com.tamin.taminhamrah.useCases.contracts.SaveContactUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.mapper.subdominant.toPresentation
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_error_medical_student_not_allowed
import taminx.core.core_ui.contract_error_red_crescent_day_limit
import taminx.core.core_ui.contract_error_red_crescent_not_eligible
import taminx.core.core_ui.contract_female_only_service
import taminx.core.core_ui.contract_preflight_active_contract
import taminx.core.core_ui.contract_preflight_cancelled_20_days
import taminx.core.core_ui.contract_preflight_cancelled_3_months
import taminx.core.core_ui.contract_preflight_not_registered
import taminx.core.core_ui.contract_preflight_other_contract
import taminx.core.core_ui.contract_preflight_under_age
import taminx.core.core_ui.contract_preflight_contracts_load_failed
import taminx.core.core_ui.contract_submit_failure_message_fallback
import taminx.core.core_ui.contract_treatment_dependents_load_error
import taminx.core.core_ui.contract_upload_failed_error
import taminx.core.core_ui.contract_upload_jpeg_only_error

class ContractFlowViewModel(
    private val config: ContractFlowConfig,
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val getContractsUseCase: GetContractsUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getSpcPremiumRatesUseCase: GetSpcPremiumRatesUseCase,
    private val getFreelancePremiumRangeUseCase: GetFreelancePremiumRangeUseCase,
    private val getOptionalPremiumRangeUseCase: GetOptionalPremiumRangeUseCase,
    private val calculateFreelanceSalaryUseCase: CalculateFreelanceSalaryUseCase,
    private val calculateOptionalSalaryUseCase: CalculateOptionalSalaryUseCase,
    private val getFreeJobWagesUseCase: GetFreeJobWagesUseCase,
    private val checkRedCrossStatusUseCase: CheckRedCrossStatusUseCase,
    private val checkMedicalStudentUseCase: CheckMedicalStudentUseCase,
    private val makeContractUseCase: MakeContractUseCase,
    private val saveContactUseCase: SaveContactUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val subdominantUseCase: SubdominantUseCase,
) : BaseViewModel<
    ContractFlowUiState,
    PartialState,
    ContractFlowEvent,
    ContractFlowIntent,
    >(
    initialState = ContractFlowUiState(
        config = config,
        allowsOnlinePayment = config.allowsOnlinePaymentAfterSubmit,
        currentStep = config.steps.first(),
    ),
) {
    override fun handleIntent(intent: ContractFlowIntent): Flow<PartialState> {
        return when (intent) {
            ContractFlowIntent.LoadInitialData -> handleLoadInitialData()
            ContractFlowIntent.GoToNextStep -> handleGoToNextStep()
            ContractFlowIntent.GoToPreviousStep -> handleGoToPreviousStep()
            is ContractFlowIntent.EditStep -> handleEditStep(intent.step)
            ContractFlowIntent.SaveEdit -> handleSaveEdit()
            is ContractFlowIntent.SetRulesConfirmed -> handleSetRulesConfirmed(intent.confirmed)
            is ContractFlowIntent.UpdateUserInfo -> handleUpdateUserInfo(intent.userInfo)
            is ContractFlowIntent.SetContractApplicantType -> handleSetContractApplicantType(intent.type)
            is ContractFlowIntent.UpdateGuardianForm -> handleUpdateGuardianForm(intent.form)
            is ContractFlowIntent.UploadGuardianImage -> handleUploadGuardianImage(intent.fileName, intent.bytes)
            ContractFlowIntent.ClearGuardianDocument -> handleClearGuardianDocument()
            is ContractFlowIntent.SelectBranchProvince -> handleSelectBranchProvince(intent.province)
            is ContractFlowIntent.SelectBranchCity -> handleSelectBranchCity(intent.city)
            is ContractFlowIntent.SelectBranch -> handleSelectBranch(intent.branch)
            is ContractFlowIntent.UpdateDocumentDescription -> handleUpdateDocumentDescription(intent.description)
            is ContractFlowIntent.UploadPickedImage -> handleUploadPickedImage(intent.fileName, intent.bytes)
            ContractFlowIntent.ClearUploadedDocument -> handleClearUploadedDocument()
            is ContractFlowIntent.SelectPremiumRate -> handleSelectPremiumRate(intent.rate)
            is ContractFlowIntent.SelectFreeJob -> handleSelectFreeJob(intent.job)
            is ContractFlowIntent.SelectMonthlyPremium -> handleSelectMonthlyPremium(intent.amount)
            ContractFlowIntent.CalculateMonthlyPremium -> handleCalculateMonthlyPremium()
            is ContractFlowIntent.SetAgreementConfirmed -> handleSetAgreementConfirmed(intent.confirmed)
            is ContractFlowIntent.SelectTreatmentSupport -> handleSelectTreatmentSupport(intent.withSupport)
            is ContractFlowIntent.SetTreatmentCommitment -> handleSetTreatmentCommitment(intent.confirmed)
            ContractFlowIntent.LoadDependents -> handleLoadDependents()
            ContractFlowIntent.SubmitContract -> handleSubmitContract()
        }
    }

    private fun handleLoadInitialData(): Flow<PartialState> = merge(
        loadRegistrationInfo(),
        loadContracts(),
        loadAllContracts(),
        loadCities(),
        loadProvinces(),
        loadPremiumRates(),
        loadFreeJobsIfNeeded(),
    )

    private fun loadFreeJobsIfNeeded(): Flow<PartialState> = flow {
        if (!config.requiresFreeJob) return@flow
        emit(PartialState.FreeJobsLoading(true))
        try {
            getFreeJobWagesUseCase().collect { jobs ->
                emit(PartialState.FreeJobsLoaded(jobs))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.FreeJobsLoading(false))
        }
    }

    private fun loadRegistrationInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getRegistrationInfoUseCase().collect { info ->
                val presentation = info.toPresentation()
                emit(PartialState.RegistrationInfoLoaded(presentation))
                emit(PartialState.UserInfoChanged(UserInfoFormPR.fromRegistration(presentation)))
                if (config.requiresFemaleGender && !presentation.isFemale) {
                    emit(PartialState.GenderGateError(getString(Res.string.contract_female_only_service)))
                }
                emitPreflightGateIfReady()
            }
        } catch (e: Exception) {
            emitError(e.message)
        }
    }

    private fun loadAllContracts(): Flow<PartialState> = flow {
        try {
            getContractsUseCase().collect { contracts ->
                emit(PartialState.AllContractsLoaded(contracts))
                emitPreflightGateIfReady()
            }
        } catch (e: Exception) {
            emit(PartialState.AllContractsLoadFailed)
            emitPreflightGateIfReady()
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.emitPreflightGateIfReady() {
        val registration = uiState.value.registrationInfo ?: return
        if (!uiState.value.hasLoadedTypedContracts) return
        if (!uiState.value.hasLoadedAllContracts) return
        if (uiState.value.allContractsLoadFailed) {
            emit(PartialState.PreflightGateError(getString(Res.string.contract_preflight_contracts_load_failed)))
            return
        }
        val block = resolvePreflightBlock(
            registration = registration,
            typedContracts = uiState.value.rawTypedContracts,
            allContracts = uiState.value.allContracts,
            currentPremiumTypeCode = config.premiumTypeCode,
        ) ?: return
        emit(PartialState.PreflightGateError(preflightMessage(block)))
    }

    private suspend fun preflightMessage(block: ContractPreflightBlock): String = when (block) {
        ContractPreflightBlock.NOT_REGISTERED -> getString(Res.string.contract_preflight_not_registered)
        ContractPreflightBlock.ACTIVE_CONTRACT -> getString(Res.string.contract_preflight_active_contract)
        ContractPreflightBlock.UNDER_AGE -> getString(Res.string.contract_preflight_under_age)
        ContractPreflightBlock.CANCELLED_OVER_20_DAYS -> getString(Res.string.contract_preflight_cancelled_20_days)
        ContractPreflightBlock.CANCELLED_OVER_3_MONTHS -> getString(Res.string.contract_preflight_cancelled_3_months)
        ContractPreflightBlock.OTHER_ACTIVE_CONTRACT -> getString(Res.string.contract_preflight_other_contract)
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.emitError(message: String?) {
        emit(PartialState.Error(message))
        message?.let { sendEvent(ContractFlowEvent.ShowMessage(it)) }
    }

    private fun loadContracts(): Flow<PartialState> = flow {
        try {
            getContractsUseCase.contractsByPremiumType(config.premiumTypeCode).collect { contracts ->
                emit(PartialState.RawTypedContractsLoaded(contracts))
                emit(PartialState.EligibilityLoaded(contracts.resolveEligibility()))
                emit(PartialState.ContractsLoaded(contracts.toPresentation()))
                emitPreflightGateIfReady()
            }
        } catch (e: Exception) {
            emitError(e.message)
        }
    }

    private fun loadCities(): Flow<PartialState> = flow {
        emit(PartialState.CitiesLoading(true))
        try {
            identityInfoUseCase.getCities().collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityPresentation()))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.CitiesLoading(false))
        }
    }

    private fun loadProvinces(): Flow<PartialState> = flow {
        emit(PartialState.ProvincesLoading(true))
        try {
            identityInfoUseCase.getProvinces().collect { provinces ->
                emit(PartialState.ProvincesLoaded(provinces.toProvincePresentation()))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.ProvincesLoading(false))
        }
    }

    private fun loadPremiumRates(): Flow<PartialState> = flow {
        if (!config.hasPremiumRateStep) return@flow
        emit(PartialState.PremiumRatesLoading(true))
        try {
            getSpcPremiumRatesUseCase().collect { rates ->
                val filteredRates = rates
                    .filter { it.selfIsuTypeCode == config.premiumTypeCode }
                    .ifEmpty { rates }
                emit(PartialState.PremiumRatesLoaded(filteredRates.toSpcPremiumRateOptions()))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.PremiumRatesLoading(false))
        }
    }

    private fun handleSelectBranchProvince(province: ProvincePR): Flow<PartialState> = merge(
        flow {
            emit(
                PartialState.BranchSelectionChanged(
                    uiState.value.branchSelection.copy(
                        provinceCode = province.provinceCode,
                        provinceName = province.provinceName,
                        cityCode = "",
                        cityName = "",
                        branchCode = "",
                        branchName = "",
                    ),
                ),
            )
            emit(PartialState.BranchCitiesLoaded(emptyList()))
            emit(PartialState.BranchesLoaded(emptyList()))
        },
        loadBranchCities(province.provinceCode),
    )

    private fun loadBranchCities(provinceCode: String): Flow<PartialState> = flow {
        if (provinceCode.isBlank()) return@flow
        emit(PartialState.BranchCitiesLoading(true))
        try {
            identityInfoUseCase.getCities(provinceCode = provinceCode).collect { cities ->
                emit(PartialState.BranchCitiesLoaded(cities.toCityPresentation()))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.BranchCitiesLoading(false))
        }
    }

    private fun loadBranches(cityCode: String): Flow<PartialState> = flow {
        emit(PartialState.BranchesLoading(true))
        try {
            getBranchesUseCase(cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toBranchPresentation()))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.BranchesLoading(false))
        }
    }

    private fun handleGoToNextStep(): Flow<PartialState> {
        val flowConfig = uiState.value.config ?: config
        val currentStep = uiState.value.currentStep
        val nextStep = flowConfig.nextStep(currentStep) ?: return flow { }
        if (!uiState.value.canGoNext) return flow { }

        if (currentStep == ContractStep.STEP_USER_INFO) {
            if (!hasContactChanged()) {
                return merge(
                    flow { emit(PartialState.StepChanged(nextStep)) },
                    premiumRangeFlowForStep(nextStep, flowConfig),
                )
            }
            return saveContactThenAdvance(nextStep)
        }

        return merge(
            flow { emit(PartialState.StepChanged(nextStep)) },
            premiumRangeFlowForStep(nextStep, flowConfig),
        )
    }

    private fun premiumRangeFlowForStep(
        nextStep: ContractStep,
        flowConfig: ContractFlowConfig,
    ): Flow<PartialState> = when {
        nextStep == ContractStep.STEP_SALARY && flowConfig.usesFreelancePremiumRange ->
            loadFreelancePremiumRange()
        nextStep == ContractStep.STEP_SALARY && flowConfig.isOptionalInsurance ->
            loadOptionalPremiumRange()
        else -> flow { }
    }

    private fun hasContactChanged(): Boolean {
        val userInfo = uiState.value.userInfo
        val info = uiState.value.registrationInfo ?: return true
        return userInfo.address != info.address ||
            userInfo.zipCode != info.zipCode ||
            userInfo.phoneNumber != info.phoneNumber
    }

    private fun saveContactThenAdvance(nextStep: ContractStep): Flow<PartialState> = flow {
        val flowConfig = uiState.value.config ?: config
        emit(PartialState.SavingContact(true))
        try {
            saveContactUseCase(buildSaveContactParams()).first()
            emit(PartialState.ContactSaved)
            emit(PartialState.StepChanged(nextStep))
            when {
                nextStep == ContractStep.STEP_SALARY && flowConfig.usesFreelancePremiumRange ->
                    emitAll(loadFreelancePremiumRange())
                nextStep == ContractStep.STEP_SALARY && flowConfig.isOptionalInsurance ->
                    emitAll(loadOptionalPremiumRange())
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.SavingContact(false))
        }
    }

    private fun buildSaveContactParams(): SaveContactRequestDN {
        val userInfo = uiState.value.userInfo
        return SaveContactRequestDN(
            address = userInfo.address,
            mobile = userInfo.mobileNumber,
            ssn = uiState.value.registrationInfo?.nationalId.orEmpty(),
            phoneNumber = userInfo.phoneNumber,
            zipCode = userInfo.zipCode,
        )
    }

    private fun handleGoToPreviousStep(): Flow<PartialState> {
        if (uiState.value.isEditMode) {
            return flow {
                emit(
                    PartialState.StepChanged(
                        step = ContractStep.STEP_SUBMIT_CONTRACT,
                        isEditMode = false,
                    ),
                )
            }
        }
        return merge(
            flow {
                val flowConfig = uiState.value.config ?: config
                val previousStep = flowConfig.previousStep(uiState.value.currentStep) ?: return@flow
                emit(PartialState.StepChanged(previousStep))
            },
            flow {
                val flowConfig = uiState.value.config ?: config
                val previousStep = flowConfig.previousStep(uiState.value.currentStep) ?: return@flow
                if (previousStep == ContractStep.STEP_INSURANCE_PREMIUM) {
                    emitAll(reloadPremiumRangeIfNeeded(flowConfig))
                }
            },
        )
    }

    private fun handleEditStep(step: ContractStep): Flow<PartialState> {
        val flowConfig = uiState.value.config ?: config
        if (step !in flowConfig.steps || !step.isEditableFromSummary()) return flow { }
        return merge(
            flow { emit(PartialState.StepChanged(step = step, isEditMode = true)) },
            when (step) {
                ContractStep.STEP_INSURANCE_PREMIUM,
                ContractStep.STEP_SALARY,
                -> reloadPremiumRangeIfNeeded(flowConfig)
                else -> flow { }
            },
        )
    }

    private fun handleSaveEdit(): Flow<PartialState> {
        if (!uiState.value.isEditMode || !uiState.value.canGoNext) return flow { }
        if (uiState.value.currentStep == ContractStep.STEP_USER_INFO && hasContactChanged()) {
            return saveContactThenAdvance(ContractStep.STEP_SUBMIT_CONTRACT)
        }
        return flow {
            emit(
                PartialState.StepChanged(
                    step = ContractStep.STEP_SUBMIT_CONTRACT,
                    isEditMode = false,
                ),
            )
        }
    }

    private fun reloadPremiumRangeIfNeeded(flowConfig: ContractFlowConfig): Flow<PartialState> {
        val state = uiState.value
        if (state.premiumRange != null || state.isPremiumRangeLoading) return flow { }
        val hasRate = state.selectedPremiumRateCode != null || state.lockedPremiumRateCode != null
        if (!hasRate) return flow { }
        return when {
            flowConfig.usesFreelancePremiumRange && !state.hidePremiumSlider ->
                loadFreelancePremiumRange()
            flowConfig.isOptionalInsurance ->
                loadOptionalPremiumRange()
            else -> flow { }
        }
    }

    private fun handleSetRulesConfirmed(confirmed: Boolean): Flow<PartialState> = flow {
        emit(PartialState.RulesConfirmedChanged(confirmed))
    }

    private fun handleUpdateUserInfo(userInfo: UserInfoFormPR): Flow<PartialState> = flow {
        emit(PartialState.UserInfoChanged(userInfo))
    }

    private fun handleSetContractApplicantType(type: ContractApplicantType): Flow<PartialState> = flow {
        emit(PartialState.ContractApplicantTypeChanged(type))
    }

    private fun handleUpdateGuardianForm(form: GuardianFormPR): Flow<PartialState> = flow {
        emit(PartialState.GuardianFormChanged(form))
    }

    private fun handleUploadGuardianImage(fileName: String, bytes: ByteArray): Flow<PartialState> = flow {
        if (!isJpegFileName(fileName)) {
            val errorMsg = getString(Res.string.contract_upload_jpeg_only_error)
            emit(PartialState.GuardianFormChanged(uiState.value.guardianForm.copy(uploadError = errorMsg)))
            sendEvent(ContractFlowEvent.ShowMessage(errorMsg))
            return@flow
        }
        emit(PartialState.GuardianDocumentUploading(true))
        try {
            val request = UploadImageRequestDN(
                fileName = fileName,
                bytes = bytes,
                description = "تصویر قیم نامه",
            )
            uploadImageUseCase(request).collect { imageId ->
                emit(PartialState.GuardianDocumentUploaded(guid = imageId, name = fileName, bytes = bytes))
            }
        } catch (e: Exception) {
            val message = e.message ?: getString(Res.string.contract_upload_failed_error)
            emit(PartialState.GuardianFormChanged(uiState.value.guardianForm.copy(uploadError = message, isUploadingDocument = false)))
            sendEvent(ContractFlowEvent.ShowMessage(message))
        }
    }

    private fun handleClearGuardianDocument(): Flow<PartialState> = flow {
        emit(PartialState.GuardianDocumentCleared)
    }

    private fun handleSelectBranchCity(city: CityPR): Flow<PartialState> = merge(
        flow {
            emit(
                PartialState.BranchSelectionChanged(
                    uiState.value.branchSelection.copy(
                        cityCode = city.cityCode,
                        cityName = city.cityName,
                        branchCode = "",
                        branchName = "",
                    ),
                ),
            )
            emit(PartialState.BranchesLoaded(emptyList()))
        },
        loadBranches(city.cityCode),
    )

    private fun handleSelectBranch(branch: BranchPR): Flow<PartialState> = flow {
        emit(
            PartialState.BranchSelectionChanged(
                uiState.value.branchSelection.copy(
                    branchCode = branch.code,
                    branchName = branch.name,
                ),
            ),
        )
    }

    private fun handleUpdateDocumentDescription(description: String): Flow<PartialState> = flow {
        emit(PartialState.DocumentDescriptionChanged(description))
    }

    private fun handleUploadPickedImage(fileName: String, bytes: ByteArray): Flow<PartialState> = flow {
        emit(PartialState.UploadDocumentError(null))
        if (!isJpegFileName(fileName)) {
            emit(PartialState.UploadDocumentError(getString(Res.string.contract_upload_jpeg_only_error)))
            return@flow
        }
        emit(PartialState.DocumentPreviewSet(bytes))
        emit(PartialState.UploadingDocument(true))
        try {
            val request = UploadImageRequestDN(
                fileName = fileName,
                bytes = bytes,
                description = uiState.value.documentDescription.takeIf { it.isNotBlank() },
            )
            uploadImageUseCase(request).collect { imageId ->
                emit(
                    PartialState.DocumentUploaded(
                        UploadImagePR(
                            imageId = imageId,
                            fileName = fileName,
                            description = uiState.value.documentDescription,
                        ),
                    ),
                )
            }
        } catch (e: Exception) {
            val message = e.message ?: getString(Res.string.contract_upload_failed_error)
            emit(PartialState.UploadDocumentError(message))
            sendEvent(ContractFlowEvent.ShowMessage(message))
        } finally {
            emit(PartialState.UploadingDocument(false))
        }
    }

    private fun handleClearUploadedDocument(): Flow<PartialState> = flow {
        emit(PartialState.UploadedDocumentCleared)
    }

    private fun isJpegFileName(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }

    private fun handleSelectPremiumRate(rate: SpcPremiumRateOptionPR): Flow<PartialState> = flow {
        if (uiState.value.lockedPremiumRateCode != null) return@flow
        emit(PartialState.PremiumRateSelected(rate.code))
        val flowConfig = uiState.value.config ?: config
        when {
            flowConfig.usesFreelancePremiumRange && !uiState.value.hidePremiumSlider ->
                emitAll(loadFreelancePremiumRange(rate.code))
            flowConfig.isOptionalInsurance ->
                emitAll(loadOptionalPremiumRange())
        }
    }

    private fun handleSelectFreeJob(job: FreeJobDN): Flow<PartialState> = flow {
        val jobCode = job.jobCode.orEmpty()
        val jobName = job.discrioption.orEmpty()

        when (jobCode) {
            FreelanceSpecialJobCode.RED_CRESCENT_CODE -> {
                try {
                    val (_, _, jalaliDay) = PersianDateFormatter.today()
                    val status = checkRedCrossStatusUseCase().first()
                    when (val decision = resolveRedCrescentSelection(jalaliDay, status)) {
                        is SpecialFreeJobDecision.Rejected -> {
                            emitError(getString(decision.reason.toStringRes()))
                            return@flow
                        }
                        is SpecialFreeJobDecision.Accepted -> emitAll(
                            specialFreeJobSelected(
                                jobCode = jobCode,
                                jobName = jobName,
                                forceTreatmentSupport = decision.forceTreatmentSupport,
                                lockedPremiumRate = decision.lockedPremiumRate,
                                hidePremiumSlider = decision.hidePremiumSlider,
                                allowsPayment = decision.allowsPayment,
                            ),
                        )
                    }
                } catch (e: Exception) {
                    emitError(e.message)
                }
            }
            FreelanceSpecialJobCode.MEDICAL_STUDENT_CODE -> {
                try {
                    val status = checkMedicalStudentUseCase().first()
                    when (val decision = resolveMedicalStudentSelection(status)) {
                        is SpecialFreeJobDecision.Rejected -> {
                            emitError(getString(decision.reason.toStringRes()))
                            return@flow
                        }
                        is SpecialFreeJobDecision.Accepted -> emitAll(
                            specialFreeJobSelected(
                                jobCode = jobCode,
                                jobName = jobName,
                                forceTreatmentSupport = decision.forceTreatmentSupport,
                                lockedPremiumRate = decision.lockedPremiumRate,
                                hidePremiumSlider = decision.hidePremiumSlider,
                                allowsPayment = decision.allowsPayment,
                            ),
                        )
                    }
                } catch (e: Exception) {
                    emitError(e.message)
                }
            }
            else -> emitAll(regularFreeJobSelected(jobCode, jobName))
        }
    }

    private fun SpecialFreeJobRejectReason.toStringRes(): StringResource = when (this) {
        SpecialFreeJobRejectReason.RED_CRESCENT_DAY_LIMIT ->
            Res.string.contract_error_red_crescent_day_limit
        SpecialFreeJobRejectReason.RED_CRESCENT_NOT_ELIGIBLE ->
            Res.string.contract_error_red_crescent_not_eligible
        SpecialFreeJobRejectReason.MEDICAL_STUDENT_NOT_ALLOWED ->
            Res.string.contract_error_medical_student_not_allowed
    }

    private fun specialFreeJobSelected(
        jobCode: String,
        jobName: String,
        forceTreatmentSupport: Boolean,
        lockedPremiumRate: String,
        hidePremiumSlider: Boolean,
        allowsPayment: Boolean,
    ): Flow<PartialState> = flow {
        emit(PartialState.FreeJobSelected(jobCode, jobName))
        emit(PartialState.ForceTreatmentSupportChanged(forceTreatmentSupport))
        if (forceTreatmentSupport) {
            emit(PartialState.TreatmentSupportCodeChanged(ContractFlowUiState.TREATMENT_SUPPORT_WITH))
            emit(PartialState.TreatmentCommitmentChanged(true))
        }
        emit(PartialState.HidePremiumSliderChanged(hidePremiumSlider))
        emit(PartialState.LockedPremiumRateChanged(lockedPremiumRate))
        emit(PartialState.PaymentAllowedChanged(allowsPayment))
    }

    private fun regularFreeJobSelected(
        jobCode: String,
        jobName: String,
    ): Flow<PartialState> = flow {
        emit(PartialState.FreeJobSelected(jobCode, jobName))
        emit(PartialState.ForceTreatmentSupportChanged(false))
        emit(PartialState.HidePremiumSliderChanged(false))
        emit(PartialState.LockedPremiumRateChanged(null))
        emit(PartialState.PaymentAllowedChanged(config.allowsOnlinePaymentAfterSubmit))
    }

    private fun loadFreelancePremiumRange(spcRateCodeOverride: String? = null): Flow<PartialState> = flow {
        val params = buildPremiumRangeParams(spcRateCodeOverride) ?: return@flow
        emit(PartialState.PremiumRangeLoading(true))
        emit(PartialState.PremiumCalculated(false))
        try {
            getFreelancePremiumRangeUseCase(params).collect { range ->
                val presentation = range.toPremiumRangePresentation()
                emit(PartialState.PremiumRangeLoaded(presentation))
                emit(PartialState.SelectedMonthlyPremiumChanged(presentation.lowPremium))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.PremiumRangeLoading(false))
        }
    }

    private fun loadOptionalPremiumRange(): Flow<PartialState> = flow {
        emit(PartialState.PremiumRangeLoading(true))
        emit(PartialState.PremiumCalculated(false))
        try {
            getOptionalPremiumRangeUseCase().collect { range ->
                val presentation = range.toPremiumRangePresentation()
                emit(PartialState.PremiumRangeLoaded(presentation))
                emit(PartialState.SelectedMonthlyPremiumChanged(presentation.lowPremium))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.PremiumRangeLoading(false))
        }
    }

    private fun buildPremiumRangeParams(spcRateCodeOverride: String? = null): FreelancePremiumRangeParams? {
        val spcRateCode = spcRateCodeOverride
            ?: uiState.value.selectedPremiumRateCode
            ?: uiState.value.lockedPremiumRateCode
            ?: return null
        val lookupCode = resolveCntFreeJobCode() ?: return null
        if (lookupCode.isBlank() && !config.isOptionalInsurance) return null
        return FreelancePremiumRangeParams(
            treatmentSupportCode = uiState.value.treatmentSupportCode,
            spcRateCode = spcRateCode,
            freeJobCode = lookupCode,
        )
    }

    private fun handleSelectMonthlyPremium(amount: Long): Flow<PartialState> = flow {
        emit(PartialState.SelectedMonthlyPremiumChanged(amount))
    }

    private fun handleCalculateMonthlyPremium(): Flow<PartialState> = flow {
        val flowConfig = uiState.value.config ?: config
        if (flowConfig.isOptionalInsurance) {
            val monthlyPremium = uiState.value.selectedMonthlyPremium ?: return@flow
            emit(PartialState.CalculatingPremium(true))
            try {
                calculateOptionalSalaryUseCase(monthlyPremium.toString()).collect { salary ->
                    emit(PartialState.CalculatedMonthlySalaryLoaded(salary))
                    emit(PartialState.PremiumCalculated(true))
                }
            } catch (e: Exception) {
                emitError(e.message)
            } finally {
                emit(PartialState.CalculatingPremium(false))
            }
            return@flow
        }
        if (!flowConfig.usesFreelancePremiumRange) return@flow
        val params = buildCalculateSalaryParams() ?: return@flow
        emit(PartialState.CalculatingPremium(true))
        try {
            calculateFreelanceSalaryUseCase(params).collect { salary ->
                emit(PartialState.CalculatedMonthlySalaryLoaded(salary))
                emit(PartialState.PremiumCalculated(true))
            }
        } catch (e: Exception) {
            emitError(e.message)
        } finally {
            emit(PartialState.CalculatingPremium(false))
        }
    }

    private fun buildCalculateSalaryParams(): FreelanceCalculateSalaryParams? {
        val monthlyPremium = uiState.value.selectedMonthlyPremium ?: return null
        val spcRateCode = uiState.value.selectedPremiumRateCode
            ?: uiState.value.lockedPremiumRateCode
            ?: return null
        return FreelanceCalculateSalaryParams(
            monthlyPremium = monthlyPremium,
            treatmentSupportCode = uiState.value.treatmentSupportCode,
            spcRateCode = spcRateCode,
        )
    }

    private fun handleSetAgreementConfirmed(confirmed: Boolean): Flow<PartialState> = flow {
        emit(PartialState.AgreementConfirmedChanged(confirmed))
    }

    private fun handleSelectTreatmentSupport(withSupport: Boolean): Flow<PartialState> = flow {
        if (uiState.value.forceTreatmentSupport && !withSupport) return@flow
        val code = if (withSupport) {
            ContractFlowUiState.TREATMENT_SUPPORT_WITH
        } else {
            ContractFlowUiState.TREATMENT_SUPPORT_WITHOUT
        }
        emit(PartialState.TreatmentSupportCodeChanged(code))
        if (withSupport && uiState.value.forceTreatmentSupport) {
            emit(PartialState.TreatmentCommitmentChanged(true))
        }
    }

    private fun handleSetTreatmentCommitment(confirmed: Boolean): Flow<PartialState> = flow {
        emit(PartialState.TreatmentCommitmentChanged(confirmed))
    }

    private fun handleLoadDependents(): Flow<PartialState> = flow {
        emit(PartialState.DependentsLoading(true))
        try {
            subdominantUseCase().collect { result ->
                emit(PartialState.DependentsLoaded(result.toPresentation().list))
            }
        } catch (e: Exception) {
            emit(
                PartialState.DependentsError(
                    e.message ?: getString(Res.string.contract_treatment_dependents_load_error),
                ),
            )
        }
    }

    private fun handleSubmitContract(): Flow<PartialState> = flow {
        val flowConfig = uiState.value.config ?: config
        val params = buildMakeContractParams() ?: return@flow
        emit(PartialState.SubmittingContract(true))
        try {
            makeContractUseCase(flowConfig.isOptionalInsurance, params).collect { result ->
                val presentation = result.toContractResultPresentation()
                emit(PartialState.ContractSubmitted(presentation))
                val amount = uiState.value.calculatedMonthlySalary
                    ?: uiState.value.selectedMonthlyPremium
                    ?: 0L
                sendEvent(
                    ContractFlowEvent.ShowSubmitSuccess(
                        contractNumber = presentation.contractNumber,
                        contractDate = presentation.contractDate,
                        amount = amount,
                        canPayOnline = uiState.value.allowsOnlinePayment,
                    ),
                )
            }
        } catch (e: Exception) {
            sendEvent(
                ContractFlowEvent.ShowSubmitFailure(
                    e.message ?: getString(Res.string.contract_submit_failure_message_fallback),
                ),
            )
        } finally {
            emit(PartialState.SubmittingContract(false))
        }
    }

    private fun buildMakeContractParams(): FreelanceMakeContractParams? {
        val flowConfig = uiState.value.config ?: config
        val selectedSalary = if (flowConfig.isOptionalInsurance) {
            uiState.value.calculatedMonthlySalary
        } else {
            uiState.value.selectedMonthlyPremium
        } ?: return null
        val premiumRateCode = if (flowConfig.isOptionalInsurance) {
            ""
        } else {
            uiState.value.lockedPremiumRateCode
                ?: uiState.value.selectedPremiumRateCode
                ?: return null
        }
        val freeJobCode = resolveCntFreeJobCode() ?: return null
        val branch = uiState.value.branchSelection
        if (!branch.isValid) return null
        val uploaded = uiState.value.uploadedDocuments.firstOrNull()
        return FreelanceMakeContractParams(
            monthlyPremium = selectedSalary,
            request = FreelanceMakeContractRequestDN(
                brchCodeNew = branch.branchCode,
                cityCode = branch.cityCode,
                cntDrmn = uiState.value.treatmentSupportCode,
                cntFreeJobCode = freeJobCode,
                guid = uploaded?.imageId ?: DEFAULT_IMAGE_GUID,
                guidName = uploaded?.fileName ?: DEFAULT_IMAGE_GUID_NAME,
                premiumRateCode = premiumRateCode,
                provinceCode = branch.provinceCode,
            ),
        )
    }

    private fun resolveCntFreeJobCode(): String? {
        val flowConfig = uiState.value.config ?: config
        if (flowConfig.isOptionalInsurance) return ""
        return flowConfig.fixedFreeJobCode
            ?: uiState.value.selectedFreeJobCode?.takeIf { it.isNotBlank() }
    }

    private companion object {
        const val DEFAULT_IMAGE_GUID = "00"
        const val DEFAULT_IMAGE_GUID_NAME = "00"
    }

    override fun reduceState(
        currentState: ContractFlowUiState,
        partialState: PartialState,
    ): ContractFlowUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.RegistrationInfoLoaded -> currentState.copy(
            isLoading = false,
            registrationInfo = partialState.info,
        )
        is PartialState.ContractsLoaded -> currentState.copy(
            isLoading = false,
            existingContracts = partialState.contracts,
        )
        is PartialState.RawTypedContractsLoaded -> currentState.copy(
            rawTypedContracts = partialState.contracts,
            hasLoadedTypedContracts = true,
        )
        is PartialState.AllContractsLoaded -> currentState.copy(
            allContracts = partialState.contracts,
            hasLoadedAllContracts = true,
            allContractsLoadFailed = false,
        )
        PartialState.AllContractsLoadFailed -> currentState.copy(
            hasLoadedAllContracts = true,
            allContractsLoadFailed = true,
        )
        is PartialState.EligibilityLoaded -> currentState.copy(
            eligibility = partialState.eligibility,
        )
        is PartialState.RulesConfirmedChanged -> currentState.copy(
            isRulesConfirmed = partialState.confirmed,
        )
        is PartialState.UserInfoChanged -> currentState.copy(
            userInfo = partialState.userInfo,
        )
        is PartialState.CitiesLoading -> currentState.copy(
            isCitiesLoading = partialState.isLoading,
        )
        is PartialState.CitiesLoaded -> currentState.copy(
            isCitiesLoading = false,
            cities = partialState.cities,
        )
        is PartialState.ProvincesLoading -> currentState.copy(
            isProvincesLoading = partialState.isLoading,
        )
        is PartialState.ProvincesLoaded -> currentState.copy(
            isProvincesLoading = false,
            provinces = partialState.provinces,
        )
        is PartialState.BranchCitiesLoading -> currentState.copy(
            isBranchCitiesLoading = partialState.isLoading,
        )
        is PartialState.BranchCitiesLoaded -> currentState.copy(
            isBranchCitiesLoading = false,
            branchCities = partialState.cities,
        )
        is PartialState.BranchesLoading -> currentState.copy(
            isBranchesLoading = partialState.isLoading,
        )
        is PartialState.BranchesLoaded -> currentState.copy(
            isBranchesLoading = false,
            branches = partialState.branches,
        )
        is PartialState.FreeJobsLoading -> currentState.copy(
            isFreeJobsLoading = partialState.isLoading,
        )
        is PartialState.FreeJobsLoaded -> currentState.copy(
            isFreeJobsLoading = false,
            freeJobs = partialState.freeJobs,
        )
        is PartialState.FreeJobSelected -> currentState.copy(
            selectedFreeJobCode = partialState.jobCode,
            selectedFreeJobName = partialState.jobName,
            premiumRange = null,
            selectedMonthlyPremium = null,
            calculatedMonthlySalary = null,
            isPremiumCalculated = false,
        )
        is PartialState.ContractApplicantTypeChanged -> currentState.copy(
            contractApplicantType = partialState.type,
        )
        is PartialState.GuardianFormChanged -> currentState.copy(
            guardianForm = partialState.form,
        )
        is PartialState.GuardianDocumentUploading -> currentState.copy(
            guardianForm = currentState.guardianForm.copy(
                isUploadingDocument = partialState.isUploading,
                uploadError = null,
            ),
        )
        is PartialState.GuardianDocumentUploaded -> currentState.copy(
            guardianForm = currentState.guardianForm.copy(
                documentGuid = partialState.guid,
                documentName = partialState.name,
                documentPreviewBytes = partialState.bytes,
                isUploadingDocument = false,
                uploadError = null,
            ),
        )
        is PartialState.GuardianDocumentCleared -> currentState.copy(
            guardianForm = currentState.guardianForm.copy(
                documentGuid = null,
                documentName = null,
                documentPreviewBytes = null,
                uploadError = null,
            ),
        )
        is PartialState.BranchSelectionChanged -> currentState.copy(
            branchSelection = partialState.branchSelection,
        )
        is PartialState.PremiumRatesLoading -> currentState.copy(
            isPremiumRatesLoading = partialState.isLoading,
        )
        is PartialState.PremiumRatesLoaded -> currentState.copy(
            isPremiumRatesLoading = false,
            premiumRates = partialState.premiumRates,
        )
        is PartialState.PremiumRateSelected -> currentState.copy(
            selectedPremiumRateCode = partialState.code,
            premiumRange = null,
            selectedMonthlyPremium = null,
            calculatedMonthlySalary = null,
            isPremiumCalculated = false,
            isPremiumRangeLoading = false,
        )
        is PartialState.PremiumRangeLoading -> currentState.copy(
            isPremiumRangeLoading = partialState.isLoading,
        )
        is PartialState.PremiumRangeLoaded -> currentState.copy(
            isPremiumRangeLoading = false,
            premiumRange = partialState.premiumRange,
        )
        is PartialState.SelectedMonthlyPremiumChanged -> currentState.copy(
            selectedMonthlyPremium = partialState.amount,
            calculatedMonthlySalary = null,
            isPremiumCalculated = false,
        )
        is PartialState.CalculatingPremium -> currentState.copy(
            isCalculatingPremium = partialState.isCalculating,
        )
        is PartialState.PremiumCalculated -> currentState.copy(
            isPremiumCalculated = partialState.calculated,
        )
        is PartialState.CalculatedMonthlySalaryLoaded -> currentState.copy(
            calculatedMonthlySalary = partialState.salary,
        )
        is PartialState.AgreementConfirmedChanged -> currentState.copy(
            isAgreementConfirmed = partialState.confirmed,
        )
        is PartialState.SavingContact -> currentState.copy(
            isSavingContact = partialState.isSaving,
        )
        PartialState.ContactSaved -> currentState
        is PartialState.SubmittingContract -> currentState.copy(
            isSubmittingContract = partialState.isSubmitting,
        )
        is PartialState.ContractSubmitted -> currentState.copy(
            submittedContract = partialState.result,
        )
        is PartialState.DocumentDescriptionChanged -> currentState.copy(
            documentDescription = partialState.description,
        )
        is PartialState.DocumentPreviewSet -> currentState.copy(
            documentPreviewBytes = partialState.bytes,
        )
        is PartialState.UploadingDocument -> currentState.copy(
            isUploadingDocument = partialState.isUploading,
        )
        is PartialState.UploadDocumentError -> currentState.copy(
            uploadDocumentError = partialState.message,
        )
        is PartialState.DocumentUploaded -> currentState.copy(
            uploadedDocuments = listOf(partialState.document),
            uploadDocumentError = null,
        )
        PartialState.UploadedDocumentCleared -> currentState.copy(
            documentPreviewBytes = null,
            uploadedDocuments = emptyList(),
            uploadDocumentError = null,
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
            isEditMode = partialState.isEditMode,
        )
        is PartialState.ForceTreatmentSupportChanged -> currentState.copy(
            forceTreatmentSupport = partialState.forced,
            treatmentSupportCode = if (partialState.forced) {
                ContractFlowUiState.TREATMENT_SUPPORT_WITH
            } else {
                currentState.treatmentSupportCode
            },
            isTreatmentCommitmentConfirmed = if (partialState.forced) {
                true
            } else {
                currentState.isTreatmentCommitmentConfirmed
            },
        )
        is PartialState.HidePremiumSliderChanged -> currentState.copy(
            hidePremiumSlider = partialState.hidden,
        )
        is PartialState.LockedPremiumRateChanged -> currentState.copy(
            lockedPremiumRateCode = partialState.code,
        )
        is PartialState.GenderGateError -> currentState.copy(
            genderGateError = partialState.message,
        )
        is PartialState.PreflightGateError -> currentState.copy(
            preflightGateError = partialState.message,
        )
        is PartialState.PaymentAllowedChanged -> currentState.copy(
            allowsOnlinePayment = partialState.allowed,
        )
        is PartialState.TreatmentSupportCodeChanged -> currentState.copy(
            treatmentSupportCode = partialState.code,
        )
        is PartialState.TreatmentCommitmentChanged -> currentState.copy(
            isTreatmentCommitmentConfirmed = partialState.confirmed,
        )
        is PartialState.DependentsLoading -> currentState.copy(
            isDependentsLoading = partialState.isLoading,
            dependentsError = if (partialState.isLoading) null else currentState.dependentsError,
        )
        is PartialState.DependentsLoaded -> currentState.copy(
            isDependentsLoading = false,
            hasLoadedDependents = true,
            dependents = partialState.dependents,
            dependentsError = null,
        )
        is PartialState.DependentsError -> currentState.copy(
            isDependentsLoading = false,
            hasLoadedDependents = true,
            dependentsError = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

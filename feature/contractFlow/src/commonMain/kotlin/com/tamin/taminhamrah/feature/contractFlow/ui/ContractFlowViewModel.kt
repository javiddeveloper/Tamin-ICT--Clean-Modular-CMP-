package com.tamin.taminhamrah.feature.contractFlow.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contractFlow.config.ContractFlowConfig
import com.tamin.taminhamrah.feature.contractFlow.config.FreelanceSpecialJobs
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowEvent
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowIntent
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowUiState
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowUiState.PartialState
import com.tamin.taminhamrah.feature.contractFlow.ui.mapper.toPresentation as toContractResultPresentation
import com.tamin.taminhamrah.feature.contractFlow.ui.mapper.toPresentation as toPremiumRangePresentation
import com.tamin.taminhamrah.feature.contractFlow.ui.mapper.toSpcPremiumRateOptions
import com.tamin.taminhamrah.feature.contractFlow.ui.mapper.resolveEligibility
import com.tamin.taminhamrah.mapper.common.filterByProvinceCode
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.mapper.contracts.toBranchPresentation
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contractFlow.ContractStep
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

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
            is ContractFlowIntent.SetRulesConfirmed -> handleSetRulesConfirmed(intent.confirmed)
            is ContractFlowIntent.UpdateUserInfo -> handleUpdateUserInfo(intent.userInfo)
            is ContractFlowIntent.SetContractApplicantType -> handleSetContractApplicantType(intent.type)
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
            ContractFlowIntent.SubmitContract -> handleSubmitContract()
        }
    }

    private fun handleLoadInitialData(): Flow<PartialState> = merge(
        flow { emit(PartialState.ConfigLoaded(config)) },
        loadRegistrationInfo(),
        loadContracts(),
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
            emit(PartialState.Error(e.message))
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
                if (config.requiresFemaleGender && presentation.genderTitle == "آقای") {
                    emit(PartialState.GenderGateError("این خدمت مختص بانوان است."))
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun loadContracts(): Flow<PartialState> = flow {
        try {
            getContractsUseCase.contractsByPremiumType(config.premiumTypeCode).collect { contracts ->
                emit(PartialState.EligibilityLoaded(contracts.resolveEligibility()))
                emit(PartialState.ContractsLoaded(contracts.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun loadCities(): Flow<PartialState> = flow {
        emit(PartialState.CitiesLoading(true))
        try {
            identityInfoUseCase.getCities().collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
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
            emit(PartialState.Error(e.message))
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
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.PremiumRatesLoading(false))
        }
    }

    private fun handleSelectBranchProvince(province: ProvincePR): Flow<PartialState> = flow {
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
        emit(
            PartialState.BranchCitiesLoaded(
                uiState.value.cities.filterByProvinceCode(province.provinceCode),
            ),
        )
        emit(PartialState.BranchesLoaded(emptyList()))
    }

    private fun loadBranches(cityCode: String): Flow<PartialState> = flow {
        emit(PartialState.BranchesLoading(true))
        try {
            getBranchesUseCase(cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toBranchPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
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
            return saveContactThenAdvance(nextStep)
        }

        return merge(
            flow { emit(PartialState.StepChanged(nextStep)) },
            when {
                nextStep == ContractStep.STEP_SALARY && flowConfig.usesFreelancePremiumRange ->
                    loadFreelancePremiumRange()
                nextStep == ContractStep.STEP_SALARY && flowConfig.isOptionalInsurance ->
                    loadOptionalPremiumRange()
                else -> flow { }
            },
        )
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
            emit(PartialState.Error(e.message))
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

    private fun handleGoToPreviousStep(): Flow<PartialState> = flow {
        val flowConfig = uiState.value.config ?: config
        val previousStep = flowConfig.previousStep(uiState.value.currentStep) ?: return@flow
        emit(PartialState.StepChanged(previousStep))
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
            emit(PartialState.UploadDocumentError("فقط تصاویر با فرمت JPEG مجاز هستند."))
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
            emit(PartialState.UploadDocumentError(e.message ?: "خطا در بارگذاری تصویر"))
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
        emit(PartialState.PremiumRateSelected(rate.code))
    }

    private fun handleSelectFreeJob(job: FreeJobDN): Flow<PartialState> = flow {
        val jobCode = job.jobCode.orEmpty()
        val jobName = job.discrioption.orEmpty()

        when (jobCode) {
            FreelanceSpecialJobs.RED_CRESCENT_CODE -> {
                try {
                    val day = checkRedCrossStatusUseCase().first().toIntOrNull()
                    if (day == null || day > 20) {
                        emit(PartialState.Error("امکان انتخاب این شغل تنها تا روز ۲۰ هر ماه وجود دارد."))
                        return@flow
                    }
                    emitAll(
                        specialFreeJobSelected(
                            jobCode = jobCode,
                            jobName = jobName,
                            forceTreatmentSupport = true,
                            lockedPremiumRate = FreelanceSpecialJobs.RED_CRESCENT_PREMIUM_RATE,
                            hidePremiumSlider = true,
                            allowsPayment = false,
                        ),
                    )
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            FreelanceSpecialJobs.MEDICAL_STUDENT_CODE -> {
                try {
                    val status = checkMedicalStudentUseCase().first()
                    if (status != FreelanceSpecialJobs.MEDICAL_STUDENT_OK_STATUS) {
                        emit(PartialState.Error("امکان انتخاب این شغل برای شما وجود ندارد."))
                        return@flow
                    }
                    emitAll(
                        specialFreeJobSelected(
                            jobCode = jobCode,
                            jobName = jobName,
                            forceTreatmentSupport = false,
                            lockedPremiumRate = FreelanceSpecialJobs.MEDICAL_STUDENT_PREMIUM_RATE,
                            hidePremiumSlider = true,
                            allowsPayment = false,
                        ),
                    )
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            else -> emitAll(regularFreeJobSelected(jobCode, jobName))
        }
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

    private fun loadFreelancePremiumRange(): Flow<PartialState> = flow {
        val params = buildPremiumRangeParams() ?: return@flow
        emit(PartialState.PremiumRangeLoading(true))
        emit(PartialState.PremiumCalculated(false))
        try {
            getFreelancePremiumRangeUseCase(params).collect { range ->
                val presentation = range.toPremiumRangePresentation()
                emit(PartialState.PremiumRangeLoaded(presentation))
                emit(PartialState.SelectedMonthlyPremiumChanged(presentation.lowPremium))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
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
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.PremiumRangeLoading(false))
        }
    }

    private fun buildPremiumRangeParams(): FreelancePremiumRangeParams? {
        val spcRateCode = uiState.value.selectedPremiumRateCode
            ?: uiState.value.lockedPremiumRateCode
            ?: return null
        val lookupCode = when {
            config.requiresFreeJob -> uiState.value.selectedFreeJobCode.orEmpty()
            else -> uiState.value.registrationInfo?.insuranceId.orEmpty()
        }
        if (lookupCode.isBlank()) return null
        return FreelancePremiumRangeParams(
            treatmentSupportCode = DEFAULT_TREATMENT_SUPPORT_CODE,
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
                emit(PartialState.Error(e.message))
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
            emit(PartialState.Error(e.message))
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
            treatmentSupportCode = DEFAULT_TREATMENT_SUPPORT_CODE,
            spcRateCode = spcRateCode,
        )
    }

    private fun handleSetAgreementConfirmed(confirmed: Boolean): Flow<PartialState> = flow {
        emit(PartialState.AgreementConfirmedChanged(confirmed))
    }

    private fun handleSubmitContract(): Flow<PartialState> = flow {
        val flowConfig = uiState.value.config ?: config
        val params = buildMakeContractParams() ?: return@flow
        emit(PartialState.SubmittingContract(true))
        try {
            makeContractUseCase(flowConfig.isOptionalInsurance, params).collect { result ->
                val presentation = result.toContractResultPresentation()
                emit(PartialState.ContractSubmitted(presentation))
                if (uiState.value.allowsOnlinePayment) {
                    val amount = uiState.value.calculatedMonthlySalary
                        ?: uiState.value.selectedMonthlyPremium
                        ?: 0L
                    sendEvent(
                        ContractFlowEvent.ShowPaymentOption(
                            contractNumber = presentation.contractNumber,
                            amount = amount,
                        ),
                    )
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
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
        return FreelanceMakeContractParams(
            monthlyPremium = selectedSalary,
            request = FreelanceMakeContractRequestDN(
                brchCodeNew = branch.branchCode,
                cityCode = branch.cityCode,
                cntDrmn = DEFAULT_TREATMENT_SUPPORT_CODE,
                cntFreeJobCode = freeJobCode,
                guid = DEFAULT_APPLICANT_GUID,
                guidName = DEFAULT_APPLICANT_GUID_NAME,
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
        /**
         * Step 8 (treatment support) is not implemented yet; legacy app uses cntDrmn "1".
         */
        const val DEFAULT_TREATMENT_SUPPORT_CODE = "1"
        const val DEFAULT_APPLICANT_GUID = "00"
        const val DEFAULT_APPLICANT_GUID_NAME = "00"
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
        is PartialState.CitiesLoaded -> {
            val cities = partialState.cities
            currentState.copy(
                isCitiesLoading = false,
                cities = cities,
                branchCities = if (currentState.branchSelection.provinceCode.isNotBlank()) {
                    cities.filterByProvinceCode(currentState.branchSelection.provinceCode)
                } else {
                    currentState.branchCities
                },
            )
        }
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
        is PartialState.ConfigLoaded -> currentState.copy(
            config = partialState.config,
            allowsOnlinePayment = partialState.config.allowsOnlinePaymentAfterSubmit,
            currentStep = partialState.config.steps.first(),
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
        )
        is PartialState.ForceTreatmentSupportChanged -> currentState.copy(
            forceTreatmentSupport = partialState.forced,
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
        is PartialState.PaymentAllowedChanged -> currentState.copy(
            allowsOnlinePayment = partialState.allowed,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

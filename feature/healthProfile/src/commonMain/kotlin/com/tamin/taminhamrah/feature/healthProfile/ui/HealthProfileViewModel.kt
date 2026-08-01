package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.mapper.*
import com.tamin.taminhamrah.feature.healthProfile.ui.model.*
import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.health.*
import com.tamin.taminhamrah.util.Logger
import kotlinx.coroutines.flow.*

class HealthProfileViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val getPatientGeneralUseCase: GetPatientGeneralUseCase,
    private val getPatientSelfDeclarativeUseCase: GetPatientSelfDeclarativeUseCase,
    private val getPatientDrugAllergiesUseCase: GetPatientDrugAllergiesUseCase,
    private val updatePatientUseCase: UpdatePatientUseCase,
    private val syncIllnessSelfDeclarativesUseCase: SyncIllnessSelfDeclarativesUseCase,
    private val syncDrugAllergiesUseCase: SyncDrugAllergiesUseCase,
    private val addSelfDeclarativeUseCase: AddSelfDeclarativeUseCase,
    private val updateSelfDeclarativeUseCase: UpdateSelfDeclarativeUseCase,
    private val getAllProvincesUseCase: GetAllProvincesUseCase,
    private val getProvinceCitiesUseCase: GetProvinceCitiesUseCase,
    private val getBloodGroupsUseCase: GetBloodGroupsUseCase,
    private val getMaritalStatusUseCase: GetMaritalStatusUseCase,
    private val getSmokingStatusUseCase: GetSmokingStatusUseCase,
    private val getActFrequenciesUseCase: GetActFrequenciesUseCase,
    private val getSelfDeclarableIllnessesByGroupUseCase: GetSelfDeclarableIllnessesByGroupUseCase,
    private val getAllDrugsUseCase: GetAllDrugsUseCase
) : BaseViewModel<HealthProfileUiState, PartialState, HealthProfileEvent, HealthProfileIntent>(
    initialState = HealthProfileUiState()
) {

    private var currentPatientNatCode: String = ""
    private var currentPatientId: Int = 0

    override fun handleIntent(intent: HealthProfileIntent): Flow<PartialState> {
        return when (intent) {
            is HealthProfileIntent.LoadHealthProfile -> handleLoadHealthProfile(intent.nationalCode)
            is HealthProfileIntent.RetryStep -> handleRefreshStep()
            is HealthProfileIntent.LoadCitiesForProvince -> handleLoadCities(intent.provinceId)

            is HealthProfileIntent.ChangeStep -> flow {
                if (intent.step == SelfDeclarationStep.SUCCESS) {
                    emit(PartialState.Loading(true))
                    val success = submitFullDeclaration()
                    emit(PartialState.Loading(false))
                    if (success) emit(
                        PartialState.StepChanged(
                            SelfDeclarationStep.SUCCESS,
                            intent.isEditMode
                        )
                    )
                } else {
                    emit(PartialState.StepChanged(intent.step, intent.isEditMode))
                }
            }

            is HealthProfileIntent.SubmitDeclaration -> flow {
                emit(PartialState.Loading(true))
                val success = submitFullDeclaration()
                emit(PartialState.Loading(false))
                if (success) emit(PartialState.StepChanged(SelfDeclarationStep.SUCCESS))
            }

            is HealthProfileIntent.UpdateIdentity -> flow { emit(PartialState.IdentityUpdated(intent.identity)) }
            is HealthProfileIntent.UpdatePersonal -> flow { emit(PartialState.PersonalUpdated(intent.personal)) }
            is HealthProfileIntent.UpdateContact -> flow { emit(PartialState.ContactUpdated(intent.contact)) }
            is HealthProfileIntent.UpdateEmergency -> flow {
                emit(
                    PartialState.EmergencyUpdated(
                        intent.emergency
                    )
                )
            }

            is HealthProfileIntent.UpdatePhysical -> flow { emit(PartialState.PhysicalUpdated(intent.physical)) }
            is HealthProfileIntent.UpdateDiseases -> flow {
                val illnessList = mutableListOf<IllnessSelfDeclareRequest>()
                intent.diseases.riskFactorIds.forEach {
                    illnessList.add(
                        IllnessSelfDeclareRequest(
                            it,
                            0,
                            null
                        )
                    )
                }
                intent.diseases.chronicDiseaseIds.forEach {
                    illnessList.add(
                        IllnessSelfDeclareRequest(it, 0, null)
                    )
                }
                intent.diseases.mentalIllnessIds.forEach {
                    illnessList.add(
                        IllnessSelfDeclareRequest(
                            it,
                            0,
                            null
                        )
                    )
                }
                intent.diseases.cancerIds.forEach {
                    illnessList.add(
                        IllnessSelfDeclareRequest(
                            it,
                            0,
                            null
                        )
                    )
                }

                Logger.d("DiseasesUpdate", "User updated diseases section")
                Logger.d("DiseasesUpdate", "New Request Payload Preview: $illnessList")

                emit(PartialState.DiseasesUpdated(intent.diseases))
            }

            is HealthProfileIntent.OpenDiseaseBottomSheet -> flow {
                val currentDiseases = uiState.value.selfDeclaration.diseases
                emit(PartialState.DiseasesUpdated(currentDiseases.copy(activeBottomSheet = intent.type)))
            }

            is HealthProfileIntent.CloseDiseaseBottomSheet -> flow {
                val currentDiseases = uiState.value.selfDeclaration.diseases
                val type = currentDiseases.activeBottomSheet
                val reverted = when (type) {
                    BottomSheetType.ILLNESS_HISTORY ->
                        if (currentDiseases.chronicDiseaseIds.isEmpty()) currentDiseases.copy(
                            hasChronicDisease = false
                        )
                        else currentDiseases

                    BottomSheetType.MENTAL ->
                        if (currentDiseases.mentalIllnessIds.isEmpty()) currentDiseases.copy(
                            hasMentalIllness = false
                        )
                        else currentDiseases

                    BottomSheetType.CANCER ->
                        if (currentDiseases.cancerIds.isEmpty()) currentDiseases.copy(hasCancer = false)
                        else currentDiseases

                    else -> currentDiseases
                }
                emit(PartialState.DiseasesUpdated(reverted.copy(activeBottomSheet = null)))
            }

            is HealthProfileIntent.SetDiseaseAnswer -> flow {
                val currentDiseases = uiState.value.selfDeclaration.diseases
                val updatedDiseases = when (intent.type) {
                    BottomSheetType.ILLNESS_HISTORY -> currentDiseases.copy(
                        hasChronicDisease = intent.isYes,
                        chronicDiseaseIds = if (!intent.isYes) emptySet() else currentDiseases.chronicDiseaseIds,
                        activeBottomSheet = if (intent.isYes) BottomSheetType.ILLNESS_HISTORY else null
                    )

                    BottomSheetType.MENTAL -> currentDiseases.copy(
                        hasMentalIllness = intent.isYes,
                        mentalIllnessIds = if (!intent.isYes) emptySet() else currentDiseases.mentalIllnessIds,
                        activeBottomSheet = if (intent.isYes) BottomSheetType.MENTAL else null
                    )

                    BottomSheetType.CANCER -> currentDiseases.copy(
                        hasCancer = intent.isYes,
                        cancerIds = if (!intent.isYes) emptySet() else currentDiseases.cancerIds,
                        activeBottomSheet = if (intent.isYes) BottomSheetType.CANCER else null
                    )

                    else -> currentDiseases
                }
                emit(PartialState.DiseasesUpdated(updatedDiseases))
            }

            is HealthProfileIntent.UpdateDiseaseSelections -> flow {
                val currentDiseases = uiState.value.selfDeclaration.diseases
                val updatedDiseases = when (intent.type) {
                    BottomSheetType.ILLNESS_HISTORY -> currentDiseases.copy(
                        chronicDiseaseIds = intent.selectedIds,
                        hasChronicDisease = intent.selectedIds.isNotEmpty()
                    )

                    BottomSheetType.MENTAL -> currentDiseases.copy(
                        mentalIllnessIds = intent.selectedIds,
                        hasMentalIllness = intent.selectedIds.isNotEmpty()
                    )

                    BottomSheetType.CANCER -> currentDiseases.copy(
                        cancerIds = intent.selectedIds,
                        hasCancer = intent.selectedIds.isNotEmpty()
                    )

                    BottomSheetType.RISK_FACTOR -> currentDiseases.copy(riskFactorIds = intent.selectedIds)
                    else -> currentDiseases
                }
                emit(PartialState.DiseasesUpdated(updatedDiseases))
            }

            is HealthProfileIntent.UpdateFamily -> flow { emit(PartialState.FamilyUpdated(intent.family)) }
            is HealthProfileIntent.UpdateBloodGroup -> flow {
                Logger.d("BloodGroupUpdate", "User updated blood group")
                Logger.d(
                    "BloodGroupUpdate",
                    "selectedBloodGroupId: ${intent.bloodGroup.selectedBloodGroupId}"
                )
                Logger.d(
                    "BloodGroupUpdate",
                    "selectedBloodGroupLetter: ${intent.bloodGroup.selectedBloodGroupLetter}"
                )
                Logger.d(
                    "BloodGroupUpdate",
                    "selectedBloodGroupRh: ${intent.bloodGroup.selectedBloodGroupRh}"
                )
                Logger.d(
                    "BloodGroupUpdate",
                    "isBloodGroupUnknown: ${intent.bloodGroup.isBloodGroupUnknown}"
                )
                emit(PartialState.BloodGroupUpdated(intent.bloodGroup))
            }

            is HealthProfileIntent.UpdateLifestyle -> flow {
                Logger.d("LifestyleUpdate", "User updated lifestyle section")
                Logger.d(
                    "LifestyleUpdate", """
                    isSmoking: ${intent.lifestyle.isSmoking}, smokingStatusId: ${intent.lifestyle.smokingStatusId}, smokingPattern: ${intent.lifestyle.smokingPattern}
                    hasAddiction: ${intent.lifestyle.hasAddiction}, substanceStatusId: ${intent.lifestyle.substanceStatusId}, substancePattern: ${intent.lifestyle.substancePattern}
                    isDrinking: ${intent.lifestyle.isDrinking}, drinkingStatusId: ${intent.lifestyle.drinkingStatusId}, drinkingPattern: ${intent.lifestyle.drinkingPattern}
                    isExercising: ${intent.lifestyle.isExercising}, exerciseStatusId: ${intent.lifestyle.exerciseStatusId}, exerciseFrequency: ${intent.lifestyle.exerciseFrequency}
                """.trimIndent()
                )
                emit(PartialState.LifestyleUpdated(intent.lifestyle))
            }

            is HealthProfileIntent.UpdateAllergy -> flow { emit(PartialState.AllergyUpdated(intent.allergy)) }
        }
    }

    private fun handleLoadHealthProfile(nationalCode: String?): Flow<PartialState> = merge(
        fetchLookupLists(),
        fetchPatientData(nationalCode)
    ).onStart {
        emit(PartialState.ClearAllErrors)
        emit(PartialState.Loading(true))
    }
        .onCompletion { emit(PartialState.Loading(false)) }

    private fun handleRefreshStep(): Flow<PartialState> = flow {
        val step = uiState.value.selfDeclaration.currentStep
        val natCode = currentPatientNatCode.takeIf { it.isNotBlank() }
            ?: tokenStoreManager.getUserId() ?: ""
        val patientId = currentPatientId

        emit(PartialState.Loading(true))
        when (step) {
            SelfDeclarationStep.COMPLETED -> {
                fetchPatientData(natCode).collect { emit(it) }
            }

            SelfDeclarationStep.CONTACT -> {
                merge(
                    fetchProvinces(),
                    if (uiState.value.selfDeclaration.contact.provinceId != null)
                        handleLoadCities(uiState.value.selfDeclaration.contact.provinceId!!)
                    else emptyFlow()
                ).collect { emit(it) }
            }

            SelfDeclarationStep.PERSONAL -> fetchMaritalStatus().collect { emit(it) }
            SelfDeclarationStep.BLOOD -> fetchBloodGroups().collect { emit(it) }
            SelfDeclarationStep.LIFESTYLE -> {
                merge(fetchSmokingStatus(), fetchActFrequencies()).collect { emit(it) }
            }

            SelfDeclarationStep.DISEASES, SelfDeclarationStep.FAMILY -> {
                fetchIllnessGroups().collect { emit(it) }
            }

            SelfDeclarationStep.ALLERGY -> fetchDrugs().collect { emit(it) }
            else -> {
                handleLoadHealthProfile(natCode).collect { emit(it) }
            }
        }
        emit(PartialState.Loading(false))
    }

    private fun fetchLookupLists(): Flow<PartialState> = merge(
        fetchMaritalStatus(),
        fetchProvinces(),
        fetchBloodGroups(),
        fetchSmokingStatus(),
        fetchActFrequencies(),
        fetchIllnessGroups(),
        fetchDrugs()
    )

    private fun fetchMaritalStatus() = flow {
        getMaritalStatusUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت وضعیت تاهل",
                        ErrorSource.MARITAL_STATUS
                    )
                )
            }
            .collect {
                emit(PartialState.MaritalStatusLoaded(it.map { s -> s.toPresentation() }))
            }
    }

    private fun fetchProvinces() = flow {
        emit(PartialState.ProvincesLoading(true))
        getAllProvincesUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت لیست استان‌ها",
                        ErrorSource.PROVINCES
                    )
                )
                emit(PartialState.ProvincesLoading(true))
            }
            .collect {
                emit(PartialState.ProvincesLoaded(it.map { p -> p.toPresentation() }))
                emit(PartialState.ProvincesLoading(false))
            }
    }

    private fun fetchBloodGroups() = flow {
        getBloodGroupsUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت گروه‌های خونی",
                        ErrorSource.BLOOD_GROUPS
                    )
                )
            }
            .collect { emit(PartialState.BloodGroupsLoaded(it.map { b -> b.toPresentation() })) }
    }

    private fun fetchSmokingStatus() = flow {
        getSmokingStatusUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت وضعیت دخانیات",
                        ErrorSource.SMOKING_STATUS
                    )
                )
            }
            .collect { emit(PartialState.SmokingStatusLoaded(it.map { s -> s.toPresentation() })) }
    }

    private fun fetchActFrequencies() = flow {
        getActFrequenciesUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت فرکانس فعالیت‌ها",
                        ErrorSource.ACT_FREQUENCIES
                    )
                )
            }
            .collect { emit(PartialState.ActFrequenciesLoaded(it.map { s -> s.toPresentation() })) }
    }

    private fun fetchIllnessGroups() = flow {
        getSelfDeclarableIllnessesByGroupUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت گروه‌های بیماری",
                        ErrorSource.ILLNESS_GROUPS
                    )
                )
            }
            .collect { emit(PartialState.IllnessGroupsLoaded(it.map { g -> g.toPresentation() })) }
    }

    private fun fetchDrugs() = flow {
        getAllDrugsUseCase()
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت لیست داروها",
                        ErrorSource.DRUGS
                    )
                )
            }
            .collect { emit(PartialState.DrugsLoaded(it.map { d -> d.toPresentation() })) }
    }

    private fun fetchPatientData(nationalCode: String?): Flow<PartialState> = flow {
        val targetNatCode = nationalCode?.takeIf { it.isNotBlank() }
            ?: currentPatientNatCode.takeIf { it.isNotBlank() }
            ?: tokenStoreManager.getUserId()
            ?: ""

        if (targetNatCode.isNotBlank()) {
            getPatientGeneralUseCase(targetNatCode)
                .catch {
                    emit(
                        PartialState.Error(
                            it.message ?: "خطا در دریافت اطلاعات عمومی",
                            ErrorSource.PATIENT_GENERAL
                        )
                    )
                }
                .collect { general ->
                    val safeNatCode =
                        general.patientNatCode?.takeIf { it.isNotBlank() } ?: targetNatCode
                    val safePatientId = general.ptientID ?: 0

                    currentPatientNatCode = safeNatCode
                    currentPatientId = safePatientId

                    emit(PartialState.GeneralLoaded(general.toPresentation()))

                    if (safeNatCode.isNotBlank() && safePatientId != 0) {
                        fetchAdditionalPatientInfo(safeNatCode, safePatientId).collect { emit(it) }
                    }
                }
        }
    }

    private fun fetchAdditionalPatientInfo(natCode: String, patientId: Int): Flow<PartialState> =
        merge(
            flow {
                getPatientSelfDeclarativeUseCase(natCode, patientId)
                    .catch {
                        emit(
                            PartialState.Error(
                                it.message ?: "خطا در دریافت اطلاعات خوداظهاری",
                                ErrorSource.PATIENT_LIFESTYLE
                            )
                        )
                    }
                    .firstOrNull()
                    ?.let { emit(PartialState.LifestyleLoaded(it.toPresentation())) }
            },
            flow {
                getPatientDrugAllergiesUseCase(natCode, patientId)
                    .catch {
                        emit(
                            PartialState.Error(
                                it.message ?: "خطا در دریافت حساسیت‌های دارویی",
                                ErrorSource.PATIENT_ALLERGIES
                            )
                        )
                    }
                    .firstOrNull()
                    ?.let { list -> emit(PartialState.AllergiesLoaded(list.map { d -> d.toPresentation() })) }
            }
        )

    private fun handleLoadCities(provinceId: Int): Flow<PartialState> = flow {
        emit(PartialState.CitiesLoading(true))
        getProvinceCitiesUseCase(provinceId)
            .catch {
                emit(
                    PartialState.Error(
                        it.message ?: "خطا در دریافت لیست شهرها",
                        ErrorSource.CITIES
                    )
                )
                emit(PartialState.CitiesLoading(false))
            }
            .collect {
                emit(PartialState.CitiesLoaded(it.map { c -> c.toPresentation() }))
                emit(PartialState.CitiesLoading(false))
            }
    }

    private suspend fun submitFullDeclaration(): Boolean {
        if (currentPatientId == 0 || currentPatientNatCode.isBlank()) {
            sendEvent(
                HealthProfileEvent.ShowToast(
                    "اطلاعات شناسایی بیمار یا کد ملی یافت نشد.",
                    isError = true
                )
            )
            return false
        }

        return try {
            val selfDecState = uiState.value.selfDeclaration

            // 1. Update Patient General Info
            val updatePatientReq = UpdatePatientRequest(
                patientID = currentPatientId,
                patientNatCode = currentPatientNatCode,
                patientMobile = selfDecState.contact.mobile,
                patientEmail = selfDecState.contact.email,
                patientAddress = selfDecState.contact.address,
                patientArea = selfDecState.contact.postcode, // Assuming postcode or some neighborhood equivalent
                patientCityID = selfDecState.contact.cityId,
                patientBloodGroup = selfDecState.bloodGroup.selectedBloodGroupId,
                patientMarriage = selfDecState.personal.maritalStatusId,
                patientJob = selfDecState.personal.job,
                patientHeight = selfDecState.physical.height,
                patientWeight = selfDecState.physical.weight,
                patientCitizenship = selfDecState.personal.citizenship,
                patientNationality = selfDecState.personal.nationality,
                patientInsurance = null,
                emergencyName = selfDecState.emergency.emergencyName,
                emergencyFamily = selfDecState.emergency.emergencyFamily,
                emergencyMobile = selfDecState.emergency.emergencyMobile,
                emergencyEmail = null,
                emergencyRelation = 1, // Specific logic from old code
                emergencyAddress = null,
                emergencyArea = null,
                emergencyCityID = null
            )
            Logger.d("HealthProfile", "Updating Patient: $updatePatientReq")
            updatePatientUseCase(updatePatientReq)

            // 2. Sync Illnesses (Diseases + Family)
            val illnessList = mutableListOf<IllnessSelfDeclareRequest>()
            selfDecState.diseases.riskFactorIds.forEach {
                illnessList.add(
                    IllnessSelfDeclareRequest(
                        it,
                        0,
                        null
                    )
                )
            }
            selfDecState.diseases.chronicDiseaseIds.forEach {
                illnessList.add(
                    IllnessSelfDeclareRequest(it, 0, null)
                )
            }
            selfDecState.diseases.mentalIllnessIds.forEach {
                illnessList.add(
                    IllnessSelfDeclareRequest(it, 0, null)
                )
            }
            selfDecState.diseases.cancerIds.forEach {
                illnessList.add(
                    IllnessSelfDeclareRequest(
                        it,
                        0,
                        null
                    )
                )
            }
            selfDecState.family.familyDiseaseIds.forEach {
                illnessList.add(
                    IllnessSelfDeclareRequest(
                        it,
                        1,
                        null
                    )
                )
            }
            selfDecState.family.familyCancerIds.forEach {
                illnessList.add(
                    IllnessSelfDeclareRequest(
                        it,
                        1,
                        null
                    )
                )
            }

            if (illnessList.isNotEmpty()) {
                val syncIllnessReq = SyncIllnessSelfDeclarativesRequest(
                    natCode = currentPatientNatCode,
                    patientID = currentPatientId,
                    illnessSelfDeclareList = illnessList
                )
                Logger.d("HealthProfile", "Syncing Illnesses: $syncIllnessReq")
                syncIllnessSelfDeclarativesUseCase(syncIllnessReq)
            }

            // 3. Sync Drug Allergies
            val allergyList = selfDecState.allergy.allergies.map {
                DrugAllergyRequest(drugId = it.drugId, allergyComments = it.allergyComments)
            }
            if (allergyList.isNotEmpty()) {
                val syncAllergyReq = SyncDrugAllergiesRequest(
                    natCode = currentPatientNatCode,
                    patientID = currentPatientId,
                    drugAllergyList = allergyList
                )
                Logger.d("HealthProfile", "Syncing Drug Allergies: $syncAllergyReq")
                syncDrugAllergiesUseCase(syncAllergyReq)
            }

            // 4. Add or Update Self Declarative (Lifestyle)
            val hasUserDeclared = uiState.value.lifestyleInfo != null
            val lifestyle = selfDecState.lifestyle

            if (hasUserDeclared) {
                val updateLifestyleReq = UpdateSelfDeclarativeRequest(
                    patientID = currentPatientId,
                    objectID = uiState.value.lifestyleInfo?.objectId,
                    smoking = if (lifestyle.isSmoking == true) lifestyle.smokingStatusId else 0,
                    smokeDesc = lifestyle.smokingPattern,
                    alcoholUse = if (lifestyle.isDrinking == true) lifestyle.drinkingStatusId else LifeStyleStatus.NEVER.id,
                    alcoholUseDesc = lifestyle.drinkingPattern,
                    substanceUse = if (lifestyle.hasAddiction == true) lifestyle.substanceStatusId else LifeStyleStatus.NEVER.id,
                    substanceUseDesc = lifestyle.substancePattern,
                    exerciseFrequency = if (lifestyle.isExercising == true) lifestyle.exerciseStatusId else LifeStyleStatus.NEVER.id,
                    exerciseDesc = lifestyle.exerciseFrequency
                )
                Logger.d("HealthProfile", "Updating Lifestyle: $updateLifestyleReq")
                updateSelfDeclarativeUseCase(updateLifestyleReq)
            } else {
                val addLifestyleReq = AddSelfDeclarativeRequest(
                    natCode = currentPatientNatCode,
                    patientID = currentPatientId,
                    smoking = if (lifestyle.isSmoking == true) lifestyle.smokingStatusId else 0,
                    smokeDesc = lifestyle.smokingPattern,
                    alcoholUse = if (lifestyle.isDrinking == true) lifestyle.drinkingStatusId else LifeStyleStatus.NEVER.id,
                    alcoholUseDesc = lifestyle.drinkingPattern,
                    substanceUse = if (lifestyle.hasAddiction == true) lifestyle.substanceStatusId else LifeStyleStatus.NEVER.id,
                    substanceUseDesc = lifestyle.substancePattern,
                    exerciseFrequency = if (lifestyle.isExercising == true) lifestyle.exerciseStatusId else LifeStyleStatus.NEVER.id,
                    exerciseDesc = lifestyle.exerciseFrequency
                )
                Logger.d("HealthProfile", "Adding Lifestyle: $addLifestyleReq")
                addSelfDeclarativeUseCase(addLifestyleReq)
            }

            sendEvent(
                HealthProfileEvent.ShowToast(
                    "اطلاعات پرونده سلامت با موفقیت ثبت شد",
                    isError = false
                )
            )
            true
        } catch (e: Exception) {
            Logger.e("HealthProfile", "Error in submitFullDeclaration: ${e.message}")
            sendEvent(
                HealthProfileEvent.ShowToast(
                    "خطا در ثبت اطلاعات: ${e.message}",
                    isError = true
                )
            )
            false
        }
    }


    override fun reduceState(
        currentState: HealthProfileUiState,
        partialState: PartialState
    ): HealthProfileUiState = when (partialState) {
        is PartialState.Loading -> {
            currentState.copy(isLoading = partialState.isLoading)
        }

        PartialState.ClearAllErrors -> {
            currentState.copy(errors = emptyMap())
        }

        is PartialState.ProvincesLoading -> currentState.copy(isProvincesLoading = partialState.isLoading)
        is PartialState.CitiesLoading -> currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errors = currentState.errors + (partialState.source to partialState.message)
        )

        is PartialState.GeneralLoaded -> reduceGeneralLoaded(currentState, partialState.info).copy(
            errors = currentState.errors - ErrorSource.PATIENT_GENERAL
        )

        is PartialState.LifestyleLoaded -> reduceLifestyleLoaded(
            currentState,
            partialState.info
        ).copy(
            errors = currentState.errors - ErrorSource.PATIENT_LIFESTYLE
        )

        is PartialState.AllergiesLoaded -> reduceAllergiesLoaded(
            currentState,
            partialState.list
        ).copy(
            errors = currentState.errors - ErrorSource.PATIENT_ALLERGIES
        )

        is PartialState.MaritalStatusLoaded -> currentState.copy(
            maritalStatusOptions = partialState.options,
            errors = currentState.errors - ErrorSource.MARITAL_STATUS
        )

        is PartialState.ProvincesLoaded -> currentState.copy(
            provinceOptions = partialState.options,
            isProvincesLoading = false,
            errors = currentState.errors - ErrorSource.PROVINCES
        )

        is PartialState.CitiesLoaded -> currentState.copy(
            cityOptions = partialState.options,
            isCitiesLoading = false,
            errors = currentState.errors - ErrorSource.CITIES
        )

        is PartialState.BloodGroupsLoaded -> {
            val sd = currentState.selfDeclaration
            val updatedSd =
                if (sd.bloodGroup.isBloodGroupUnknown && sd.bloodGroup.selectedBloodGroupId == null) {
                    val unknownId = partialState.options.find {
                        it.label.contains("نامشخص") || it.label.contains("نمی‌دانم") || it.label.contains(
                            "نمیدانم"
                        ) || it.label.contains("unknown")
                    }?.id
                    sd.copy(bloodGroup = sd.bloodGroup.copy(selectedBloodGroupId = unknownId))
                } else sd

            currentState.copy(
                bloodGroupOptions = partialState.options,
                selfDeclaration = updatedSd,
                errors = currentState.errors - ErrorSource.BLOOD_GROUPS
            )
        }

        is PartialState.SmokingStatusLoaded -> currentState.copy(
            smokingStatusOptions = partialState.options,
            errors = currentState.errors - ErrorSource.SMOKING_STATUS
        )

        is PartialState.ActFrequenciesLoaded -> currentState.copy(
            actFrequencyOptions = partialState.options,
            errors = currentState.errors - ErrorSource.ACT_FREQUENCIES
        )

        is PartialState.IllnessGroupsLoaded -> currentState.copy(
            illnessGroups = partialState.groups,
            errors = currentState.errors - ErrorSource.ILLNESS_GROUPS
        )

        is PartialState.DrugsLoaded -> currentState.copy(
            drugOptions = partialState.options,
            errors = currentState.errors - ErrorSource.DRUGS
        )

        is PartialState.StepChanged -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                currentStep = partialState.step,
                isEditMode = partialState.isEditMode
            )
        )

        is PartialState.IdentityUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                identity = partialState.identity
            )
        )

        is PartialState.PersonalUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                personal = partialState.personal
            )
        )

        is PartialState.ContactUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                contact = partialState.contact
            )
        )

        is PartialState.EmergencyUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                emergency = partialState.emergency
            )
        )

        is PartialState.PhysicalUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                physical = partialState.physical
            )
        )

        is PartialState.DiseasesUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                diseases = partialState.diseases
            )
        )

        is PartialState.FamilyUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                family = partialState.family
            )
        )

        is PartialState.BloodGroupUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                bloodGroup = partialState.bloodGroup
            )
        )

        is PartialState.LifestyleUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                lifestyle = partialState.lifestyle
            )
        )

        is PartialState.AllergyUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(
                allergy = partialState.allergy
            )
        )
    }

    private fun reduceGeneralLoaded(
        currentState: HealthProfileUiState,
        info: com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralPR
    ): HealthProfileUiState {
        val sd = currentState.selfDeclaration
        val hasLocalBloodGroupEdit =
            sd.bloodGroup.selectedBloodGroupId != null || sd.bloodGroup.isBloodGroupUnknown

        return currentState.copy(
            isLoading = false,
            generalInfo = info,
            selfDeclaration = sd.copy(
                identity = sd.identity.copy(
                    patientName = info.patientName,
                    patientFamily = info.patientFamily,
                    patientFather = info.patientFather,
                    patientGender = info.patientGender,
                    patientBirthDate = info.patientBirthDate,
                    insuranceNumber = info.insuranceNumber,
                    insuranceType = info.insuranceType,
                    lastVisitDate = info.lastVisitDate
                ),
                contact = sd.contact.copy(
                    mobile = info.patientMobile,
                    address = info.patientAddress
                ),
                emergency = sd.emergency.copy(
                    emergencyName = info.emergencyName,
                    emergencyFamily = info.emergencyFamily,
                    emergencyRelation = info.emergencyRelation,
                    emergencyMobile = info.emergencyMobile
                ),
                physical = sd.physical.copy(
                    height = if (info.patientHeight > 0) info.patientHeight.toInt() else null,
                    weight = if (info.patientWeight > 0) info.patientWeight.toInt() else null
                ),
                bloodGroup = if (hasLocalBloodGroupEdit) {
                    sd.bloodGroup
                } else {
                    val isUnknown =
                        info.patientBloodGroupCode == null || info.patientBloodGroupCode == 0
                    val unknownId = if (isUnknown) {
                        currentState.bloodGroupOptions.find {
                            it.label.contains("نامشخص") || it.label.contains("نمی‌دانم") || it.label.contains(
                                "نمیدانم"
                            ) || it.label.contains("unknown")
                        }?.id
                    } else {
                        info.patientBloodGroupCode
                    }

                    sd.bloodGroup.copy(
                        selectedBloodGroupId = unknownId,
                        selectedBloodGroupLetter = extractLetter(info.patientBloodGroup),
                        selectedBloodGroupRh = extractRh(info.patientBloodGroup),
                        isBloodGroupUnknown = isUnknown
                    )
                }
            )
        )
    }

    private fun reduceLifestyleLoaded(
        currentState: HealthProfileUiState,
        info: com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativePR
    ): HealthProfileUiState {
        val sd = currentState.selfDeclaration
        fun isActive(code: Int?) =
            code != null && LifeStyleStatus.fromStyleId(code) != LifeStyleStatus.NEVER

        val hasLocalLifestyleEdit = with(sd.lifestyle) {
            isSmoking != null || hasAddiction != null || isDrinking != null || isExercising != null
        }

        return currentState.copy(
            isLoading = false,
            lifestyleInfo = info,
            selfDeclaration = sd.copy(
                lifestyle = if (hasLocalLifestyleEdit) {
                    sd.lifestyle
                } else {
                    sd.lifestyle.copy(
                        isSmoking = info.smokingStatus != null && info.smokingStatus != SmokingStatus.NONE.id && info.smokingStatus != SmokingStatus.NEVER_CONSUMED.id,
                        smokingStatusId = info.smokingStatus,
                        smokingPattern = info.smokingDesc.takeIf { it.isNotBlank() }
                            ?: currentState.smokingStatusOptions.find { it.id == info.smokingStatus }?.label,

                        hasAddiction = isActive(info.substanceUsage),
                        substanceStatusId = info.substanceUsage,
                        substancePattern = info.substanceDesc.takeIf { it.isNotBlank() }
                            ?: currentState.actFrequencyOptions.find { it.id == info.substanceUsage }?.label,

                        isDrinking = isActive(info.alcoholUsage),
                        drinkingStatusId = info.alcoholUsage,
                        drinkingPattern = info.alcoholDesc.takeIf { it.isNotBlank() }
                            ?: LifeStyleStatus.fromStyleId(info.alcoholUsage)?.title,

                        isExercising = isActive(info.exerciseFreq),
                        exerciseStatusId = info.exerciseFreq,
                        exerciseFrequency = info.exerciseDesc.takeIf { it.isNotBlank() }
                            ?: LifeStyleStatus.fromStyleId(info.exerciseFreq)?.title
                    )
                }
            )
        )
    }

    private fun reduceAllergiesLoaded(
        currentState: HealthProfileUiState,
        list: List<com.tamin.taminhamrah.feature.healthProfile.ui.model.DrugAllergyItemPR>
    ): HealthProfileUiState {
        val sd = currentState.selfDeclaration
        return currentState.copy(
            isLoading = false,
            drugAllergies = list,
            selfDeclaration = sd.copy(
                allergy = if (sd.allergy.allergies.isNotEmpty()) {
                    sd.allergy
                } else {
                    sd.allergy.copy(allergies = list)
                }
            )
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

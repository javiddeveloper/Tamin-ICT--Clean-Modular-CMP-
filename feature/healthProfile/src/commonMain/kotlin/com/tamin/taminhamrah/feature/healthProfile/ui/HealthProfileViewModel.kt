package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.mapper.*
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LifeStyleStatus
import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.useCases.health.*
import com.tamin.taminhamrah.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class HealthProfileViewModel(
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

    init {
        sendIntent(HealthProfileIntent.LoadHealthProfile())
    }

    override fun handleIntent(intent: HealthProfileIntent): Flow<PartialState> {
        return when (intent) {
            is HealthProfileIntent.LoadHealthProfile     -> handleLoadHealthProfile(intent.nationalCode)
            is HealthProfileIntent.LoadCitiesForProvince -> handleLoadCities(intent.provinceId)

            is HealthProfileIntent.ChangeStep -> flow {
                if (intent.step == SelfDeclarationStep.SUCCESS) {
                    emit(PartialState.Loading(true))
                    val success = submitFullDeclaration()
                    emit(PartialState.Loading(false))
                    if (success) emit(PartialState.StepChanged(SelfDeclarationStep.SUCCESS))
                } else {
                    emit(PartialState.StepChanged(intent.step))
                }
            }

            is HealthProfileIntent.SubmitDeclaration -> flow {
                emit(PartialState.Loading(true))
                val success = submitFullDeclaration()
                emit(PartialState.Loading(false))
                if (success) emit(PartialState.StepChanged(SelfDeclarationStep.SUCCESS))
            }

            is HealthProfileIntent.UpdateIdentity   -> flow { emit(PartialState.IdentityUpdated(intent.identity)) }
            is HealthProfileIntent.UpdatePersonal   -> flow { emit(PartialState.PersonalUpdated(intent.personal)) }
            is HealthProfileIntent.UpdateContact    -> flow { emit(PartialState.ContactUpdated(intent.contact)) }
            is HealthProfileIntent.UpdateEmergency  -> flow { emit(PartialState.EmergencyUpdated(intent.emergency)) }
            is HealthProfileIntent.UpdatePhysical   -> flow { emit(PartialState.PhysicalUpdated(intent.physical)) }
            is HealthProfileIntent.UpdateDiseases   -> flow { emit(PartialState.DiseasesUpdated(intent.diseases)) }
            is HealthProfileIntent.UpdateFamily     -> flow { emit(PartialState.FamilyUpdated(intent.family)) }
            is HealthProfileIntent.UpdateBloodGroup -> flow {
                Logger.d("BloodGroupUpdate", "User updated blood group")
                Logger.d("BloodGroupUpdate", "selectedBloodGroupId: ${intent.bloodGroup.selectedBloodGroupId}")
                Logger.d("BloodGroupUpdate", "selectedBloodGroupLetter: ${intent.bloodGroup.selectedBloodGroupLetter}")
                Logger.d("BloodGroupUpdate", "selectedBloodGroupRh: ${intent.bloodGroup.selectedBloodGroupRh}")
                Logger.d("BloodGroupUpdate", "isBloodGroupUnknown: ${intent.bloodGroup.isBloodGroupUnknown}")
                emit(PartialState.BloodGroupUpdated(intent.bloodGroup))
            }
            is HealthProfileIntent.UpdateLifestyle  -> flow { emit(PartialState.LifestyleUpdated(intent.lifestyle)) }
            is HealthProfileIntent.UpdateAllergy    -> flow { emit(PartialState.AllergyUpdated(intent.allergy)) }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Load everything on init
    // ─────────────────────────────────────────────────────────────────────────

    private fun handleLoadHealthProfile(nationalCode: String?): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))

        // 1. Lookup lists (independent of patient data — load immediately)
        getMaritalStatusUseCase.invoke()
            .catch { }
            .collect { emit(PartialState.MaritalStatusLoaded(it.map { s -> s.toPresentation() })) }

        emit(PartialState.ProvincesLoading(true))
        getAllProvincesUseCase()
            .catch { emit(PartialState.ProvincesLoading(false)) }
            .collect {
                emit(PartialState.ProvincesLoaded(it.map { p -> p.toPresentation() }))
                emit(PartialState.ProvincesLoading(false))
            }

        getBloodGroupsUseCase()
            .catch { }
            .collect { emit(PartialState.BloodGroupsLoaded(it.map { b -> b.toPresentation() })) }

        getSmokingStatusUseCase()
            .catch { }
            .collect { emit(PartialState.SmokingStatusLoaded(it.map { s -> s.toPresentation() })) }

        getActFrequenciesUseCase()
            .catch { }
            .collect { emit(PartialState.ActFrequenciesLoaded(it.map { s -> s.toPresentation() })) }

        getSelfDeclarableIllnessesByGroupUseCase()
            .catch { }
            .collect { emit(PartialState.IllnessGroupsLoaded(it.map { g -> g.toPresentation() })) }

        getAllDrugsUseCase()
            .catch { }
            .collect { emit(PartialState.DrugsLoaded(it.map { d -> d.toPresentation() })) }

        val targetNatCode = nationalCode?.takeIf { it.isNotBlank() } ?: currentPatientNatCode

        if (targetNatCode.isNotBlank()) {
            // 2. Patient general info
            getPatientGeneralUseCase(targetNatCode)
                .catch { emit(PartialState.Error(it.message ?: "خطا در دریافت اطلاعات عمومی")) }
                .collect { general ->
                    val safeNatCode = general.patientNatCode?.takeIf { it.isNotBlank() } ?: targetNatCode
                    val safePatientId = general.ptientID ?: 0

                    currentPatientNatCode = safeNatCode
                    currentPatientId = safePatientId

                    emit(PartialState.GeneralLoaded(general.toPresentation()))

                    if (safeNatCode.isNotBlank() && safePatientId != 0) {
                        // 3. Lifestyle / self-declarative
                        getPatientSelfDeclarativeUseCase(safeNatCode, safePatientId)
                            .catch { /* non-fatal */ }
                            .collect { emit(PartialState.LifestyleLoaded(it.toPresentation())) }

                        // 4. Drug allergies
                        getPatientDrugAllergiesUseCase(safeNatCode, safePatientId)
                            .catch { /* non-fatal */ }
                            .collect { emit(PartialState.AllergiesLoaded(it.map { d -> d.toPresentation() })) }
                    }
                }
        }

        emit(PartialState.Loading(false))
    }

    // Triggered when user selects a province in Step 3
    private fun handleLoadCities(provinceId: Int): Flow<PartialState> = flow {
        emit(PartialState.CitiesLoading(true))
        getProvinceCitiesUseCase(provinceId)
            .catch { emit(PartialState.CitiesLoading(false)) }
            .collect {
                emit(PartialState.CitiesLoaded(it.map { c -> c.toPresentation() }))
                emit(PartialState.CitiesLoading(false))
            }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Submit full declaration (called on SUCCESS step or explicit submit)
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun submitFullDeclaration(): Boolean {
        if (currentPatientId == 0 || currentPatientNatCode.isBlank()) {
            sendEvent(HealthProfileEvent.ShowToast("اطلاعات شناسایی بیمار یا کد ملی یافت نشد."))
            return false
        }

        return try {
            val selfDecState = uiState.value.selfDeclaration

            // A. Update patient demographics, contact, emergency, physical
            val updatePatientReq = UpdatePatientRequest(
                patientID        = currentPatientId,
                patientNatCode   = currentPatientNatCode,
                patientMobile    = selfDecState.contact.mobile,
                patientEmail     = selfDecState.contact.email,
                patientAddress   = selfDecState.contact.address,
                patientArea      = null,
                patientCityID    = selfDecState.contact.cityId,
                patientBloodGroup = selfDecState.bloodGroup.selectedBloodGroupId,
                patientMarriage  = selfDecState.personal.maritalStatusId,
                patientJob       = selfDecState.personal.job,
                patientHeight    = selfDecState.physical.height,
                patientWeight    = selfDecState.physical.weight,
                patientCitizenship = selfDecState.personal.citizenship,
                patientNationality = selfDecState.personal.nationality,
                patientInsurance = null,
                emergencyName    = selfDecState.emergency.emergencyName,
                emergencyFamily  = selfDecState.emergency.emergencyFamily,
                emergencyMobile  = selfDecState.emergency.emergencyMobile,
                emergencyEmail   = null,
                emergencyRelation = null,
                emergencyAddress = null,
                emergencyArea    = null,
                emergencyCityID  = null
            )
            updatePatientUseCase(updatePatientReq)

            // B. Sync diseases + family history
            val illnessList = mutableListOf<IllnessSelfDeclareRequest>()
            selfDecState.diseases.chronicDiseaseIds.forEach { illnessList.add(IllnessSelfDeclareRequest(it, 0, null)) }
            selfDecState.diseases.mentalIllnessIds.forEach  { illnessList.add(IllnessSelfDeclareRequest(it, 0, null)) }
            selfDecState.diseases.cancerIds.forEach         { illnessList.add(IllnessSelfDeclareRequest(it, 0, null)) }
            selfDecState.family.familyCancerIds.forEach     { illnessList.add(IllnessSelfDeclareRequest(it, 1, null)) }

            if (illnessList.isNotEmpty()) {
                syncIllnessSelfDeclarativesUseCase(
                    SyncIllnessSelfDeclarativesRequest(
                        natCode              = currentPatientNatCode,
                        patientID            = currentPatientId,
                        illnessSelfDeclareList = illnessList
                    )
                )
            }

            // C. Sync drug allergies
            val allergyList = selfDecState.allergy.allergies.map {
                DrugAllergyRequest(drugId = it.drugId, allergyComments = it.allergyComments)
            }
            if (allergyList.isNotEmpty()) {
                syncDrugAllergiesUseCase(
                    SyncDrugAllergiesRequest(
                        natCode        = currentPatientNatCode,
                        patientID      = currentPatientId,
                        drugAllergyList = allergyList
                    )
                )
            }

            // D. Add / update self-declarative lifestyle
            val lifestyleReq = AddSelfDeclarativeRequest(
                natCode          = currentPatientNatCode,
                patientID        = currentPatientId,
                smoking          = if (selfDecState.lifestyle.isSmoking == true) selfDecState.lifestyle.smokingStatusId ?: 0 else 0,
                smokeDesc        = selfDecState.lifestyle.smokingPattern,
                alcoholUse       = if (selfDecState.lifestyle.isDrinking == true) selfDecState.lifestyle.drinkingStatusId ?: 0 else LifeStyleStatus.NEVER.id,
                alcoholUseDesc   = selfDecState.lifestyle.drinkingPattern,
                substanceUse     = if (selfDecState.lifestyle.hasAddiction == true) selfDecState.lifestyle.substanceStatusId ?: 0 else LifeStyleStatus.NEVER.id,
                substanceUseDesc = selfDecState.lifestyle.substancePattern,
                exerciseFrequency = if (selfDecState.lifestyle.isExercising == true) selfDecState.lifestyle.exerciseStatusId ?: 0 else LifeStyleStatus.NEVER.id,
                exerciseDesc     = selfDecState.lifestyle.exerciseFrequency
            )
            addSelfDeclarativeUseCase(lifestyleReq)

            sendEvent(HealthProfileEvent.ShowToast("اطلاعات پرونده سلامت با موفقیت ثبت شد"))
            true
        } catch (e: Exception) {
            sendEvent(HealthProfileEvent.ShowToast("خطا در ثبت اطلاعات: ${e.message}"))
            false
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reduce
    // ─────────────────────────────────────────────────────────────────────────

    override fun reduceState(
        currentState: HealthProfileUiState,
        partialState: PartialState
    ): HealthProfileUiState = when (partialState) {

        is PartialState.Loading          -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.ProvincesLoading -> currentState.copy(isProvincesLoading = partialState.isLoading)
        is PartialState.CitiesLoading    -> currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.Error            -> currentState.copy(isLoading = false, error = partialState.message)

        // ── Remote patient data ───────────────────────────────────────────────
        is PartialState.GeneralLoaded -> {
            val info = partialState.info
            val sd   = currentState.selfDeclaration
            currentState.copy(
                isLoading   = false,
                generalInfo = info,
                selfDeclaration = sd.copy(
                    identity = sd.identity.copy(
                        patientName    = info.patientName,
                        patientFamily  = info.patientFamily,
                        patientFather  = info.patientFather,
                        patientGender  = info.patientGender,
                        patientBirthDate = info.patientBirthDate,
                        insuranceNumber = info.insuranceNumber,
                        insuranceType  = info.insuranceType,
                        lastVisitDate  = info.lastVisitDate
                    ),
                    contact = sd.contact.copy(
                        mobile  = info.patientMobile,
                        address = info.patientAddress
                    ),
                    emergency = sd.emergency.copy(
                        emergencyName    = info.emergencyName,
                        emergencyFamily  = info.emergencyFamily,
                        emergencyRelation = info.emergencyRelation,
                        emergencyMobile  = info.emergencyMobile
                    ),
                    physical = sd.physical.copy(
                        height = info.patientHeight.toInt(),
                        weight = info.patientWeight.toInt()
                    ),
                    bloodGroup = sd.bloodGroup.copy(
                        selectedBloodGroupId = info.patientBloodGroupCode,
                        selectedBloodGroupLetter = info.patientBloodGroup,
                        selectedBloodGroupRh = info.patientBloodGroup
                    )
                )
            )
        }


            is PartialState.LifestyleLoaded -> {
            val info = partialState.info
            val sd = currentState.selfDeclaration

            fun isActive(code: Int?) =
                code != null && LifeStyleStatus.fromStyleId(code) != LifeStyleStatus.NEVER

            currentState.copy(
                isLoading = false,
                lifestyleInfo = info,
                selfDeclaration = sd.copy(
                    lifestyle = sd.lifestyle.copy(
                        isSmoking = (info.smokingStatus ?: 0) > 0,
                        smokingStatusId = info.smokingStatus,
                        smokingPattern = info.smokingDesc.takeIf { it.isNotBlank() },

                        hasAddiction = isActive(info.substanceUsage),
                        substanceStatusId = info.substanceUsage,
                        substancePattern = info.substanceDesc.takeIf { it.isNotBlank() },

                        isDrinking = isActive(info.alcoholUsage),
                        drinkingStatusId = info.alcoholUsage,
                        drinkingPattern = info.alcoholDesc.takeIf { it.isNotBlank() },

                        isExercising = isActive(info.exerciseFreq),
                        exerciseStatusId = info.exerciseFreq,
                        exerciseFrequency = info.exerciseDesc.takeIf { it.isNotBlank() }
                    )
                )
            )
        }

        is PartialState.AllergiesLoaded -> {
            val list = partialState.list
            val sd   = currentState.selfDeclaration
            currentState.copy(
                isLoading    = false,
                drugAllergies = list,
                selfDeclaration = sd.copy(allergy = sd.allergy.copy(allergies = list))
            )
        }

        // ── Lookup lists ──────────────────────────────────────────────────────
        is PartialState.MaritalStatusLoaded -> currentState.copy(maritalStatusOptions = partialState.options)
        is PartialState.ProvincesLoaded     -> currentState.copy(provinceOptions = partialState.options, isProvincesLoading = false)
        is PartialState.CitiesLoaded        -> currentState.copy(cityOptions = partialState.options, isCitiesLoading = false)
        is PartialState.BloodGroupsLoaded   -> currentState.copy(bloodGroupOptions = partialState.options)
        is PartialState.SmokingStatusLoaded -> currentState.copy(smokingStatusOptions = partialState.options)
        is PartialState.ActFrequenciesLoaded -> currentState.copy(actFrequencyOptions = partialState.options)
        is PartialState.IllnessGroupsLoaded -> currentState.copy(illnessGroups = partialState.groups)
        is PartialState.DrugsLoaded         -> currentState.copy(drugOptions = partialState.options)

        // ── Step navigation ───────────────────────────────────────────────────
        is PartialState.StepChanged -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(currentStep = partialState.step)
        )

        // ── Per-step updates ──────────────────────────────────────────────────
        is PartialState.IdentityUpdated  -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(identity  = partialState.identity))
        is PartialState.PersonalUpdated  -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(personal  = partialState.personal))
        is PartialState.ContactUpdated   -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(contact   = partialState.contact))
        is PartialState.EmergencyUpdated -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(emergency = partialState.emergency))
        is PartialState.PhysicalUpdated  -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(physical  = partialState.physical))
        is PartialState.DiseasesUpdated  -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(diseases  = partialState.diseases))
        is PartialState.FamilyUpdated    -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(family    = partialState.family))
        is PartialState.BloodGroupUpdated -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(bloodGroup = partialState.bloodGroup))
        is PartialState.LifestyleUpdated -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(lifestyle = partialState.lifestyle))
        is PartialState.AllergyUpdated   -> currentState.copy(selfDeclaration = currentState.selfDeclaration.copy(allergy   = partialState.allergy))
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

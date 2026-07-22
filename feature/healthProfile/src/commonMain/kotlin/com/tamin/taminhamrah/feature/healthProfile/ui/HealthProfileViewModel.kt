package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.mapper.toUiMock
import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.useCases.health.*
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
    private val getSelfDeclarableIllnessesUseCase: GetSelfDeclarableIllnessesUseCase,
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

            is SelfDeclarationIntent.ChangeStep -> flow {
                if (intent.step == SelfDeclarationStep.SUCCESS) {
                    emit(PartialState.Loading(true))
                    val success = submitFullDeclaration()
                    emit(PartialState.Loading(false))
                    if (success) {
                        emit(PartialState.StepChanged(SelfDeclarationStep.SUCCESS))
                    }
                } else {
                    emit(PartialState.StepChanged(intent.step))
                }
            }

            is SelfDeclarationIntent.SubmitDeclaration -> flow {
                emit(PartialState.Loading(true))
                val success = submitFullDeclaration()
                emit(PartialState.Loading(false))
                if (success) {
                    emit(PartialState.StepChanged(SelfDeclarationStep.SUCCESS))
                }
            }

            is SelfDeclarationIntent.UpdateIdentity -> flow { emit(PartialState.IdentityUpdated(intent.identity)) }
            is SelfDeclarationIntent.UpdatePersonal -> flow { emit(PartialState.PersonalUpdated(intent.personal)) }
            is SelfDeclarationIntent.UpdateContact -> flow { emit(PartialState.ContactUpdated(intent.contact)) }
            is SelfDeclarationIntent.UpdateEmergency -> flow { emit(PartialState.EmergencyUpdated(intent.emergency)) }
            is SelfDeclarationIntent.UpdatePhysical -> flow { emit(PartialState.PhysicalUpdated(intent.physical)) }
            is SelfDeclarationIntent.UpdateDiseases -> flow { emit(PartialState.DiseasesUpdated(intent.diseases)) }
            is SelfDeclarationIntent.UpdateFamily -> flow { emit(PartialState.FamilyUpdated(intent.family)) }
            is SelfDeclarationIntent.UpdateBloodGroup -> flow { emit(PartialState.BloodGroupUpdated(intent.bloodGroup)) }
            is SelfDeclarationIntent.UpdateLifestyle -> flow { emit(PartialState.LifestyleUpdated(intent.lifestyle)) }
            is SelfDeclarationIntent.UpdateAllergy -> flow { emit(PartialState.AllergyUpdated(intent.allergy)) }
            else -> flow {}
        }
    }

    private fun handleLoadHealthProfile(nationalCode: String?): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))

        val targetNatCode = nationalCode?.takeIf { it.isNotBlank() } ?: currentPatientNatCode

        getPatientGeneralUseCase(targetNatCode)
            .catch { emit(PartialState.Error(it.message ?: "خطا در دریافت اطلاعات عمومی")) }
            .collect { general ->
                val safeNatCode = general.patientNatCode ?: targetNatCode
                val safePatientId = general.ptientID ?: 0

                currentPatientNatCode = safeNatCode
                currentPatientId = safePatientId

                emit(PartialState.GeneralLoaded(general.toUiMock()))

                if (safeNatCode.isNotBlank() && safePatientId != 0) {
                    // Fetch Lifestyle & Self-Declarative info safely
                    getPatientSelfDeclarativeUseCase(safeNatCode, safePatientId)
                        .catch { /* non-fatal fallback */ }
                        .collect { selfDec ->
                            emit(PartialState.LifestyleLoaded(selfDec.toUiMock()))
                        }

                    // Fetch Drug Allergies safely
                    getPatientDrugAllergiesUseCase(safeNatCode, safePatientId)
                        .catch { /* non-fatal fallback */ }
                        .collect { allergies ->
                            emit(PartialState.AllergiesLoaded(allergies.map { it.toUiMock() }))
                        }
                }
            }

        emit(PartialState.Loading(false))
    }

    private suspend fun submitFullDeclaration(): Boolean {
        if (currentPatientId == 0 || currentPatientNatCode.isBlank()) {
            sendEvent(HealthProfileEvent.ShowToast("اطلاعات شناسایی بیمار یا کد ملی یافت نشد."))
            return false
        }

        return try {
            val selfDecState = uiState.value.selfDeclaration

            // A. Update Patient Demographics & Contact & Emergency & Physical
            val updatePatientReq = UpdatePatientRequest(
                patientID = currentPatientId,
                patientNatCode = currentPatientNatCode,
                patientMobile = selfDecState.contact.mobile,
                patientEmail = selfDecState.contact.email,
                patientAddress = selfDecState.contact.address,
                patientArea = null,
                patientCityID = null,
                patientBloodGroup = null,
                patientMarriage = null,
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
                emergencyRelation = null,
                emergencyAddress = null,
                emergencyArea = null,
                emergencyCityID = null
            )
            updatePatientUseCase(updatePatientReq)

            // B. Sync Diseases & Family History
            val illnessList = mutableListOf<IllnessSelfDeclareRequest>()
            selfDecState.diseases.chronicDiseases.forEach { illnessList.add(IllnessSelfDeclareRequest(it, 0, null)) }
            selfDecState.diseases.mentalIllnesses.forEach { illnessList.add(IllnessSelfDeclareRequest(it, 0, null)) }
            selfDecState.diseases.cancers.forEach { illnessList.add(IllnessSelfDeclareRequest(it, 0, null)) }
            selfDecState.family.familyCancers.forEach { illnessList.add(IllnessSelfDeclareRequest(it, 1, null)) }

            if (illnessList.isNotEmpty()) {
                syncIllnessSelfDeclarativesUseCase(
                    SyncIllnessSelfDeclarativesRequest(
                        natCode = currentPatientNatCode,
                        patientID = currentPatientId,
                        illnessSelfDeclareList = illnessList
                    )
                )
            }

            // C. Sync Drug Allergies
            val allergyList = selfDecState.allergy.allergies.map {
                DrugAllergyRequest(drugId = it.drugId.toInt(), allergyComments = it.allergyComments)
            }
            if (allergyList.isNotEmpty()) {
                syncDrugAllergiesUseCase(
                    SyncDrugAllergiesRequest(
                        natCode = currentPatientNatCode,
                        patientID = currentPatientId,
                        drugAllergyList = allergyList
                    )
                )
            }

            // D. Add / Update Self-Declarative Lifestyle
            val lifestyleReq = AddSelfDeclarativeRequest(
                natCode = currentPatientNatCode,
                patientID = currentPatientId,
                smoking = if (selfDecState.lifestyle.isSmoking == true) 1 else 0,
                smokeDesc = selfDecState.lifestyle.smokingPattern,
                alcoholUse = if (selfDecState.lifestyle.isDrinking == true) 1 else 0,
                alcoholUseDesc = selfDecState.lifestyle.drinkingPattern,
                substanceUse = if (selfDecState.lifestyle.hasAddiction == true) 1 else 0,
                substanceUseDesc = null,
                exerciseFrequency = if (selfDecState.lifestyle.isExercising == true) 1 else 0,
                exerciseDesc = selfDecState.lifestyle.exerciseFrequency
            )
            addSelfDeclarativeUseCase(lifestyleReq)

            sendEvent(HealthProfileEvent.ShowToast("اطلاعات پرونده سلامت با موفقیت ثبت شد"))
            true
        } catch (e: Exception) {
            sendEvent(HealthProfileEvent.ShowToast("خطا در ثبت اطلاعات: ${e.message}"))
            false
        }
    }

    override fun reduceState(
        currentState: HealthProfileUiState,
        partialState: PartialState
    ): HealthProfileUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.GeneralLoaded -> currentState.copy(
            isLoading = false,
            generalInfo = partialState.info
        )
        is PartialState.LifestyleLoaded -> currentState.copy(
            isLoading = false,
            lifestyleInfo = partialState.info
        )
        is PartialState.AllergiesLoaded -> currentState.copy(
            isLoading = false,
            drugAllergies = partialState.list
        )
        is PartialState.StepChanged -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(currentStep = partialState.step)
        )
        is PartialState.IdentityUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(identity = partialState.identity)
        )
        is PartialState.PersonalUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(personal = partialState.personal)
        )
        is PartialState.ContactUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(contact = partialState.contact)
        )
        is PartialState.EmergencyUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(emergency = partialState.emergency)
        )
        is PartialState.PhysicalUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(physical = partialState.physical)
        )
        is PartialState.DiseasesUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(diseases = partialState.diseases)
        )
        is PartialState.FamilyUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(family = partialState.family)
        )
        is PartialState.BloodGroupUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(bloodGroup = partialState.bloodGroup)
        )
        is PartialState.LifestyleUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(lifestyle = partialState.lifestyle)
        )
        is PartialState.AllergyUpdated -> currentState.copy(
            selfDeclaration = currentState.selfDeclaration.copy(allergy = partialState.allergy)
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}

package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileEvent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.model.HealthProfileMockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class HealthProfileViewModel : BaseViewModel<HealthProfileUiState, PartialState, HealthProfileEvent, HealthProfileIntent>(
    initialState = HealthProfileUiState()
) {

    override fun handleIntent(intent: HealthProfileIntent): Flow<PartialState> {
        return when (intent) {
            is HealthProfileIntent.LoadHealthProfile -> handleLoadHealthProfile()
            is SelfDeclarationIntent.ChangeStep -> flow { emit(PartialState.StepChanged(intent.step)) }
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
        }
    }

    private fun handleLoadHealthProfile(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        // Add a slight delay to simulate network call loading effect
        delay(500)
        emit(PartialState.GeneralLoaded(HealthProfileMockData.generalInfo))
        emit(PartialState.LifestyleLoaded(HealthProfileMockData.lifestyleInfo))
        emit(PartialState.AllergiesLoaded(HealthProfileMockData.drugAllergies))
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

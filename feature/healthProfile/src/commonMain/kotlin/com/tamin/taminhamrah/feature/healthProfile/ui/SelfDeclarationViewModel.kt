package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationEvent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SelfDeclarationViewModel : BaseViewModel<SelfDeclarationUiState, PartialState, SelfDeclarationEvent, SelfDeclarationIntent>(
    initialState = SelfDeclarationUiState()
) {
    override fun handleIntent(intent: SelfDeclarationIntent): Flow<PartialState> {
        return when (intent) {
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

    override fun reduceState(
        currentState: SelfDeclarationUiState,
        partialState: PartialState
    ): SelfDeclarationUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step
        )
        is PartialState.IdentityUpdated -> currentState.copy(identity = partialState.identity)
        is PartialState.PersonalUpdated -> currentState.copy(personal = partialState.personal)
        is PartialState.ContactUpdated -> currentState.copy(contact = partialState.contact)
        is PartialState.EmergencyUpdated -> currentState.copy(emergency = partialState.emergency)
        is PartialState.PhysicalUpdated -> currentState.copy(physical = partialState.physical)
        is PartialState.DiseasesUpdated -> currentState.copy(diseases = partialState.diseases)
        is PartialState.FamilyUpdated -> currentState.copy(family = partialState.family)
        is PartialState.BloodGroupUpdated -> currentState.copy(bloodGroup = partialState.bloodGroup)
        is PartialState.LifestyleUpdated -> currentState.copy(lifestyle = partialState.lifestyle)
        is PartialState.AllergyUpdated -> currentState.copy(allergy = partialState.allergy)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}


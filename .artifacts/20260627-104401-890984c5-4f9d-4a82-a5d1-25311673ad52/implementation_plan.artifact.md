# Refactor Treatment Feature to use Multiple ViewModels

The goal is to refactor the `TreatmentViewModel` by extracting sub-flow logic into separate, specialized ViewModels for each dashboard menu item. This will improve maintainability and follow the single responsibility principle.

## User Review Required

- **ViewModel Scoping**: Each sub-flow content will instantiate its own ViewModel. They will be notified of the `selectedNationalCode` from the parent `TreatmentScreen`.
- **State Management**: The parent `TreatmentViewModel` will still manage the shared state such as the selected patient, the list of patients, and the active flow.

## Proposed Changes

### [New Contracts]

Create separate contract files for each sub-flow to define their own `UiState` and `Intent`.

#### [PrescriptionsContract.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/contract/PrescriptionsContract.kt)
- Define `PrescriptionsUiState` and `PrescriptionsIntent`.

#### [ConfirmationsContract.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/contract/ConfirmationsContract.kt)
- Define `ConfirmationsUiState` and `ConfirmationsIntent`.

#### [CostsContract.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/contract/CostsContract.kt)
- Define `CostsUiState` and `CostsIntent`.

#### [HealthProfileContract.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/contract/HealthProfileContract.kt)
- Define `HealthProfileUiState` and `HealthProfileIntent`.

---

### [New ViewModels]

Implement the new ViewModels by extracting logic from `TreatmentViewModel`.

#### [PrescriptionsViewModel.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/prescriptions/PrescriptionsViewModel.kt)
- Handles electronic prescriptions logic.

#### [MedicalConfirmationsViewModel.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/medicalConfirmations/MedicalConfirmationsViewModel.kt)
- Handles medical authorities logic.

#### [TreatmentCostsViewModel.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/treatmentCosts/TreatmentCostsViewModel.kt)
- Handles treatment costs logic.

#### [HealthProfileViewModel.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/healthProfile/HealthProfileViewModel.kt)
- Handles health profile, self-declarations, and allergies logic.

---

### [Refactoring Existing Files]

#### [TreatmentViewModel.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/TreatmentViewModel.kt)
- Remove sub-flow specific logic.
- Keep identity fetch, deserved status, and patient list management.

#### [TreatmentContract.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/contract/TreatmentContract.kt)
- Remove sub-flow specific state and intents.

#### [TreatmentModule.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/di/TreatmentModule.kt)
- Register the new ViewModels in the Koin module.

#### [TreatmentScreen.kt](file:///D:/Project/Android/tamin-kmp-new/feature/treatment/src/commonMain/kotlin/com/tamin/taminhamrah/feature/treatment/ui/TreatmentScreen.kt)
- Update to use the new ViewModels for each sub-flow.
- Pass the shared `selectedNationalCode` to the sub-flow ViewModels via Intents.

---

### [Stateless Content Updates]

Update the content composables to use their respective `UiState` instead of the global `TreatmentUiState`.

- `PrescriptionsContent`
- `MedicalConfirmationsContent`
- `TreatmentCostsContent`
- `HealthProfileContent`

## Verification Plan

### Automated Tests
- Run `gradlew :feature:treatment:assembleDebug` to ensure compilation.
- (Optional) Run existing `TreatmentViewModelTest` if they still apply, or update them.

### Manual Verification
- Deploy the app and navigate to the Treatment screen.
- Verify patient selection still works and updates sub-flows.
- Verify each sub-flow (Prescriptions, Confirmations, Costs, Health Profile) loads its data correctly.
- Verify switching back and forth between flows works as expected.

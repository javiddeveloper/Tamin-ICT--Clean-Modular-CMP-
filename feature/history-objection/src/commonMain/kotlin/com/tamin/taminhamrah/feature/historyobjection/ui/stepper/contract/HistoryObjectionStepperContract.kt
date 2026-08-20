package com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.InsuranceTypePR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

const val STEP_BRANCH = 1
const val STEP_WORKSHOP = 2
const val STEP_RECORD = 3

private const val WORKSHOP_ID_LENGTH = 10

/** Identifies which picker opened the shared bottom sheet, mirroring `addDependent`'s `BottomSheetTarget`. */
enum class HistoryObjectionBottomSheetTarget {
    PROVINCE, CITY, BRANCH, INSURANCE_TYPE
}

@Immutable
data class HistoryObjectionStepperState(
    val currentStep: Int = STEP_BRANCH,
    val isEditMode: Boolean = false,
    val editRequestNumber: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

    // Step 1 — اطلاعات شعبه
    val provinces: ImmutableList<ProvincePR> = persistentListOf(),
    val cities: ImmutableList<CityPR> = persistentListOf(),
    val branches: ImmutableList<BranchDN> = persistentListOf(),
    val insuranceTypes: ImmutableList<InsuranceTypePR> = persistentListOf(),
    val selectedProvince: ProvincePR? = null,
    val selectedCity: CityPR? = null,
    val selectedBranch: BranchDN? = null,
    val selectedInsuranceType: InsuranceTypePR? = null,
    val isCitiesLoading: Boolean = false,
    val isBranchesLoading: Boolean = false,
    val bottomSheetConfig: TaminBottomSheetConfig? = null,
    val bottomSheetTarget: HistoryObjectionBottomSheetTarget? = null,

    // Step 2 — اطلاعات کارگاه
    val workshopId: String = "",
    val workshopName: String = "",
    val employerName: String = "",
    val workshopAddress: String = "",

    // Step 3 — اطلاعات کارکرد
    val startDateLabel: String = "",
    val startDateTimestamp: Long? = null,
    val endDateLabel: String = "",
    val endDateTimestamp: Long? = null,
    val workDays: String = "",
) {
    val canGoNextFromCurrentStep: Boolean
        get() = when (currentStep) {
            STEP_BRANCH -> selectedProvince != null && selectedCity != null &&
                selectedBranch != null && selectedInsuranceType != null
            STEP_WORKSHOP -> workshopId.length == WORKSHOP_ID_LENGTH &&
                workshopName.isNotBlank() && employerName.isNotBlank() && workshopAddress.isNotBlank()
            STEP_RECORD -> startDateTimestamp != null && endDateTimestamp != null && workDays.isNotBlank()
            else -> false
        }

    sealed interface PartialState {
        data class ModeInitialized(val isEditMode: Boolean, val editRequestNumber: String?) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data object ErrorDismissed : PartialState
        data class StepChanged(val step: Int) : PartialState
        data class EditModeDataLoaded(
            val provinceCode: String?,
            val provinceName: String?,
            val cityCode: String?,
            val cityName: String?,
            val branch: BranchDN?,
            val insuranceType: InsuranceTypePR?,
            val workshopId: String,
            val workshopName: String,
            val employerName: String,
            val workshopAddress: String,
            val startDateTimestamp: Long?,
            val endDateTimestamp: Long?,
            val workDays: String,
        ) : PartialState

        data class ProvincesLoaded(val provinces: ImmutableList<ProvincePR>) : PartialState
        data class CitiesLoading(val isLoading: Boolean) : PartialState
        data class CitiesLoaded(val cities: ImmutableList<CityPR>) : PartialState
        data class BranchesLoading(val isLoading: Boolean) : PartialState
        data class BranchesLoaded(val branches: ImmutableList<BranchDN>) : PartialState
        data class InsuranceTypesLoaded(val insuranceTypes: ImmutableList<InsuranceTypePR>) : PartialState

        data class ProvinceSelected(val province: ProvincePR) : PartialState
        data class CitySelected(val city: CityPR) : PartialState
        data class BranchSelected(val branch: BranchDN) : PartialState
        data class InsuranceTypeSelected(val insuranceType: InsuranceTypePR) : PartialState

        data class BottomSheetStateChanged(
            val config: TaminBottomSheetConfig?,
            val target: HistoryObjectionBottomSheetTarget?,
        ) : PartialState

        data class WorkshopIdChanged(val value: String) : PartialState
        data class WorkshopNameChanged(val value: String) : PartialState
        data class EmployerNameChanged(val value: String) : PartialState
        data class WorkshopAddressChanged(val value: String) : PartialState

        data class StartDateSelected(val label: String, val timestamp: Long) : PartialState
        data class EndDateSelected(val label: String, val timestamp: Long) : PartialState
        data class WorkDaysChanged(val value: String) : PartialState
    }
}

sealed interface HistoryObjectionStepperIntent {
    data class Load(val editRequestNumber: String?) : HistoryObjectionStepperIntent
    data object OnNextClicked : HistoryObjectionStepperIntent
    data object OnBackClicked : HistoryObjectionStepperIntent
    data object OnConfirmClicked : HistoryObjectionStepperIntent
    data object OnErrorDismissed : HistoryObjectionStepperIntent

    data object OnShowProvincePicker : HistoryObjectionStepperIntent
    data object OnShowCityPicker : HistoryObjectionStepperIntent
    data object OnShowBranchPicker : HistoryObjectionStepperIntent
    data object OnShowInsuranceTypePicker : HistoryObjectionStepperIntent
    data object OnDismissBottomSheet : HistoryObjectionStepperIntent
    data class OnProvinceSelected(val province: ProvincePR) : HistoryObjectionStepperIntent
    data class OnCitySelected(val city: CityPR) : HistoryObjectionStepperIntent
    data class OnBranchSelected(val branch: BranchDN) : HistoryObjectionStepperIntent
    data class OnInsuranceTypeSelected(val insuranceType: InsuranceTypePR) : HistoryObjectionStepperIntent

    data class OnWorkshopIdChanged(val value: String) : HistoryObjectionStepperIntent
    data class OnWorkshopNameChanged(val value: String) : HistoryObjectionStepperIntent
    data class OnEmployerNameChanged(val value: String) : HistoryObjectionStepperIntent
    data class OnWorkshopAddressChanged(val value: String) : HistoryObjectionStepperIntent

    data class OnStartDateSelected(val year: Int, val month: Int, val day: Int) : HistoryObjectionStepperIntent
    data class OnEndDateSelected(val year: Int, val month: Int, val day: Int) : HistoryObjectionStepperIntent
    data class OnWorkDaysChanged(val value: String) : HistoryObjectionStepperIntent
}

sealed interface HistoryObjectionStepperEvent {
    data object NavigateBack : HistoryObjectionStepperEvent
    data class ShowMessage(val message: String) : HistoryObjectionStepperEvent
}

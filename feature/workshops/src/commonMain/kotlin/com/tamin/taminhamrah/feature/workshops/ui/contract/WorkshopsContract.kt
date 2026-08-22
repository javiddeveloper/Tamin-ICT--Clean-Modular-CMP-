package com.tamin.taminhamrah.feature.workshops.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.studentContract.BranchSelectionFormPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR

@Immutable
data class WorkshopsUiState(
    val isLoading: Boolean = false,
    val agreements: List<EmployerAgreementPR> = emptyList(),
    val error: String? = null,
    // استان → شهر → شعبه. Only the resolved branchCode reaches the workshops query: an
    // agreement carries no province or city of its own to filter on.
    val branchSelection: BranchSelectionFormPR = BranchSelectionFormPR(),
    val provinces: List<ProvincePR> = emptyList(),
    val cities: List<CityPR> = emptyList(),
    val branches: List<BranchPR> = emptyList(),
    val isProvincesLoading: Boolean = false,
    val isCitiesLoading: Boolean = false,
    val isBranchesLoading: Boolean = false,
    // Kept per picker rather than in `error`: a lookup that fails should mark its own field, not
    // replace the workshop list that loaded fine.
    val provincesError: String? = null,
    val citiesError: String? = null,
    val branchesError: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class WorkshopsLoaded(val list: List<EmployerAgreementPR>) : PartialState

        data class ProvincesLoading(val isLoading: Boolean) : PartialState
        data class ProvincesLoaded(val list: List<ProvincePR>) : PartialState
        data class CitiesLoading(val isLoading: Boolean) : PartialState
        data class CitiesLoaded(val list: List<CityPR>) : PartialState
        data class BranchesLoading(val isLoading: Boolean) : PartialState
        data class BranchesLoaded(val list: List<BranchPR>) : PartialState
        data class BranchSelectionChanged(val selection: BranchSelectionFormPR) : PartialState
        data class ProvincesError(val message: String?) : PartialState
        data class CitiesError(val message: String?) : PartialState
        data class BranchesError(val message: String?) : PartialState
    }
}

sealed interface WorkshopsIntent {
    data class LoadWorkshops(
        val workshopId: String? = null,
        val branchCode: String? = null,
        val workshopStatus: String? = null
    ) : WorkshopsIntent

    data object LoadProvinces : WorkshopsIntent
    data object RetryCities : WorkshopsIntent
    data object RetryBranches : WorkshopsIntent
    data class SelectProvince(val province: ProvincePR) : WorkshopsIntent
    data class SelectCity(val city: CityPR) : WorkshopsIntent
    data class SelectBranch(val branch: BranchPR) : WorkshopsIntent
}

sealed interface WorkshopsEvent {
    data class ShowToast(val message: String) : WorkshopsEvent
}

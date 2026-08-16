package com.tamin.taminhamrah.feature.orotezprotez.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezInsuredDetailUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezPicker
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState.PartialState
import com.tamin.taminhamrah.mapper.orotezProtez.toBranchWorkshopPresentationList
import com.tamin.taminhamrah.mapper.orotezProtez.toPresentation
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopPR
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonPR
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.orotezProtez.GetInsuredPersonsUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetRequestInsuredMainInfoUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.orotez_protez_insured_person_subtitle

class OrotezProtezViewModel(
    private val getRequestInsuredMainInfoUseCase: GetRequestInsuredMainInfoUseCase,
    private val getInsuredPersonsUseCase: GetInsuredPersonsUseCase,
) : BaseViewModel<OrotezProtezUiState, PartialState, OrotezProtezEvent, OrotezProtezIntent>(
    initialState = OrotezProtezUiState()
) {

    init {
        sendIntent(OrotezProtezIntent.LoadInitialData)
    }

    override fun handleIntent(intent: OrotezProtezIntent): Flow<PartialState> = when (intent) {
        is OrotezProtezIntent.LoadInitialData -> loadInitialData()

        is OrotezProtezIntent.OnPickerRequested -> flow {
            emit(PartialState.PickerChanged(intent.picker))
        }

        is OrotezProtezIntent.OnPickerDismissed -> flow {
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnBranchPicked -> flow {
            emit(PartialState.BranchSelected(intent.option))
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnInsuredPersonPicked -> flow {
            emit(PartialState.InsuredPersonSelected(intent.option))
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnPrescriptionDatePicked -> flow {
            emit(PartialState.PrescriptionDateSelected(intent.label))
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnNextStepClicked -> flow {
            if (uiState.value.canGoNext) {
                emit(PartialState.StepChanged(OrotezProtezStep.InsuredInfo))
            }
        }

        is OrotezProtezIntent.OnConfirmInsuredInfoClicked -> flow {
            emit(PartialState.StepChanged(OrotezProtezStep.Documents))
        }

        is OrotezProtezIntent.BackToPreviousStep -> handleBackStep()
    }

    private fun handleBackStep(): Flow<PartialState> = flow {
        when (uiState.value.currentStep) {
            OrotezProtezStep.Documents -> emit(PartialState.StepChanged(OrotezProtezStep.InsuredInfo))
            OrotezProtezStep.InsuredInfo -> emit(PartialState.StepChanged(OrotezProtezStep.UserSelection))
            OrotezProtezStep.UserSelection -> sendEvent(OrotezProtezEvent.NavigateBack)
        }
    }

    /** Step 1's branch/workshop sheet and insured-person sheet are both fed by the real API. */
    private fun loadInitialData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val branchOptions = getRequestInsuredMainInfoUseCase().first().toBranchOptions()
            val insuredPersons = getInsuredPersonsUseCase().first().map { it.toPresentation() }
            emit(
                PartialState.DataLoaded(
                    branch = branchOptions.firstOrNull(),
                    branchOptions = branchOptions,
                    insuredPersonOptions = insuredPersons.toOptionUiList(),
                    insuredPersonDetails = insuredPersons.toDetailUiMap(),
                )
            )
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    /** DN -> PR (core-ui) -> this feature's generic picker-row model, kept as three separate steps per the project's layering convention. */
    private fun RequestInsuredMainInfoDN?.toBranchOptions(): ImmutableList<OrotezProtezOptionUi> {
        return this?.toBranchWorkshopPresentationList()
            ?.map { it.toOptionUi() }
            ?.toPersistentList()
            ?: persistentListOf()
    }

    private fun BranchWorkshopPR.toOptionUi() = OrotezProtezOptionUi(id = id, label = label)

    private suspend fun List<InsuredPersonPR>.toOptionUiList(): ImmutableList<OrotezProtezOptionUi> {
        return map { person ->
            OrotezProtezOptionUi(
                id = person.id,
                label = person.label,
                subtitle = getString(Res.string.orotez_protez_insured_person_subtitle, person.id),
            )
        }.toPersistentList()
    }

    private fun List<InsuredPersonPR>.toDetailUiMap(): ImmutableMap<String, OrotezProtezInsuredDetailUi> {
        return associate { person ->
            person.id to OrotezProtezInsuredDetailUi(
                fullName = person.fullName,
                relation = person.relation,
                nationalCode = person.nationalCode,
                birthCertificateNumber = person.birthCertificateNumber,
                issuePlace = person.issuePlace,
                birthDateLabel = person.birthDateLabel,
                bookletValidUntilLabel = person.bookletValidUntilLabel,
            )
        }.toPersistentMap()
    }

    override fun reduceState(
        currentState: OrotezProtezUiState,
        partialState: PartialState
    ): OrotezProtezUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.DataLoaded -> currentState.copy(
            branch = partialState.branch,
            branchOptions = partialState.branchOptions,
            insuredPersonOptions = partialState.insuredPersonOptions,
            insuredPersonDetails = partialState.insuredPersonDetails,
        )
        is PartialState.PickerChanged -> currentState.copy(picker = partialState.picker)
        is PartialState.BranchSelected -> currentState.copy(branch = partialState.branch)
        is PartialState.InsuredPersonSelected -> currentState.copy(insuredPerson = partialState.insuredPerson)
        is PartialState.PrescriptionDateSelected -> currentState.copy(prescriptionDateLabel = partialState.label)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

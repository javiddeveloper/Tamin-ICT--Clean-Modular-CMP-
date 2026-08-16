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
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopPR
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.orotezProtez.GetRequestInsuredMainInfoUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OrotezProtezViewModel(
    private val getRequestInsuredMainInfoUseCase: GetRequestInsuredMainInfoUseCase,
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

    /** Step 1's branch/workshop sheet is fed by the real API; the insured-person sheet is still mocked. */
    private fun loadInitialData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        getRequestInsuredMainInfoUseCase()
            .map { info ->
                val branchOptions = info.toBranchOptions()
                PartialState.DataLoaded(
                    branch = branchOptions.firstOrNull(),
                    branchOptions = branchOptions,
                    insuredPersonOptions = MOCK_INSURED_PERSON_OPTIONS,
                    insuredPersonDetails = MOCK_INSURED_PERSON_DETAILS,
                ) as PartialState
            }
            .catch { emit(PartialState.Error(it.toSingleLineMessage())) }
            .collect { emit(it) }
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

    private companion object {
        val MOCK_INSURED_PERSON_OPTIONS = persistentListOf(
            OrotezProtezOptionUi(
                id = "insured-1",
                label = "اصلی (خود) - رضا دریکوند",
                subtitle = "شماره بیمه ۰۰۵۳۱۸۵۲۴۲",
            ),
            OrotezProtezOptionUi(
                id = "insured-2",
                label = "همسر - مریم دریکوند",
                subtitle = "شماره بیمه ۰۰۵۳۱۸۵۲۴۳",
            ),
            OrotezProtezOptionUi(
                id = "insured-3",
                label = "فرزند - امیرعلی دریکوند",
                subtitle = "شماره بیمه ۰۰۵۳۱۸۵۲۴۴",
            ),
        )

        // Step 2's whole read-only card is looked up from here by the step-1 selection's id.
        val MOCK_INSURED_PERSON_DETAILS = persistentMapOf(
            "insured-1" to OrotezProtezInsuredDetailUi(
                fullName = "رضا دریکوند",
                relation = "اصلی (خود)",
                nationalCode = "۴۰۶۰۴۳۴۰۶۱",
                birthCertificateNumber = "۴۰۶۰۴۳۴۰۶۱",
                issuePlace = "خرم آباد",
                birthDateLabel = "۱۳۷۰/۰۷/۱۳",
                bookletValidUntilLabel = "۱۴۰۵/۰۶/۱۵",
            ),
            "insured-2" to OrotezProtezInsuredDetailUi(
                fullName = "مریم دریکوند",
                relation = "همسر",
                nationalCode = "۴۰۶۰۴۳۴۰۶۲",
                birthCertificateNumber = "۴۰۶۰۴۳۴۰۶۲",
                issuePlace = "خرم آباد",
                birthDateLabel = "۱۳۷۲/۰۳/۰۲",
                bookletValidUntilLabel = "۱۴۰۵/۰۶/۱۵",
            ),
            "insured-3" to OrotezProtezInsuredDetailUi(
                fullName = "امیرعلی دریکوند",
                relation = "فرزند",
                nationalCode = "۴۰۶۰۴۳۴۰۶۳",
                birthCertificateNumber = "۴۰۶۰۴۳۴۰۶۳",
                issuePlace = "خرم آباد",
                birthDateLabel = "۱۳۹۸/۱۱/۲۰",
                bookletValidUntilLabel = "۱۴۰۵/۰۶/۱۵",
            ),
        )
    }
}

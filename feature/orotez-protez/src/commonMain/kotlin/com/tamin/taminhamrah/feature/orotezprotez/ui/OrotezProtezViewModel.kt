package com.tamin.taminhamrah.feature.orotezprotez.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezInsuredDetailUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezPicker
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState.PartialState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrotezProtezViewModel : BaseViewModel<OrotezProtezUiState, PartialState, OrotezProtezEvent, OrotezProtezIntent>(
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

    private fun loadInitialData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(
            PartialState.DataLoaded(
                branch = MOCK_BRANCH_OPTIONS.first(),
                branchOptions = MOCK_BRANCH_OPTIONS,
                insuredPersonOptions = MOCK_INSURED_PERSON_OPTIONS,
                insuredPersonDetails = MOCK_INSURED_PERSON_DETAILS,
            )
        )
        emit(PartialState.Loading(false))
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

    private companion object {
        val MOCK_BRANCH_OPTIONS = persistentListOf(
            OrotezProtezOptionUi(id = "branch-1", label = "یک تهران - شرکت ارد پارس اسپادانا"),
            OrotezProtezOptionUi(id = "branch-2", label = "دو کرج - کارگاه صنعتی البرز"),
            OrotezProtezOptionUi(id = "branch-3", label = "سه شیراز - دفتر مرکزی"),
        )
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

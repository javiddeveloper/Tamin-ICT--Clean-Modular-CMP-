package com.tamin.taminhamrah.feature.taminServices.occurrence

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSuccessModal
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceWarningBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step1PersonInfoStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step2WorkshopStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step3JobDetailsStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step4WorkHoursStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step5AccidentStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step6DocumentSubmitStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.ErrorSource
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceEvent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.PersonInfoStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrencePersonalInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.TaminFormAbandonDialog
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_title

private val STEPS_REQUIRING_EXIT_CONFIRMATION = setOf(
    OccurrenceStep.WORKSHOP_INFO,
    OccurrenceStep.JOB_DETAILS,
    OccurrenceStep.WORK_HOURS,
    OccurrenceStep.ACCIDENT_DETAILS,
    OccurrenceStep.DOCUMENT_SUBMIT,
)

@Composable
fun OccurrenceScreen(
    viewModel: OccurrenceViewModel = koinViewModel(),
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onIntent = viewModel::sendIntent

    val onExitRequested = remember(uiState.currentStep, uiState.dialogs, onBack) {
        {
            if (uiState.currentStep in STEPS_REQUIRING_EXIT_CONFIRMATION) {
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showExitConfirmation = true)))
            } else {
                onBack()
            }
        }
    }

    BackHandler(onBack = { viewModel.sendIntent(OccurrenceIntent.GoToPreviousStep) })

    if (uiState.dialogs.showExitConfirmation) {
        TaminFormAbandonDialog(
            formName = stringResource(Res.string.occurrence_title),
            onStay = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(showExitConfirmation = false),
                    ),
                )
            },
            onAbandon = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(showExitConfirmation = false),
                    ),
                )
                onBack()
            },
        )
    }

    HandleOccurrenceEvents(
        events = viewModel.events,
        onBackClicked = onBack,
    )

    OccurrenceContent(
        uiState = uiState,
        onBack = { viewModel.sendIntent(OccurrenceIntent.GoToPreviousStep) },
        onClose = onExitRequested,
        onIntent = onIntent,
    )

    if (uiState.dialogs.showWarningSheet) {
        OccurrenceWarningBottomSheet(
            onConfirm = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            showWarningSheet = false
                        )
                    )
                )
            },
            onDismiss = onBack,
        )
    }

    uiState.dialogs.successTrackingCode?.let { code ->
        OccurrenceSuccessModal(
            trackingCode = code,
            onDismiss = {
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(successTrackingCode = null)))
                onDone()
            },
        )
    }
}

@Composable
fun HandleOccurrenceEvents(
    events: Flow<OccurrenceEvent>,
    onBackClicked: () -> Unit,
) {
    val toaster = LocalToaster.current
    events.collectWithLifecycleAware(key = onBackClicked) { event ->
        when(event){
            is OccurrenceEvent.ShowToast -> toaster.error(event.message)
            is OccurrenceEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
internal fun OccurrenceContent(
    uiState: OccurrenceUiState,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onIntent: (OccurrenceIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState.currentStep) {
        OccurrenceStep.PERSON_INFO -> Step1PersonInfoStep(
            uiState = uiState,
            onIntent = onIntent,
            onBack = onBack,
            modifier = modifier,
            error = uiState.errors[ErrorSource.USER_INFO]
        )

        OccurrenceStep.WORKSHOP_INFO -> Step2WorkshopStep(
            uiState = uiState,
            onIntent = onIntent,
            onBack = onBack,
            modifier = modifier,
            onClose = onClose,
            error = uiState.errors[ErrorSource.WORKSHOPS]
        )

        OccurrenceStep.JOB_DETAILS -> Step3JobDetailsStep(
            uiState = uiState,
            onIntent = onIntent,
            onBack = onBack,
            onClose = onClose,
            modifier = modifier,
            error = uiState.errors[ErrorSource.INSURED_RELATION]
        )

        OccurrenceStep.WORK_HOURS -> Step4WorkHoursStep(
            uiState = uiState,
            onIntent = onIntent,
            onBack = onBack,
            onClose = onClose,
            modifier = modifier
        )

        OccurrenceStep.ACCIDENT_DETAILS -> Step5AccidentStep(
            uiState = uiState,
            onIntent = onIntent,
            onBack = onBack,
            onClose = onClose,
            modifier = modifier
        )

        OccurrenceStep.DOCUMENT_SUBMIT -> Step6DocumentSubmitStep(
            uiState = uiState,
            onIntent = onIntent,
            onBack = onBack,
            onClose = onClose,
            modifier = modifier
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun OccurrenceContentStep1Preview() {
    PreviewRtlThemeContent {
        OccurrenceContent(
            uiState = OccurrenceUiState(
                currentStep = OccurrenceStep.PERSON_INFO,
                personInfo = PersonInfoStepState(
                    personalInfo = OccurrencePersonalInfoPR(
                        nationalCode = "0012345678",
                        firstName = "علی",
                        lastName = "محمدی",
                        fatherName = "رضا",
                        gender = "مرد",
                        birthDate = "1370/01/15",
                        insuranceNumber = "12345",
                        branchCode = "001",
                        nationality = "ایرانی",
                        insuranceType = "اجباری",
                    ),
                    birthDate = "1370/01/15",
                ),
            ),
            onBack = {},
            onIntent = {},
            onClose = {},
        )
    }
}

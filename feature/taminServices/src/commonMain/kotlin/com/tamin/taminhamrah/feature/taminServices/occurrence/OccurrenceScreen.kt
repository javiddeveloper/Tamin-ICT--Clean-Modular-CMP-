package com.tamin.taminhamrah.feature.taminServices.occurrence

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceEvent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.PersonInfoStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrencePersonalInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OccurrenceScreen(
    viewModel: OccurrenceViewModel = koinViewModel(),
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    var showWarningSheet by remember { mutableStateOf(true) }
    var successTrackingCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(OccurrenceIntent.LoadInitialData)
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is OccurrenceEvent.ShowToast -> toaster.error(event.message)
            is OccurrenceEvent.DisplaySuccessModal -> successTrackingCode = event.trackingCode
            is OccurrenceEvent.NavigateBack -> onBack()
        }
    }

    OccurrenceContent(
        uiState = uiState,
        onBack = { viewModel.sendIntent(OccurrenceIntent.GoToPreviousStep) },
        onIntent = viewModel::sendIntent,
    )

    if (showWarningSheet) {
        OccurrenceWarningBottomSheet(
            onConfirm = { showWarningSheet = false },
            onDismiss = onBack,
        )
    }

    successTrackingCode?.let { code ->
        OccurrenceSuccessModal(
            trackingCode = code,
            onDismiss = {
                successTrackingCode = null
                onDone()
            },
        )
    }
}

@Composable
internal fun OccurrenceContent(
    uiState: OccurrenceUiState,
    onBack: () -> Unit,
    onIntent: (OccurrenceIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState.currentStep) {
        OccurrenceStep.PERSON_INFO -> Step1PersonInfoStep(uiState = uiState, onIntent = onIntent, onBack = onBack, modifier = modifier)
        OccurrenceStep.WORKSHOP_INFO -> Step2WorkshopStep(uiState = uiState, onIntent = onIntent, onBack = onBack, modifier = modifier)
        OccurrenceStep.JOB_DETAILS -> Step3JobDetailsStep(uiState = uiState, onIntent = onIntent, onBack = onBack, modifier = modifier)
        OccurrenceStep.WORK_HOURS -> Step4WorkHoursStep(uiState = uiState, onIntent = onIntent, onBack = onBack, modifier = modifier)
        OccurrenceStep.ACCIDENT_DETAILS -> Step5AccidentStep(uiState = uiState, onIntent = onIntent, onBack = onBack, modifier = modifier)
        OccurrenceStep.DOCUMENT_SUBMIT -> Step6DocumentSubmitStep(uiState = uiState, onIntent = onIntent, onBack = onBack, modifier = modifier)
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
        )
    }
}

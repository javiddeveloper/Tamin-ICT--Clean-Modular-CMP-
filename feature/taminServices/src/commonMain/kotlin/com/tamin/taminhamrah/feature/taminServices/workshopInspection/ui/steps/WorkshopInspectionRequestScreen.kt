package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.steps

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestSuccessDialog
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.components.topbars.TaminTopAppBar
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.close_content_description
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.inspection_request_objection_title
import taminx.core.core_ui.inspection_request_step1_label
import taminx.core.core_ui.inspection_request_step2_label
import taminx.core.core_ui.inspection_request_step3_label

/**
 * The "ثبت اعتراض به بازرسی" wizard for the employer/workshop list. Always an objection request —
 * this screen has no "request a new inspection" entry point (see
 * [com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent.OpenRequestFlow]).
 * Ported from [com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps.InspectionRequestScreen]
 * onto [WorkshopInspectionUiState]/[WorkshopInspectionIntent] — see that file's doc comment for why
 * this isn't a shared composable (the user chose separate screens for the two flows).
 */
@Composable
internal fun WorkshopInspectionRequestScreen(
    uiState: WorkshopInspectionUiState,
    onIntent: (WorkshopInspectionIntent) -> Unit,
    onClose: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val step1Label = stringResource(Res.string.inspection_request_step1_label)
    val step2Label = stringResource(Res.string.inspection_request_step2_label)
    val step3Label = stringResource(Res.string.inspection_request_step3_label)
    val title = stringResource(Res.string.inspection_request_objection_title)
    val onBack = { onIntent(WorkshopInspectionIntent.GoToPreviousRequestStep) }

    val steps = remember(uiState.requestStep) {
        persistentListOf(
            StepIndicatorModel(
                title = step1Label,
                stepNumber = "1".toPersianDigits(),
                state = uiState.requestStep.toStepState(InspectionRequestStep.IDENTITY_CONTACT),
            ),
            StepIndicatorModel(
                title = step2Label,
                stepNumber = "2".toPersianDigits(),
                state = uiState.requestStep.toStepState(InspectionRequestStep.WORKSHOP_INFO),
            ),
            StepIndicatorModel(
                title = step3Label,
                stepNumber = "3".toPersianDigits(),
                state = uiState.requestStep.toStepState(InspectionRequestStep.REQUEST_DESCRIPTION),
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        Surface(color = taminColors.bgSurface, shadowElevation = 0.dp) {
            Column(modifier = Modifier.statusBarsPadding()) {
                TaminTopAppBar(
                    title = {
                        TaminText(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = null,
                            tint = taminColors.textPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    onNavigationClick = onBack,
                    actionIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.close_content_description),
                            tint = taminColors.textPrimary,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    onActionClick = onClose,
                )
            }
        }

        StepIndicator(
            steps = steps,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        )

        AnimatedContent(
            targetState = uiState.requestStep,
            transitionSpec = {
                val direction = if (targetState.ordinal > initialState.ordinal) {
                    AnimatedContentTransitionScope.SlideDirection.Start
                } else {
                    AnimatedContentTransitionScope.SlideDirection.End
                }
                slideIntoContainer(direction, animationSpec = tween(300)) togetherWith
                    slideOutOfContainer(direction, animationSpec = tween(300))
            },
            label = "WorkshopInspectionRequestStepTransition",
            modifier = Modifier.weight(1f),
        ) { step ->
            when (step) {
                InspectionRequestStep.IDENTITY_CONTACT -> WorkshopIdentityContactStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onBack = onBack,
                    error = uiState.requestErrors[InspectionRequestErrorSource.USER_INFO],
                )
                InspectionRequestStep.WORKSHOP_INFO -> WorkshopWorkshopInfoStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onBack = onBack,
                    error = uiState.requestErrors[InspectionRequestErrorSource.JOBS],
                )
                InspectionRequestStep.REQUEST_DESCRIPTION -> WorkshopRequestDescriptionStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onBack = onBack,
                )
            }
        }
    }

    if (uiState.isSubmitted) {
        InspectionRequestSuccessDialog(
            isObjection = true,
            branchName = uiState.workshopInfo.branchName,
            trackingId = uiState.submittedTrackingId,
            onDismiss = { onIntent(WorkshopInspectionIntent.CloseRequestFlow) },
        )
    }
}

private fun InspectionRequestStep.toStepState(target: InspectionRequestStep): StepState = when {
    ordinal > target.ordinal -> StepState.Completed
    ordinal == target.ordinal -> StepState.Active
    else -> StepState.Inactive
}

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionRequestScreenPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkshopInspectionRequestScreen(
                uiState = WorkshopInspectionUiState(requestStep = InspectionRequestStep.REQUEST_DESCRIPTION),
                onIntent = {},
                onClose = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionRequestScreenDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            WorkshopInspectionRequestScreen(
                uiState = WorkshopInspectionUiState(requestStep = InspectionRequestStep.REQUEST_DESCRIPTION),
                onIntent = {},
                onClose = {},
            )
        }
    }
}

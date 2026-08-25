package com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestSuccessDialog
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
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
import taminx.core.core_ui.inspection_request_source_chip_format
import taminx.core.core_ui.inspection_request_step1_label
import taminx.core.core_ui.inspection_request_step2_label
import taminx.core.core_ui.inspection_request_step3_label
import taminx.core.core_ui.inspection_title

/**
 * The "درخواست بازرسی" / "ثبت اعتراض" request wizard. Driven entirely by the shared
 * [InspectionUiState]/[InspectionIntent] — this is a screen, not a separate ViewModel/nav
 * destination, so it and [com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionScreen]
 * always agree on one source of truth (see `InspectionViewModel.handleOpenRequestFlow`).
 *
 * Top bar and step indicator are intentionally NOT mirrored from occurrence's per-step
 * `OccurrenceStepScaffold` — this wizard keeps its own single shared [TaminTopAppBar] + [StepIndicator]
 * here, with only the bottom action bar owned per-step (see [IdentityContactStep]/[WorkshopInfoStep]/
 * [RequestDescriptionStep] and [com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestStepScaffold]).
 */
@Composable
internal fun InspectionRequestScreen(
    uiState: InspectionUiState,
    onIntent: (InspectionIntent) -> Unit,
    onClose: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val step1Label = stringResource(Res.string.inspection_request_step1_label)
    val step2Label = stringResource(Res.string.inspection_request_step2_label)
    val step3Label = stringResource(Res.string.inspection_request_step3_label)
    val title = stringResource(
        if (uiState.isObjectionRequest) Res.string.inspection_request_objection_title else Res.string.inspection_title
    )
    val onBack = { onIntent(InspectionIntent.GoToPreviousRequestStep) }

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

        // Part of the screen body, not the top bar — its own section on the page background,
        // not the top bar's Surface/bgSurface container.
        StepIndicator(
            steps = steps,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        )

        AnimatedContent(
            targetState = uiState.requestStep,
            transitionSpec = {
                // Layout-direction-aware: Start/End already resolve correctly under the app's
                // hardcoded RTL, so no manual mirroring is needed here.
                val direction = if (targetState.ordinal > initialState.ordinal) {
                    AnimatedContentTransitionScope.SlideDirection.Start
                } else {
                    AnimatedContentTransitionScope.SlideDirection.End
                }
                slideIntoContainer(direction, animationSpec = tween(300)) togetherWith
                    slideOutOfContainer(direction, animationSpec = tween(300))
            },
            label = "InspectionRequestStepTransition",
            modifier = Modifier.weight(1f),
        ) { step ->
            when (step) {
                InspectionRequestStep.IDENTITY_CONTACT -> IdentityContactStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onBack = onBack,
                    error = uiState.requestErrors[InspectionRequestErrorSource.USER_INFO],
                )
                InspectionRequestStep.WORKSHOP_INFO -> WorkshopInfoStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onBack = onBack,
                    // Branches and jobs fail independently — show whichever error is present; retry
                    // (wired inside WorkshopInfoStep) re-issues only the call(s) that actually failed.
                    error = uiState.requestErrors[InspectionRequestErrorSource.BRANCHES]
                        ?: uiState.requestErrors[InspectionRequestErrorSource.JOBS],
                )
                InspectionRequestStep.REQUEST_DESCRIPTION -> RequestDescriptionStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onBack = onBack,
                )
            }
        }
    }

    if (uiState.isSubmitted) {
        InspectionRequestSuccessDialog(
            isObjection = uiState.isObjectionRequest,
            branchName = uiState.workshopInfo.branchName,
            trackingId = uiState.submittedTrackingId,
            onDismiss = { onIntent(InspectionIntent.CloseRequestFlow) },
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
private fun InspectionRequestScreenPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            InspectionRequestScreen(
                uiState = InspectionUiState(requestStep = InspectionRequestStep.REQUEST_DESCRIPTION),
                onIntent = {},
                onClose = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionRequestScreenDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            InspectionRequestScreen(
                uiState = InspectionUiState(requestStep = InspectionRequestStep.REQUEST_DESCRIPTION),
                onIntent = {},
                onClose = {},
            )
        }
    }
}

package com.tamin.taminhamrah.feature.orotezprotez.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezHeader
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezOptionSheet
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezPicker
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePickerBottomSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.ic_home_menu
import taminx.core.core_ui.orotez_protez_field_branch_placeholder
import taminx.core.core_ui.orotez_protez_field_insured_person
import taminx.core.core_ui.orotez_protez_field_prescription_date
import taminx.core.core_ui.orotez_protez_next_step
import taminx.core.core_ui.orotez_protez_pick_branch_subtitle
import taminx.core.core_ui.orotez_protez_pick_branch_title
import taminx.core.core_ui.orotez_protez_pick_insured_person_subtitle
import taminx.core.core_ui.orotez_protez_pick_insured_person_title
import taminx.core.core_ui.orotez_protez_step_documents
import taminx.core.core_ui.orotez_protez_step_info
import taminx.core.core_ui.orotez_protez_step_user
import taminx.core.core_ui.orotez_protez_step_user_description
import taminx.core.core_ui.orotez_protez_step_user_title
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import taminx.core.core_ui.ic_branch

@Composable
fun OrotezProtezScreen(
    viewModel: OrotezProtezViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    OrotezProtezContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
private fun OrotezProtezContent(
    modifier: Modifier = Modifier,
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            OrotezProtezHeader(onBackClicked = onBackClicked)
            OrotezProtezUserStep(
                state = state,
                onIntent = onIntent,
            )
        }
        if (state.isLoading) {
            LoadingStateOverlay()
        }
    }

    when (state.picker) {
        OrotezProtezPicker.BRANCH -> OrotezProtezOptionSheet(
            title = stringResource(Res.string.orotez_protez_pick_branch_title),
            subtitle = stringResource(Res.string.orotez_protez_pick_branch_subtitle),
            options = state.branchOptions,
            selectedId = state.branch?.id,
            onSelect = { onIntent(OrotezProtezIntent.OnBranchPicked(it)) },
            onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
        )

        OrotezProtezPicker.INSURED_PERSON -> OrotezProtezOptionSheet(
            title = stringResource(Res.string.orotez_protez_pick_insured_person_title),
            subtitle = stringResource(Res.string.orotez_protez_pick_insured_person_subtitle),
            options = state.insuredPersonOptions,
            selectedId = state.insuredPerson?.id,
            onSelect = { onIntent(OrotezProtezIntent.OnInsuredPersonPicked(it)) },
            onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
        )

        OrotezProtezPicker.DATE -> TaminJalaliDatePickerBottomSheet(
            title = stringResource(Res.string.orotez_protez_field_prescription_date),
            onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(
                    OrotezProtezIntent.OnPrescriptionDatePicked(
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )

        OrotezProtezPicker.NONE -> Unit
    }

    ErrorStateView(
        message = state.error,
        onDismiss = onBackClicked,
        onRetry = { onIntent(OrotezProtezIntent.LoadInitialData) },
    )
}

/** Step 1 of the wizard: who the branch, insured person and prescription date are for. */
@Composable
private fun OrotezProtezUserStep(
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        StepIndicator(
            steps = persistentListOf(
                StepIndicatorModel(stringResource(Res.string.orotez_protez_step_user), "۱", StepState.Active),
                StepIndicatorModel(stringResource(Res.string.orotez_protez_step_info), "۲", StepState.Inactive),
                StepIndicatorModel(stringResource(Res.string.orotez_protez_step_documents), "۳", StepState.Inactive),
            ),
        )

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = stringResource(Res.string.orotez_protez_step_user_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = stringResource(Res.string.orotez_protez_step_user_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Spacer(Modifier.height(Spacing.lg))

        PickerRow(
            text = state.branch?.label ?: stringResource(Res.string.orotez_protez_field_branch_placeholder),
            isPlaceholder = state.branch == null,
            icon = vectorResource(Res.drawable.ic_branch),
            iconTint = colors.blueText,
            showChevron = true,
            onClick = { onIntent(OrotezProtezIntent.OnPickerRequested(OrotezProtezPicker.BRANCH)) },
        )
        Spacer(Modifier.height(Spacing.sm))

        PickerRow(
            text = state.insuredPerson?.label ?: stringResource(Res.string.orotez_protez_field_insured_person),
            isPlaceholder = state.insuredPerson == null,
            icon = vectorResource(Res.drawable.ic_tamin_user),
            iconTint = colors.blueText,
            showChevron = true,
            onClick = { onIntent(OrotezProtezIntent.OnPickerRequested(OrotezProtezPicker.INSURED_PERSON)) },
        )
        Spacer(Modifier.height(Spacing.sm))

        PickerRow(
            text = state.prescriptionDateLabel ?: stringResource(Res.string.orotez_protez_field_prescription_date),
            isPlaceholder = state.prescriptionDateLabel == null,
            icon = vectorResource(Res.drawable.ic_tamin_calendar),
            iconTint = colors.blueText,
            onClick = { onIntent(OrotezProtezIntent.OnPickerRequested(OrotezProtezPicker.DATE)) },
        )

        Spacer(Modifier.height(Spacing.xl))

        LoadingButton(
            text = stringResource(Res.string.orotez_protez_next_step),
            onClick = { onIntent(OrotezProtezIntent.OnNextStepClicked) },
            enabled = state.canGoNext,
            icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezScreenLight() {
    PreviewRtlThemeContent {
        OrotezProtezContent(
            state = PreviewState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezScreenDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        OrotezProtezContent(
            state = PreviewState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

private val PreviewState = OrotezProtezUiState(
    branch = OrotezProtezOptionUi(
        id = "branch-1",
        label = "یک تهران - شرکت ارد پارس اسپادانا",
    ),
)

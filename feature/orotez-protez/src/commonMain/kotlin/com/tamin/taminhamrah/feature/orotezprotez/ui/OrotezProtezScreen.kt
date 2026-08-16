package com.tamin.taminhamrah.feature.orotezprotez.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezHeader
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezOptionSheet
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezInsuredDetailUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezPicker
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePickerBottomSheet
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.ic_home_menu
import taminx.core.core_ui.orotez_protez_confirm_and_continue
import taminx.core.core_ui.orotez_protez_detail_birth_certificate_number
import taminx.core.core_ui.orotez_protez_detail_birth_date
import taminx.core.core_ui.orotez_protez_detail_booklet_valid_until
import taminx.core.core_ui.orotez_protez_detail_full_name
import taminx.core.core_ui.orotez_protez_detail_issue_place
import taminx.core.core_ui.orotez_protez_detail_national_code
import taminx.core.core_ui.orotez_protez_detail_relation
import taminx.core.core_ui.orotez_protez_documents_placeholder
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
import taminx.core.core_ui.orotez_protez_step_info_description
import taminx.core.core_ui.orotez_protez_step_info_title
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

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            OrotezProtezEvent.NavigateBack -> onBackClicked()
        }
    }

    // Step 1's back leaves the feature; step 2/3's back returns to the previous step, keeping state.
    val handleBack: () -> Unit = {
        if (uiState.currentStep == OrotezProtezStep.UserSelection) {
            onBackClicked()
        } else {
            viewModel.sendIntent(OrotezProtezIntent.BackToPreviousStep)
        }
    }

    OrotezProtezContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = handleBack,
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

            OrotezProtezStepIndicator(
                currentStep = state.currentStep,
                modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.lg),
            )

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.index > initialState.index) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "OrotezProtezStepTransition",
            ) { step ->
                when (step) {
                    OrotezProtezStep.UserSelection -> OrotezProtezUserStep(
                        state = state,
                        onIntent = onIntent,
                    )

                    OrotezProtezStep.InsuredInfo -> OrotezProtezInsuredInfoStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = onBackClicked,
                    )

                    OrotezProtezStep.Documents -> OrotezProtezDocumentsStep()
                }
            }
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

/** The stepper header, shared across all three steps and reflecting the real flow position. */
@Composable
private fun OrotezProtezStepIndicator(
    currentStep: OrotezProtezStep,
    modifier: Modifier = Modifier,
) {
    val currentIndex = currentStep.index

    StepIndicator(
        modifier = modifier,
        steps = persistentListOf(
            StepIndicatorModel(
                title = stringResource(Res.string.orotez_protez_step_user),
                stepNumber = "۱",
                state = when {
                    currentIndex == 0 -> StepState.Active
                    currentIndex > 0 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.orotez_protez_step_info),
                stepNumber = "۲",
                state = when {
                    currentIndex == 1 -> StepState.Active
                    currentIndex > 1 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.orotez_protez_step_documents),
                stepNumber = "۳",
                state = if (currentIndex == 2) StepState.Active else StepState.Inactive,
            ),
        ),
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

/**
 * Step 2 of the wizard: a read-only summary of the insured person selected in step 1.
 * Every value here is looked up from [OrotezProtezUiState.selectedInsuredDetail] — nothing is
 * re-entered or stored independently.
 */
@Composable
private fun OrotezProtezInsuredInfoStep(
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val detail = state.selectedInsuredDetail

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.orotez_protez_step_info_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = stringResource(Res.string.orotez_protez_step_info_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Spacer(Modifier.height(Spacing.lg))

        if (detail != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            ) {
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_full_name),
                    value = detail.fullName,
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_relation),
                    value = detail.relation,
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_national_code),
                    value = detail.nationalCode,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_birth_certificate_number),
                    value = detail.birthCertificateNumber,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_issue_place),
                    value = detail.issuePlace,
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_birth_date),
                    value = detail.birthDateLabel,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.orotez_protez_detail_booklet_valid_until),
                    value = detail.bookletValidUntilLabel,
                )
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OrotezProtezBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.orotez_protez_confirm_and_continue),
                onClick = { onIntent(OrotezProtezIntent.OnConfirmInsuredInfoClicked) },
                enabled = detail != null,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            )
        }
    }
}

/** Square outline button placed next to the primary action, for a one-tap return to the previous step. */
@Composable
private fun OrotezProtezBackStepButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)

    Box(
        modifier = modifier
            .size(ButtonDimens.height)
            .clip(shape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = colors.textPrimary,
        )
    }
}

/** Step 3 placeholder: document upload UI is not designed yet. */
@Composable
private fun OrotezProtezDocumentsStep() {
    TaminEmptyState(
        message = stringResource(Res.string.orotez_protez_documents_placeholder),
        modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.lg),
    )
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

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezInsuredInfoStepLight() {
    PreviewRtlThemeContent {
        OrotezProtezContent(
            state = PreviewInsuredInfoState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezInsuredInfoStepDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        OrotezProtezContent(
            state = PreviewInsuredInfoState,
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

private val PreviewInsuredInfoState = OrotezProtezUiState(
    currentStep = OrotezProtezStep.InsuredInfo,
    branch = OrotezProtezOptionUi(
        id = "branch-2",
        label = "دو کرج - کارگاه صنعتی البرز",
    ),
    insuredPerson = OrotezProtezOptionUi(
        id = "insured-1",
        label = "اصلی (خود) - رضا دریکوند",
        subtitle = "شماره بیمه ۰۰۵۳۱۸۵۲۴۲",
    ),
    prescriptionDateLabel = "۱۴۰۵/۰۵/۲۵",
    insuredPersonDetails = persistentMapOf(
        "insured-1" to OrotezProtezInsuredDetailUi(
            fullName = "رضا دریکوند",
            relation = "اصلی (خود)",
            nationalCode = "۴۰۶۰۴۳۴۰۶۱",
            birthCertificateNumber = "۴۰۶۰۴۳۴۰۶۱",
            issuePlace = "خرم آباد",
            birthDateLabel = "۱۳۷۰/۰۷/۱۳",
            bookletValidUntilLabel = "۱۴۰۵/۰۶/۱۵",
        ),
    ),
)

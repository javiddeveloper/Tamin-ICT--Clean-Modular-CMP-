package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.components.IssuanceCertificateHeader
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.components.IssuanceCertificateRecipientSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictPensionerSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.issuance_certificate_branch_name_placeholder
import taminx.core.core_ui.issuance_certificate_certificate_type_value
import taminx.core.core_ui.issuance_certificate_confirm_disclaimer
import taminx.core.core_ui.issuance_certificate_confirm_title
import taminx.core.core_ui.issuance_certificate_copy
import taminx.core.core_ui.issuance_certificate_destination_value
import taminx.core.core_ui.issuance_certificate_detail_branch
import taminx.core.core_ui.issuance_certificate_detail_certificate_type
import taminx.core.core_ui.issuance_certificate_detail_destination
import taminx.core.core_ui.issuance_certificate_detail_full_name
import taminx.core.core_ui.issuance_certificate_detail_pensioner_number
import taminx.core.core_ui.issuance_certificate_detail_recipient
import taminx.core.core_ui.issuance_certificate_disclaimer
import taminx.core.core_ui.issuance_certificate_info_title
import taminx.core.core_ui.issuance_certificate_next_step
import taminx.core.core_ui.issuance_certificate_pensioner_number_label
import taminx.core.core_ui.issuance_certificate_recipient_placeholder
import taminx.core.core_ui.issuance_certificate_step_confirm
import taminx.core.core_ui.issuance_certificate_step_info
import taminx.core.core_ui.issuance_certificate_submit_and_send
import taminx.core.core_ui.issuance_certificate_success_confirm
import taminx.core.core_ui.issuance_certificate_success_desc
import taminx.core.core_ui.issuance_certificate_success_title

@Composable
fun IssuanceCertificateScreen(
    onBack: () -> Unit,
    viewModel: IssuanceCertificateViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    HandleIssuanceCertificateEvents(
        events = viewModel.events,
        onNavigateBack = onBack,
    )

    IssuanceCertificateContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
    )

    if (state.showPensionerSheet) {
        EdictPensionerSheet(
            pensionerIds = state.pensionerIds.map { it.pensionerId },
            selectedId = state.selectedPensionerId,
            onSelect = { id -> viewModel.sendIntent(IssuanceCertificateIntent.SelectPensionerId(id)) },
            onDismiss = { viewModel.sendIntent(IssuanceCertificateIntent.DismissPensionerSheet) },
        )
    }

    if (state.showRecipientsSheet) {
        IssuanceCertificateRecipientSheet(
            searchQuery = state.searchQuery,
            isLoading = state.isLoadingRecipients,
            recipients = state.filteredRecipients,
            onSearchQueryChange = { viewModel.sendIntent(IssuanceCertificateIntent.SearchRecipients(it)) },
            onRecipientSelected = { viewModel.sendIntent(IssuanceCertificateIntent.SelectRecipient(it)) },
            onDismiss = { viewModel.sendIntent(IssuanceCertificateIntent.DismissRecipientsSheet) },
        )
    }

    if (state.showSuccessDialog) {
        val colors = LocalTaminColors.current
        TaminConfirmationDialog(
            title = stringResource(Res.string.issuance_certificate_success_title),
            description = stringResource(Res.string.issuance_certificate_success_desc),
            icon = Icons.Default.Check,
            iconTint = colors.greenText,
            iconBackground = colors.greenBg,
            onDismissRequest = { viewModel.sendIntent(IssuanceCertificateIntent.DismissSuccessDialog) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.issuance_certificate_success_confirm),
                    onClick = { viewModel.sendIntent(IssuanceCertificateIntent.DismissSuccessDialog) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }
}

@Composable
private fun HandleIssuanceCertificateEvents(
    events: Flow<IssuanceCertificateEvent>,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is IssuanceCertificateEvent.NavigateBack -> onNavigateBack()
            is IssuanceCertificateEvent.ShowToast -> Unit
        }
    }
}

@Composable
private fun IssuanceCertificateContent(
    state: IssuanceCertificateUiState,
    onIntent: (IssuanceCertificateIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val handleBack: () -> Unit = {
        if (state.currentStep == IssuanceCertificateStep.Info) {
            onBack()
        } else {
            onIntent(IssuanceCertificateIntent.GoToPreviousStep)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IssuanceCertificateHeader(onBackClicked = handleBack)

            IssuanceCertificateStepIndicator(
                currentStep = state.currentStep,
                modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.lg),
            )

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "IssuanceCertificateStepTransition",
                modifier = Modifier.weight(1f),
            ) { step ->
                when (step) {
                    IssuanceCertificateStep.Info -> IssuanceCertificateInfoStep(
                        state = state,
                        onIntent = onIntent,
                    )

                    IssuanceCertificateStep.Confirm -> IssuanceCertificateConfirmStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = handleBack,
                    )
                }
            }
        }
    }
}

@Composable
private fun IssuanceCertificateStepIndicator(
    currentStep: IssuanceCertificateStep,
    modifier: Modifier = Modifier,
) {
    val currentIndex = currentStep.ordinal

    StepIndicator(
        modifier = modifier,
        steps = persistentListOf(
            StepIndicatorModel(
                title = stringResource(Res.string.issuance_certificate_step_info),
                stepNumber = "۱",
                state = when {
                    currentIndex == 0 -> StepState.Active
                    currentIndex > 0 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.issuance_certificate_step_confirm),
                stepNumber = "۲",
                state = if (currentIndex == 1) StepState.Active else StepState.Inactive,
            ),
        ),
    )
}

@Composable
private fun IssuanceCertificateInfoStep(
    state: IssuanceCertificateUiState,
    onIntent: (IssuanceCertificateIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page),
        ) {
            Text(
                text = stringResource(Res.string.issuance_certificate_info_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )

            Spacer(Modifier.height(Spacing.lg))

            PensionerNumberCard(
                pensionerId = state.selectedPensionerId,
                canSwitch = state.pensionerIds.size > 1,
                onSwitchClicked = { onIntent(IssuanceCertificateIntent.ShowPensionerSheet) },
            )
            if (state.pensionerIdError != null) {
                FieldErrorText(state.pensionerIdError)
            }

            Spacer(Modifier.height(Spacing.sm))

            PickerRow(
                text = state.selectedRecipient?.name
                    ?: stringResource(Res.string.issuance_certificate_recipient_placeholder),
                isPlaceholder = state.selectedRecipient == null,
                isError = state.recipientError != null,
                showChevron = false,
                onClick = { onIntent(IssuanceCertificateIntent.ShowRecipientsSheet) },
            )
            if (state.recipientError != null) {
                FieldErrorText(state.recipientError)
            }

            Spacer(Modifier.height(Spacing.sm))

            TaminTextField(
                value = state.branchName,
                onValueChange = { onIntent(IssuanceCertificateIntent.ChangeBranchName(it)) },
                placeholder = stringResource(Res.string.issuance_certificate_branch_name_placeholder),
                isError = state.branchNameError != null,
                errorMessage = state.branchNameError,
            )

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = stringResource(Res.string.issuance_certificate_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.md)
                .navigationBarsPadding(),
        ) {
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.issuance_certificate_next_step),
                onClick = { onIntent(IssuanceCertificateIntent.GoToNextStep) },
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconPosition = LoadingButtonIconPosition.TRAILING,
            )
        }
    }
}

@Composable
private fun FieldErrorText(message: String) {
    val colors = LocalTaminColors.current
    Text(
        text = message,
        style = MaterialTheme.typography.labelSmall,
        color = colors.dangerText,
        modifier = Modifier.padding(top = Spacing.xxs, start = Spacing.sm),
    )
}

@Composable
private fun PensionerNumberCard(
    pensionerId: String?,
    canSwitch: Boolean,
    onSwitchClicked: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val displayValue = pensionerId.orEmpty().toPersianDigits()
    val copyAction = pensionerId?.let { rememberCopyAction(it) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(Res.string.issuance_certificate_pensioner_number_label),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.xxs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                NumericText(
                    text = displayValue,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                if (canSwitch) {
                    Spacer(Modifier.width(Spacing.sm))
                    Box(
                        modifier = Modifier
                            .size(IconSize.textFieldIconContainer)
                            .clip(RoundedCornerShape(CornerRadius.md))
                            .background(colors.blueBg)
                            .clickable(onClick = onSwitchClicked),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Badge,
                            contentDescription = stringResource(Res.string.issuance_certificate_pensioner_number_label),
                            tint = colors.blueText,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                }
            }
        }

        if (copyAction != null) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.max))
                    .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.max))
                    .clickable(onClick = copyAction)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_copy),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.small),
                )
                Text(
                    text = stringResource(Res.string.issuance_certificate_copy),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun IssuanceCertificateConfirmStep(
    state: IssuanceCertificateUiState,
    onIntent: (IssuanceCertificateIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page),
        ) {
            Text(
                text = stringResource(Res.string.issuance_certificate_confirm_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )

            Spacer(Modifier.height(Spacing.lg))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            ) {
                DetailRow(
                    label = stringResource(Res.string.issuance_certificate_detail_full_name),
                    value = state.fullName,
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.issuance_certificate_detail_pensioner_number),
                    value = state.selectedPensionerId.orEmpty(),
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.issuance_certificate_detail_certificate_type),
                    value = stringResource(Res.string.issuance_certificate_certificate_type_value),
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.issuance_certificate_detail_recipient),
                    value = state.selectedRecipient?.name.orEmpty(),
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.issuance_certificate_detail_branch),
                    value = state.branchName,
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.issuance_certificate_detail_destination),
                    value = stringResource(Res.string.issuance_certificate_destination_value),
                    numeric = false,
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = stringResource(Res.string.issuance_certificate_confirm_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.md)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            IssuanceCertificateBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.issuance_certificate_submit_and_send),
                onClick = { onIntent(IssuanceCertificateIntent.SubmitRequest) },
                isLoading = state.isSubmitting,
            )
        }
    }
}

@Composable
private fun IssuanceCertificateBackStepButton(
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
            .border(1.dp, colors.border, shape)
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

@PreviewRtlTheme
@Composable
private fun PreviewIssuanceCertificateInfoStepLight() {
    PreviewRtlThemeContent {
        IssuanceCertificateContent(state = PreviewInfoState, onIntent = {}, onBack = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewIssuanceCertificateInfoStepDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        IssuanceCertificateContent(state = PreviewInfoState, onIntent = {}, onBack = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewIssuanceCertificateConfirmStepLight() {
    PreviewRtlThemeContent {
        IssuanceCertificateContent(state = PreviewConfirmState, onIntent = {}, onBack = {})
    }
}

private val PreviewInfoState = IssuanceCertificateUiState(
    fullName = "سیدرحمت اله میرفضلی",
    selectedPensionerId = "1003406938",
    branchName = "",
)

private val PreviewConfirmState = PreviewInfoState.copy(
    currentStep = IssuanceCertificateStep.Confirm,
    branchName = "شعبه مرکزی",
)

package com.tamin.taminhamrah.feature.weddingPresent.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.weddingPresent.ui.components.WeddingPresentHeader
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentEvent
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentIntent
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentUiState
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminCheckBox
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.wedding_present_commitment_body
import taminx.core.core_ui.wedding_present_details_section
import taminx.core.core_ui.wedding_present_hide_details
import taminx.core.core_ui.wedding_present_label_bank_account
import taminx.core.core_ui.wedding_present_label_bank_name
import taminx.core.core_ui.wedding_present_label_branch
import taminx.core.core_ui.wedding_present_label_full_name
import taminx.core.core_ui.wedding_present_label_insurance_id
import taminx.core.core_ui.wedding_present_label_insurance_status
import taminx.core.core_ui.wedding_present_label_insurance_type
import taminx.core.core_ui.wedding_present_label_mobile
import taminx.core.core_ui.wedding_present_marriage_date
import taminx.core.core_ui.wedding_present_marriage_date_placeholder
import taminx.core.core_ui.wedding_present_more_details
import taminx.core.core_ui.wedding_present_partner_national_code
import taminx.core.core_ui.wedding_present_partner_national_code_placeholder
import taminx.core.core_ui.wedding_present_request_section
import taminx.core.core_ui.wedding_present_submit
import taminx.core.core_ui.wedding_present_success_confirm
import taminx.core.core_ui.wedding_present_success_message
import taminx.core.core_ui.wedding_present_success_title

private const val CHEVRON_DOWN_DEGREES = 90f
private const val NATIONAL_CODE_LENGTH = 10
private val DetailDashOn = Spacing.sm
private val DetailDashOff = Spacing.xs

@Composable
fun WeddingPresentScreen(
    onBack: () -> Unit,
    onNavigateToCalculate: () -> Unit = {},
    viewModel: WeddingPresentViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HandleWeddingPresentEvents(
        events = viewModel.events,
        onBack = onBack,
        onNavigateToCalculate = onNavigateToCalculate,
    )

    WeddingPresentContent(
        state = state,
        onBack = onBack,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleWeddingPresentEvents(
    events: Flow<WeddingPresentEvent>,
    onBack: () -> Unit,
    onNavigateToCalculate: () -> Unit,
) {
    val toaster = LocalToaster.current
    events.collectWithLifecycleAware { event ->
        when (event) {
            WeddingPresentEvent.NavigateBack -> onBack()
            WeddingPresentEvent.NavigateToCalculate -> onNavigateToCalculate()
            is WeddingPresentEvent.ShowToast -> toaster.error(event.message)
        }
    }
}

@Composable
private fun WeddingPresentContent(
    state: WeddingPresentUiState,
    onBack: () -> Unit,
    onIntent: (WeddingPresentIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            WeddingPresentHeader(
                onBackClicked = onBack,
                onCalculateClicked = { onIntent(WeddingPresentIntent.OpenCalculate) },
            )
        },
        bottomBar = {
            if (!state.isLoading && state.info != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.bgPage)
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = Spacing.page, vertical = Spacing.md),
                ) {
                    LoadingButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.wedding_present_submit),
                        onClick = { onIntent(WeddingPresentIntent.Submit) },
                        isLoading = state.isSubmitting,
                        icon = Icons.Filled.Check,
                    )
                }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> WeddingPresentSkeleton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.page, vertical = Spacing.lg),
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.page, vertical = Spacing.lg)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                state.info?.let { info ->
                    RequestDetailsSection(
                        info = info,
                        isExpanded = state.isDetailsExpanded,
                        onToggle = { onIntent(WeddingPresentIntent.ToggleDetails) },
                    )
                    RequestFormSection(
                        state = state,
                        onIntent = onIntent,
                    )
                }
            }
        }
    }

    if (state.showDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.wedding_present_marriage_date),
            onDismiss = { onIntent(WeddingPresentIntent.DismissDatePicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    WeddingPresentIntent.MarriageDatePicked(
                        label = PersianDateFormatter.format(year, month, day),
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                    ),
                )
            },
        )
    }

    if (state.showSuccessDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.wedding_present_success_title),
            description = stringResource(Res.string.wedding_present_success_message),
            icon = Icons.Filled.Check,
            iconTint = colors.greenText,
            iconBackground = colors.greenBg,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.wedding_present_success_confirm),
                    onClick = { onIntent(WeddingPresentIntent.DismissSuccessDialog) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(WeddingPresentIntent.DismissSuccessDialog) },
        )
    }

    ErrorStateView(
        message = state.error,
        onDismiss = { onIntent(WeddingPresentIntent.DismissError) },
    )
}

@Composable
private fun RequestDetailsSection(
    info: WeddingPresentInfoPR,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(Res.string.wedding_present_details_section),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        ) {
            DetailRow(
                label = stringResource(Res.string.wedding_present_label_insurance_id),
                value = info.risuid.toPersianDigits(),
            )
            DashedDetailDivider()
            DetailRow(
                label = stringResource(Res.string.wedding_present_label_full_name),
                value = info.fullName,
                numeric = false,
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    DashedDetailDivider()
                    DetailRow(
                        label = stringResource(Res.string.wedding_present_label_insurance_type),
                        value = info.insuranceTypeDesc,
                        numeric = false,
                    )
                    DashedDetailDivider()
                    DetailRow(
                        label = stringResource(Res.string.wedding_present_label_insurance_status),
                        value = info.insuranceStatusDesc,
                        numeric = false,
                    )
                    DashedDetailDivider()
                    DetailRow(
                        label = stringResource(Res.string.wedding_present_label_bank_account),
                        value = info.bankAccount.toPersianDigits(),
                    )
                    DashedDetailDivider()
                    DetailRow(
                        label = stringResource(Res.string.wedding_present_label_bank_name),
                        value = info.bankName,
                        numeric = false,
                    )
                    DashedDetailDivider()
                    DetailRow(
                        label = stringResource(Res.string.wedding_present_label_branch),
                        value = info.branchName,
                        numeric = false,
                    )
                    DashedDetailDivider()
                    DetailRow(
                        label = stringResource(Res.string.wedding_present_label_mobile),
                        value = info.mobileNumber.toPersianDigits(),
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.full))
                    .background(colors.blueBg)
                    .clickable(onClick = onToggle)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(
                        if (isExpanded) {
                            Res.string.wedding_present_hide_details
                        } else {
                            Res.string.wedding_present_more_details
                        },
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.blueText,
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier
                        .padding(start = Spacing.xs)
                        .size(IconSize.small)
                        .rotate(if (isExpanded) -CHEVRON_DOWN_DEGREES else CHEVRON_DOWN_DEGREES),
                )
            }
        }
    }
}

@Composable
private fun RequestFormSection(
    state: WeddingPresentUiState,
    onIntent: (WeddingPresentIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(Res.string.wedding_present_request_section),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminStyledTextField(
                value = state.marriageDateLabel.toPersianDigits(),
                onValueChange = {},
                label = stringResource(Res.string.wedding_present_marriage_date),
                placeholder = stringResource(Res.string.wedding_present_marriage_date_placeholder),
                leadingIconPainter = painterResource(Res.drawable.ic_tamin_calendar),
                readOnly = true,
                isRequired = true,
                isValid = state.marriageDateError?.let { false },
                errorText = state.marriageDateError,
                onClick = { onIntent(WeddingPresentIntent.OpenDatePicker) },
            )
            TaminStyledTextField(
                value = state.partnerNationalCode,
                onValueChange = { onIntent(WeddingPresentIntent.PartnerNationalCodeChanged(it)) },
                label = stringResource(Res.string.wedding_present_partner_national_code),
                placeholder = stringResource(Res.string.wedding_present_partner_national_code_placeholder),
                leadingIconPainter = painterResource(Res.drawable.ic_tamin_user),
                isRequired = true,
                isValid = state.partnerNationalCodeError?.let { false },
                errorText = state.partnerNationalCodeError,
                maxLength = NATIONAL_CODE_LENGTH,
                inputRestriction = InputRestriction.DigitsOnly,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.bgPage)
                    .clickable {
                        onIntent(WeddingPresentIntent.CommitmentChecked(!state.isCommitmentChecked))
                    }
                    .padding(Spacing.md),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminCheckBox(
                    checked = state.isCommitmentChecked,
                    onCheckedChange = { onIntent(WeddingPresentIntent.CommitmentChecked(it)) },
                )
                Text(
                    text = stringResource(Res.string.wedding_present_commitment_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DashedDetailDivider(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = Spacing.xs,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
            .height(Thickness.border)
            .drawBehind {
                drawLine(
                    color = colors.divider,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = size.height,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(DetailDashOn.toPx(), DetailDashOff.toPx()),
                    ),
                )
            },
    )
}

@Composable
private fun WeddingPresentSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.cardHeight)
                .clip(RoundedCornerShape(CornerRadius.card))
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.cardHeight)
                .clip(RoundedCornerShape(CornerRadius.card))
                .shimmer(),
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.fieldHeight),
        )
    }
}

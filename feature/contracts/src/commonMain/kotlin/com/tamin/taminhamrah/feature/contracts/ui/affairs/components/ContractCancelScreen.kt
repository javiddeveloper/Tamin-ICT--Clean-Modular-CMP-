package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.ContractStatePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableOptionSheet
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toRialAmount
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_cancel_confirm_accept
import taminx.core.core_ui.contract_affairs_cancel_confirm_dismiss
import taminx.core.core_ui.contract_affairs_cancel_confirm_message
import taminx.core.core_ui.contract_affairs_cancel_confirm_title
import taminx.core.core_ui.contract_affairs_cancel_description_label
import taminx.core.core_ui.contract_affairs_cancel_description_placeholder
import taminx.core.core_ui.contract_affairs_cancel_insurance_type
import taminx.core.core_ui.contract_affairs_cancel_reason_label
import taminx.core.core_ui.contract_affairs_cancel_reason_placeholder
import taminx.core.core_ui.contract_affairs_cancel_reason_sheet_title
import taminx.core.core_ui.contract_affairs_cancel_submit
import taminx.core.core_ui.contract_affairs_cancel_subtitle
import taminx.core.core_ui.contract_affairs_cancel_success_dismiss
import taminx.core.core_ui.contract_affairs_cancel_success_message
import taminx.core.core_ui.contract_affairs_cancel_success_title
import taminx.core.core_ui.contract_affairs_cancel_title
import taminx.core.core_ui.contract_affairs_cancel_warning
import taminx.core.core_ui.contract_affairs_monthly_wage
import taminx.core.core_ui.contract_affairs_premium_rate
import taminx.core.core_ui.contract_affairs_search_number_label
import taminx.core.core_ui.ic_tamin_chevron_back

private const val CANCEL_DESCRIPTION_MAX_LENGTH = 500


@Composable
internal fun ContractCancelScreen(
    contract: ContractPR,
    reasons: ImmutableList<ContractStatePR>,
    isReasonsLoading: Boolean,
    selectedReason: ContractStatePR?,
    description: String,
    isSubmitting: Boolean,
    onReasonSelected: (ContractStatePR) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    var showReasonSheet by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage)
            // Opaque overlay: swallow taps so nothing reaches the list/header behind it.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
    ) {
        CancelHeader(contract = contract, onBackClicked = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg)
                .padding(top = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            WarningBanner()

            ContractSummaryCard(contract)

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                TaminText(
                    text = stringResource(Res.string.contract_affairs_cancel_reason_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textMuted,
                )
                PickerRow(
                    text = selectedReason?.title
                        ?: stringResource(Res.string.contract_affairs_cancel_reason_placeholder),
                    isPlaceholder = selectedReason == null,
                    onClick = { if (!isReasonsLoading) showReasonSheet = true },
                )
            }

            TaminTextArea(
                value = description,
                onValueChange = onDescriptionChanged,
                label = stringResource(Res.string.contract_affairs_cancel_description_label),
                placeholder = stringResource(Res.string.contract_affairs_cancel_description_placeholder),
                maxLength = CANCEL_DESCRIPTION_MAX_LENGTH,
            )

            Spacer(Modifier.height(Spacing.xs))
        }

        CancelActionBar(
            enabled = selectedReason != null && !isSubmitting,
            isSubmitting = isSubmitting,
            onClick = { showConfirmDialog = true },
        )
    }

    if (showReasonSheet) {
        TaminSearchableOptionSheet(
            title = stringResource(Res.string.contract_affairs_cancel_reason_sheet_title),
            items = reasons.map { TaminOptionSheetItem(id = it.code.toString(), label = it.title) },
            selectedId = selectedReason?.code?.toString(),
            showSearch = false,
            onSelect = { option ->
                reasons.firstOrNull { it.code.toString() == option.id }?.let(onReasonSelected)
                showReasonSheet = false
            },
            onDismiss = { showReasonSheet = false },
        )
    }

    if (showConfirmDialog) {
        CancelConfirmationDialog(
            title = stringResource(Res.string.contract_affairs_cancel_confirm_title),
            description = stringResource(
                Res.string.contract_affairs_cancel_confirm_message,
                contract.contractNumber,
            ),
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.contract_affairs_cancel_confirm_accept),
                    onClick = {
                        showConfirmDialog = false
                        onConfirm()
                    },
                    background = colors.iconGradientDanger,
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.contract_affairs_cancel_confirm_dismiss),
                    onClick = { showConfirmDialog = false },
                )
            },
            onDismissRequest = { showConfirmDialog = false },
        )
    }
}

@Composable
private fun CancelHeader(contract: ContractPR, onBackClicked: () -> Unit) {
    val colors = LocalTaminColors.current
    val gradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
            )
            .background(gradient),
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.contract_affairs_cancel_title),
            background = gradient,
            bottomPadding = Spacing.md,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                )
            },
        ) {
            TaminText(
                textAlign = TextAlign.Center,
                text = stringResource(
                    Res.string.contract_affairs_cancel_subtitle,
                    contract.insuranceType,
                    contract.contractNumber,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textHeaderSubtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
private fun WarningBanner() {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.orangeBg, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.WarningAmber,
            contentDescription = null,
            tint = colors.orangeText,
            modifier = Modifier.size(18.dp),
        )
        TaminText(
            text = stringResource(Res.string.contract_affairs_cancel_warning),
            style = MaterialTheme.typography.labelMedium,
            color = colors.orangeText,
        )
    }
}

@Composable
private fun ContractSummaryCard(contract: ContractPR) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            SummaryCell(
                label = stringResource(Res.string.contract_affairs_cancel_insurance_type),
                value = contract.insuranceType,
                numeric = false,
                modifier = Modifier.weight(1f),
            )
            SummaryCell(
                label = stringResource(Res.string.contract_affairs_search_number_label),
                value = contract.contractNumber,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            SummaryCell(
                label = stringResource(Res.string.contract_affairs_premium_rate),
                value = contract.premiumRatePercentLabel.ifBlank { "—" },
                numeric = contract.premiumRatePercentLabel.isNotBlank(),
                modifier = Modifier.weight(1f),
            )
            SummaryCell(
                label = stringResource(Res.string.contract_affairs_monthly_wage),
                value = contract.monthlyIncome.toRialAmount(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        if (numeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        } else {
            TaminText(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
        }
    }
}

@Composable
private fun CancelActionBar(
    enabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.glassSolid)
            .imePadding()
            .padding(start = Spacing.md, end = Spacing.md, top = Spacing.md)
            .padding(bottom = Spacing.md + bottomInset),
    ) {
        LoadingButton(
            text = stringResource(Res.string.contract_affairs_cancel_submit),
            onClick = onClick,
            enabled = enabled,
            isLoading = isSubmitting,
            background = if (enabled || isSubmitting) colors.iconGradientDanger else null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Confirmation shown once the request in [ContractCancelScreen] succeeds (mirrors the old success dialog). */
@Composable
internal fun ContractCancelSuccessDialog(onDismiss: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.contract_affairs_cancel_success_title),
        description = stringResource(Res.string.contract_affairs_cancel_success_message),
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.contract_affairs_cancel_success_dismiss),
                onClick = onDismiss,
                background = colors.successGradient,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
    )
}

// ---- previews ----

private val PreviewContract = ContractPR(
    contractNumber = "4832222686",
    statusDesc = "فعال بعلت تنظیم قرارداد",
    isActive = true,
    requestDate = "۱۴۰۵/۰۴/۰۱",
    insuranceType = "بیمهٔ اختیاری",
    monthlyPremiumLabel = "بیمه اختیاری ۲۷ درصد",
    monthlyIncome = "199506600",
    treatmentSupportText = "حمایت درمان دارد",
    hasTreatmentSupport = true,
    jobTitle = "",
    premiumTypeCode = "02",
    statusCode = 1,
    freeJobCode = "",
    premiumRatePercentLabel = "۲۷ درصد",
)

private val PreviewReasons = listOf(
    ContractStatePR(code = 1, title = "ابطال به دلیل سپری شدن مهلت قانونی"),
    ContractStatePR(code = 2, title = "انصراف بیمه‌شده"),
    ContractStatePR(code = 3, title = "اشتغال به کار و پوشش بیمهٔ اجباری"),
    ContractStatePR(code = 4, title = "برقراری مستمری بازنشستگی"),
).toImmutableList()

@PreviewRtlTheme
@Composable
private fun ContractCancelScreenEmptyPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractCancelScreen(
                contract = PreviewContract,
                reasons = PreviewReasons,
                isReasonsLoading = false,
                selectedReason = null,
                description = "",
                isSubmitting = false,
                onReasonSelected = {},
                onDescriptionChanged = {},
                onConfirm = {},
                onBack = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractCancelScreenFilledPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractCancelScreen(
                contract = PreviewContract,
                reasons = PreviewReasons,
                isReasonsLoading = false,
                selectedReason = PreviewReasons[1],
                description = "به دلیل اشتغال در کارگاه مشمول بیمهٔ اجباری",
                isSubmitting = false,
                onReasonSelected = {},
                onDescriptionChanged = {},
                onConfirm = {},
                onBack = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractCancelSuccessDialogPreview() {
    PreviewRtlThemeContent {
        ContractCancelSuccessDialog(onDismiss = {})
    }
}

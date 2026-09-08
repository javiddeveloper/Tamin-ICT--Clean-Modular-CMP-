package com.tamin.taminhamrah.feature.contractaffair.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractOperation
import com.tamin.taminhamrah.model.contractAffair.ContractPR
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.SelfInsuredContractStatus
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_deactivate_contract
import taminx.core.core_ui.contract_affairs_edit_contract
import taminx.core.core_ui.contract_affairs_operations_fraction_note
import taminx.core.core_ui.contract_affairs_operations_pending_note
import taminx.core.core_ui.contract_affairs_operations_sheet_title
import taminx.core.core_ui.contract_affairs_pay_premium
import taminx.core.core_ui.contract_affairs_search_number_label
import taminx.core.core_ui.contract_affairs_view_contract
import taminx.core.core_ui.contract_affairs_view_payments
import taminx.core.core_ui.ic_tamin_chevron_forward

/**
 * امور قرارداد — the per-contract operations bottom sheet opened from a [ContractAffairsItemCard].
 *
 * Visually mirrors [NewContractSheet]: a title + subtitle header, then a stack of rounded outlined
 * rows (icon tile + label + chevron). The set of rows comes straight from the ViewModel's
 * `operationsFor(contract)` — a single «مشاهدهٔ قرارداد» for قراردادهای در انتظار بررسی and کسری از
 * ماه (both with an explanatory note), the full پرداخت/پرداخت‌ها/ویرایش/مشاهده/غیرفعال set otherwise.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContractOperationsSheet(
    contract: ContractPR,
    operations: ImmutableList<ContractOperation>,
    onOperationClick: (ContractOperation) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val note = when {
        contract.statusCode != SelfInsuredContractStatus.ACTIVE ->
            stringResource(Res.string.contract_affairs_operations_pending_note)

        contract.premiumTypeCode == ContractPremiumType.FRACTION.code ->
            stringResource(Res.string.contract_affairs_operations_fraction_note)

        else -> null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                TaminText(
                    text = stringResource(
                        Res.string.contract_affairs_operations_sheet_title,
                        contract.insuranceType,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    TaminText(
                        text = stringResource(Res.string.contract_affairs_search_number_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                    NumericText(
                        text = contract.contractNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }
            }

            note?.let { InfoNote(it) }

            operations.forEach { operation ->
                ContractOperationRow(
                    operation = operation,
                    onClick = { onOperationClick(operation) },
                )
            }
        }
    }
}

@Composable
private fun InfoNote(text: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(18.dp),
        )
        TaminText(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
        )
    }
}

@Composable
private fun ContractOperationRow(
    operation: ContractOperation,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val destructive = operation == ContractOperation.DEACTIVATE
    val shape = RoundedCornerShape(CornerRadius.lg)

    val (tileColor, iconTint) = when (operation) {
        ContractOperation.PAY_PREMIUM -> colors.greenBg to colors.greenText
        ContractOperation.VIEW_PAYMENTS,
        ContractOperation.EDIT_CONTRACT -> colors.blueBg to colors.blueText

        ContractOperation.VIEW_CONTRACT -> colors.bgPage to colors.textMuted
        ContractOperation.DEACTIVATE -> colors.dangerBorder to colors.dangerText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface, shape)
            .border(1.dp, colors.border.copy(alpha = 0.5f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(tileColor, RoundedCornerShape(CornerRadius.md)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = operationIcon(operation),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        TaminText(
            text = stringResource(operationLabel(operation)),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (destructive) colors.dangerText else colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.sm),
        )

        Spacer(Modifier.width(Spacing.xs))

        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = if (destructive) colors.dangerText else colors.textMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun operationIcon(operation: ContractOperation): ImageVector = when (operation) {
    ContractOperation.PAY_PREMIUM -> Icons.Outlined.CreditCard
    ContractOperation.VIEW_PAYMENTS -> Icons.Outlined.History
    ContractOperation.EDIT_CONTRACT -> Icons.Outlined.Edit
    ContractOperation.VIEW_CONTRACT -> Icons.Outlined.Description
    ContractOperation.DEACTIVATE -> Icons.Outlined.Delete
}

private fun operationLabel(operation: ContractOperation) = when (operation) {
    ContractOperation.PAY_PREMIUM -> Res.string.contract_affairs_pay_premium
    ContractOperation.VIEW_PAYMENTS -> Res.string.contract_affairs_view_payments
    ContractOperation.EDIT_CONTRACT -> Res.string.contract_affairs_edit_contract
    ContractOperation.VIEW_CONTRACT -> Res.string.contract_affairs_view_contract
    ContractOperation.DEACTIVATE -> Res.string.contract_affairs_deactivate_contract
}

// ---- previews ----

private val PreviewOptionalActive = ContractPR(
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

private val PreviewHomemakerPending = ContractPR(
    contractNumber = "4841110073",
    statusDesc = "در انتظار بررسی",
    isActive = true,
    requestDate = "۱۴۰۵/۰۵/۱۸",
    insuranceType = "بیمهٔ زنان خانه‌دار",
    monthlyPremiumLabel = "زنان خانه‌دار ۱۴ درصد",
    monthlyIncome = "110000000",
    treatmentSupportText = "حمایت درمان ندارد",
    hasTreatmentSupport = false,
    jobTitle = "",
    premiumTypeCode = "05",
    statusCode = null,
    freeJobCode = "",
    premiumRatePercentLabel = "۱۴ درصد",
)

private val PreviewFractionActive = ContractPR(
    contractNumber = "4796551208",
    statusDesc = "فعال",
    isActive = true,
    requestDate = "۱۴۰۴/۰۸/۲۵",
    insuranceType = "تکمیل سوابق کسری از ماه",
    monthlyPremiumLabel = "تکمیل سوابق ۲۷ درصد",
    monthlyIncome = "89340000",
    treatmentSupportText = "حمایت درمان ندارد",
    hasTreatmentSupport = false,
    jobTitle = "",
    premiumTypeCode = "38",
    statusCode = 1,
    freeJobCode = "",
    premiumRatePercentLabel = "۲۷ درصد",
)

private val FullOperations = listOf(
    ContractOperation.PAY_PREMIUM,
    ContractOperation.VIEW_PAYMENTS,
    ContractOperation.EDIT_CONTRACT,
    ContractOperation.VIEW_CONTRACT,
    ContractOperation.DEACTIVATE,
).toImmutableList()

@PreviewRtlTheme
@Composable
private fun ContractOperationsSheetFullPreviewLight() {
    PreviewRtlThemeContent {
        ContractOperationsSheet(
            contract = PreviewOptionalActive,
            operations = FullOperations,
            onOperationClick = {},
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractOperationsSheetFullPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractOperationsSheet(
            contract = PreviewOptionalActive,
            operations = FullOperations,
            onOperationClick = {},
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractOperationsSheetPendingPreviewLight() {
    PreviewRtlThemeContent {
        ContractOperationsSheet(
            contract = PreviewHomemakerPending,
            operations = persistentListOf(ContractOperation.VIEW_CONTRACT),
            onOperationClick = {},
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractOperationsSheetFractionPreviewLight() {
    PreviewRtlThemeContent {
        ContractOperationsSheet(
            contract = PreviewFractionActive,
            operations = persistentListOf(ContractOperation.VIEW_CONTRACT),
            onOperationClick = {},
            onDismiss = {},
        )
    }
}

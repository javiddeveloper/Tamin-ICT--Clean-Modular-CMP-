package com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.rememberCopyCodeChipState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toRialAmount
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.geometry.CornerRadius as GeometryCornerRadius
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_payment_history_collection_status
import taminx.core.core_ui.contract_payment_history_debt_number
import taminx.core.core_ui.contract_payment_history_hide_details
import taminx.core.core_ui.contract_payment_history_payment_date
import taminx.core.core_ui.contract_payment_history_payment_deadline
import taminx.core.core_ui.contract_payment_history_show_details
import taminx.core.core_ui.contract_payment_history_term_end
import taminx.core.core_ui.contract_payment_history_term_start
import taminx.core.core_ui.contract_payment_history_total_debt

private const val ABSENT = "—"

@Composable
internal fun PaymentHistoryItemCard(
    item: ContractPaymentHistoryItemPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var expanded by rememberSaveable(item.debtNumber) { mutableStateOf(false) }

    val tileColor = if (item.isPaid) colors.greenBg else colors.orangeBg
    val accent = if (item.isPaid) colors.greenText else colors.orangeText
    val statusIcon = if (item.isPaid) Icons.Outlined.Check else Icons.Outlined.PriorityHigh

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 26.dp,
                offsetY = 10.dp,
            )
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.lg, bottom = Spacing.sm),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.large)
                    .background(tileColor, RoundedCornerShape(CornerRadius.avatarTile)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(IconSize.medium),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = item.amountPayment.toRialAmount(fallback = ABSENT),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
                StatusPill(
                    text = item.statusLabel,
                    containerColor = tileColor,
                    contentColor = accent,
                    borderColor = colors.border,
                    verticalPadding = 2.dp,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                TaminText(
                    text = stringResource(Res.string.contract_payment_history_payment_date),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                NumericText(
                    text = item.datePayment.ifBlank { "_" },
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textPrimary,
                )
            }
        }

        DebtNumberRow(debtNumber = item.debtNumber)

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xlg)
                    .padding(bottom = Spacing.lg, top = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    DetailTile(
                        label = stringResource(Res.string.contract_payment_history_collection_status),
                        value = item.collectionStatus.ifBlank { ABSENT },
                        modifier = Modifier.weight(1f),
                    )
                    DetailTile(
                        label = stringResource(Res.string.contract_payment_history_total_debt),
                        value = item.totalDebt.toRialAmount(),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    DetailTile(
                        label = stringResource(Res.string.contract_payment_history_term_start),
                        value = item.termStart.ifBlank { ABSENT },
                        modifier = Modifier.weight(1f),
                    )
                    DetailTile(
                        label = stringResource(Res.string.contract_payment_history_term_end),
                        value = item.termEnd.ifBlank { ABSENT },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (item.paymentDeadline.isNotBlank()) {
                    DetailTile(
                        label = stringResource(Res.string.contract_payment_history_payment_deadline),
                        value = item.paymentDeadline,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.sm, bottom = Spacing.lg),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .border(1.dp, colors.blueBorder, RoundedCornerShape(CornerRadius.md))
                    .background(colors.bgSurface, RoundedCornerShape(CornerRadius.md))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                TaminText(
                    text = stringResource(
                        if (expanded) Res.string.contract_payment_history_hide_details
                        else Res.string.contract_payment_history_show_details,
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.blueText,
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun DebtNumberRow(debtNumber: String) {
    val colors = LocalTaminColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xlg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(18.dp),
        )
        TaminText(
            text = stringResource(Res.string.contract_payment_history_debt_number),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )

        DashedLine(color = colors.divider, modifier = Modifier.weight(1f))

        val copied = rememberCopyCodeChipState()
        val copy = rememberCopyAction(debtNumber, copiedState = copied)
        Row(
            modifier = Modifier
                .dashedRoundedBorder(colors.blueText, CornerRadius.md)
                .background(colors.blueBg, RoundedCornerShape(CornerRadius.md))
                .clickable(enabled = debtNumber.isNotBlank(), onClick = copy)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            CopyIconButton(value = debtNumber, interactive = false, copiedState = copied)
            NumericText(
                text = debtNumber.ifBlank { ABSENT },
                style = MaterialTheme.typography.labelLarge,
                color = colors.blueText,
            )
        }
    }
}

@Composable
private fun DetailTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun DashedLine(color: Color, modifier: Modifier = Modifier, thickness: Dp = 1.dp) {
    Canvas(modifier = modifier.height(thickness)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
        )
    }
}

private fun Modifier.dashedRoundedBorder(color: Color, radius: Dp): Modifier = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f),
        ),
        cornerRadius = GeometryCornerRadius(radius.toPx(), radius.toPx()),
    )
}

// ---- previews ----

private val PaidRow = ContractPaymentHistoryItemPR(
    debtNumber = "۹۰۵۰۱۱۳۶۱۳۹۶",
    amountPayment = "55662341",
    datePayment = "۱۴۰۵/۰۴/۱۶",
    totalDebt = "55662341",
    paymentDeadline = "۱۴۰۵/۰۵/۱۵",
    termStart = "۱۴۰۵/۰۲/۰۱",
    termEnd = "۱۴۰۵/۰۴/۳۱",
    collectionStatus = "وصول شده",
    isPaid = true,
    statusLabel = "پرداخت شده",
)

private val UnpaidRow = ContractPaymentHistoryItemPR(
    debtNumber = "۹۰۵۰۱۱۶۵۷۹۲۹",
    amountPayment = "0",
    datePayment = "",
    totalDebt = "58940120",
    paymentDeadline = "",
    termStart = "۱۴۰۵/۰۵/۰۱",
    termEnd = "۱۴۰۵/۰۷/۳۱",
    collectionStatus = "در انتظار",
    isPaid = false,
    statusLabel = "پرداخت نشده",
)

@PreviewRtlTheme
@Composable
private fun PaymentHistoryItemCardPaidPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            PaymentHistoryItemCard(item = PaidRow, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentHistoryItemCardUnpaidPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            PaymentHistoryItemCard(item = UnpaidRow, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

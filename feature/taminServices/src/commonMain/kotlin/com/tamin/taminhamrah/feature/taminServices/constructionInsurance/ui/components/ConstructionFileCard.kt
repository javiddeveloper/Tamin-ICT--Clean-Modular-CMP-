package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.branch
import taminx.core.core_ui.btn_action
import taminx.core.core_ui.btn_request_details
import taminx.core.core_ui.cash_type
import taminx.core.core_ui.file_number
import taminx.core.core_ui.installment_type
import taminx.core.core_ui.label_calculated_amount
import taminx.core.core_ui.label_registration_date
import taminx.core.core_ui.label_request_number
import taminx.core.core_ui.workshop_number

/** The literal every money line in the app appends to a grouped amount — see `Extentions.kt`. */
private const val RIAL_UNIT = "ریال"

/**
 * One پروندهٔ ساختمانی row — file number + payment-type pill, a کارگاه/درخواست grid, a تاریخ ثبت /
 * مبلغ محاسبه‌شده grid, the شعبه line, then two actions. Shell copied from `employerOnlineServices`'
 * `EmployerAgreementCard` (`coloredShadow` + `taminSurface`, `bgPage`-tinted `InfoBox` tiles) rather
 * than a `Card` with elevation, and every color comes from [LocalTaminColors] so both themes hold.
 *
 * جزئیات درخواست and عملیات are inert for now — wired once those flows are designed.
 */
@Composable
fun ConstructionFileCard(
    item: ConstructionFilePR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val isInstallment = item.debitStatusCode == INSTALLMENT_DEBIT_STATUS_CODE

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
        // Header: file number title on the leading (right, RTL) edge, payment-type pill trailing.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.lg, bottom = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.file_number),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${item.fileNumber ?: "-"}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
            }

            StatusPill(
                text = stringResource(
                    if (isInstallment) Res.string.installment_type else Res.string.cash_type,
                ),
                containerColor = if (isInstallment) colors.orangeBg else colors.blueBg,
                contentColor = if (isInstallment) colors.orangeText else colors.blueText,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            DetailGridRow(
                labelStart = stringResource(Res.string.workshop_number),
                valueStart = item.workshopInfo?.workshopId ?: "-",
                labelEnd = stringResource(Res.string.label_request_number),
                valueEnd = "${item.requestNumber ?: "-"}",
            )
            DetailGridRow(
                labelStart = stringResource(Res.string.label_registration_date),
                valueStart = item.workshopInfo?.workshopRegisterDate ?: item.requestDate ?: "-",
                labelEnd = stringResource(Res.string.label_calculated_amount),
                valueEnd = (item.totalPayment ?: 0L).toPriceFormat(),
                unitEnd = RIAL_UNIT,
                valueEndColor = colors.blueText,
                weightEnd = AmountTileWeight,
            )

            val branchCode = item.workshopInfo?.brhCode
            if (!branchCode.isNullOrBlank()) {
                Text(
                    text = "${stringResource(Res.string.branch)} $branchCode",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
        }

        // Footer: جزئیات درخواست on the leading (right, RTL) edge — wider — عملیات trailing.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.md, bottom = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminOutlinedButton(
                text = stringResource(Res.string.btn_request_details),
                onClick = { /* no-op for now — wired once the detail flow is designed */ },
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                iconPosition = IconPosition.Start,
                height = ButtonHeight,
                textStyle = MaterialTheme.typography.labelMedium,
                containerColor = colors.bgPage,
                borderColor = Color.Transparent,
                contentColor = colors.blueText,
                modifier = Modifier.weight(AmountTileWeight),
            )
            TaminFilledButton(
                text = stringResource(Res.string.btn_action),
                onClick = { /* no-op for now — wired once the action flow is designed */ },
                icon = Icons.Default.Settings,
                height = ButtonHeight,
                textStyle = MaterialTheme.typography.labelMedium,
                background = Brush.linearGradient(listOf(TaminNavy300, TaminNavy900)),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private const val INSTALLMENT_DEBIT_STATUS_CODE = "51"

/** How much wider مبلغ محاسبه‌شده / جزئیات درخواست read next to their one-unit-weight neighbor. */
private const val AmountTileWeight = 2f
private val ButtonHeight = 44.dp

@Composable
private fun DetailGridRow(
    labelStart: String,
    valueStart: String,
    labelEnd: String,
    valueEnd: String,
    modifier: Modifier = Modifier,
    unitEnd: String? = null,
    valueEndColor: Color? = null,
    weightEnd: Float = 1f,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        InfoBox(label = labelStart, value = valueStart, modifier = Modifier.weight(1f))
        InfoBox(
            label = labelEnd,
            value = valueEnd,
            unit = unitEnd,
            valueColor = valueEndColor,
            modifier = Modifier.weight(weightEnd),
        )
    }
}

@Composable
private fun InfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    valueColor: Color? = null,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        val valueStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        val resolvedValueColor = valueColor ?: colors.textPrimary
        if (unit != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumericText(text = value, style = valueStyle, color = resolvedValueColor)
                Text(text = unit, style = valueStyle, color = resolvedValueColor)
            }
        } else {
            NumericText(text = value, style = valueStyle, color = resolvedValueColor)
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewInstallmentFile = ConstructionFilePR(
    fileNumber = 123804L,
    requestNumber = 879115L,
    requestDate = "14021109",
    workshopInfo = WorkshopIdInfoPR(
        workshopRegisterDate = "1402/11/09",
        workshopId = "2361847",
        brhCode = "7",
    ),
    totalPayment = 1_284_000_000L,
    debitStatusCode = "51",
)

private val PreviewCashFile = ConstructionFilePR(
    fileNumber = 124037L,
    requestNumber = 881902L,
    requestDate = "14030714",
    workshopInfo = WorkshopIdInfoPR(
        workshopRegisterDate = "1403/07/14",
        workshopId = "2361847",
        brhCode = "7",
    ),
    totalPayment = 486_000_000L,
    debitStatusCode = "10",
)

@PreviewRtlTheme
@Composable
private fun ConstructionFileCardInstallmentPreviewLight() {
    PreviewRtlThemeContent {
        ConstructionFileCard(item = PreviewInstallmentFile, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionFileCardInstallmentPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ConstructionFileCard(item = PreviewInstallmentFile, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionFileCardCashPreviewLight() {
    PreviewRtlThemeContent {
        ConstructionFileCard(item = PreviewCashFile, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionFileCardCashPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ConstructionFileCard(item = PreviewCashFile, modifier = Modifier.padding(Spacing.lg))
    }
}

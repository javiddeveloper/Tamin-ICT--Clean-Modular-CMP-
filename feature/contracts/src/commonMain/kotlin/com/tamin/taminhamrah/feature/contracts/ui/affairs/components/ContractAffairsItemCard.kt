package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toRialAmount
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_contract_code
import taminx.core.core_ui.contract_affairs_deferred_debt
import taminx.core.core_ui.contract_affairs_job
import taminx.core.core_ui.contract_affairs_monthly_wage
import taminx.core.core_ui.contract_affairs_operations
import taminx.core.core_ui.contract_affairs_pay_premium
import taminx.core.core_ui.contract_affairs_premium_rate
import taminx.core.core_ui.contract_affairs_request_date
import taminx.core.core_ui.contract_affairs_status_active
import taminx.core.core_ui.contract_affairs_treatment_has
import taminx.core.core_ui.contract_affairs_treatment_none
import taminx.core.core_ui.contract_affairs_treatment_support
import taminx.core.core_ui.contract_affairs_view_contract
import androidx.compose.ui.geometry.CornerRadius as GeometryCornerRadius

/** `contractStatusObject.selfIsuContStatCode == 1` → the contract is operable (شغل/pay/deactivate). */
private const val ACTIVE_STATUS_CODE = 1

/** `premiumTypeCode == "38"` → تکمیل/کسری contract; its only per-contract action is مشاهدهٔ قرارداد. */
private const val FRACTION_PREMIUM_TYPE_CODE = "38"

/**
 * One امور قراردادها و پرداخت card — mirrors the shape of
 * [com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components.EmployerAgreementCard]:
 * a glass icon + title + status pill header, the کد قرارداد chip, a 2×2 نرخ/دستمزد/درمان/شغل grid,
 * an optional بدهی معوق banner, then the footer actions.
 *
 * Footer variants:
 *  - not operable (pending / cancelled) → no footer at all; the امور قرارداد sheet only exists for
 *    فعال contracts.
 *  - operable + تکمیل سوابق (fraction) → «امور قرارداد» + «مشاهدهٔ قرارداد».
 *  - operable, any other type → «امور قرارداد» + «پرداخت حق بیمه».
 */
@Composable
internal fun ContractAffairsItemCard(
    item: ContractPR,
    onOperationsClicked: () -> Unit,
    onPrimaryActionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val operable = item.statusCode == ACTIVE_STATUS_CODE
    val isFraction = item.premiumTypeCode == FRACTION_PREMIUM_TYPE_CODE

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
        Header(item)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ContractCodeRow(code = item.contractNumber)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                StatBox(
                    label = stringResource(Res.string.contract_affairs_premium_rate),
                    value = item.premiumRatePercentLabel.ifBlank { "—" },
                    numeric = item.premiumRatePercentLabel.isNotBlank(),
                    modifier = Modifier.weight(1f),
                )
                StatBox(
                    label = stringResource(Res.string.contract_affairs_monthly_wage),
                    value = item.monthlyIncome.toRialAmount(),
                    valueColor = colors.greenText,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                StatBox(
                    label = stringResource(Res.string.contract_affairs_treatment_support),
                    value = stringResource(
                        if (item.hasTreatmentSupport) Res.string.contract_affairs_treatment_has
                        else Res.string.contract_affairs_treatment_none,
                    ),
                    numeric = false,
                    valueColor = if (item.hasTreatmentSupport) colors.blueText else colors.dangerText,
                    modifier = Modifier.weight(1f),
                )
                StatBox(
                    label = stringResource(Res.string.contract_affairs_job),
                    value = item.jobTitle.orDash(),
                    numeric = false,
                    modifier = Modifier.weight(1f),
                )
            }

            item.deferredDebtLabel?.let { DeferredDebtBanner(it) }
        }

        // Only فعال contracts expose امور قرارداد; pending / ابطال cards end at the details grid.
        if (operable) {
            Footer(
                isFraction = isFraction,
                onOperationsClicked = onOperationsClicked,
                onPrimaryActionClicked = onPrimaryActionClicked,
            )
        } else {
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun Header(item: ContractPR) {
    val colors = LocalTaminColors.current
    val isDark = colors == DarkTaminColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xlg)
            .padding(top = Spacing.lg, bottom = Spacing.sm),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {

        Box(
            modifier = Modifier
                .size(IconSize.large)
                .shadow(
                    elevation = Elevation.md,
                    shape = RoundedCornerShape(CornerRadius.xl),
                    clip = false,
                    ambientColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                    spotColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.7f
                            )
                        ),
                        start = Offset.Zero,
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(CornerRadius.xl)
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isDark) 0.15f else 0.9f),
                            Color.White.copy(alpha = if (isDark) 0.02f else 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(CornerRadius.xl)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.05f else 0.6f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(CornerRadius.xl)
                    )
            )
            Icon(
                imageVector = contractIcon(item.premiumTypeCode, item.insuranceType),
                contentDescription = "contract_icons",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(IconSize.large)
                    .padding(Spacing.sm)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = item.insuranceType,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            StatusPill(
                verticalPadding = 2.dp,
                text = item.statusDesc,
                containerColor = statusContainerColor(item),
                contentColor = statusContentColor(item),
                borderColor = colors.border,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(Res.string.contract_affairs_request_date),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            NumericText(
                text = item.requestDate.ifBlank { "—" },
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
            )
        }
    }
}

@Composable
private fun ContractCodeRow(code: String) {
    val colors = LocalTaminColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.CreditCard,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(Res.string.contract_affairs_contract_code),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )

        DashedLine(
            color = colors.divider,
            modifier = Modifier.weight(1f),
        )

        val copyCode = rememberCopyAction(code)
        Row(
            modifier = Modifier
                .dashedRoundedBorder(colors.blueText, CornerRadius.md)
                .background(colors.blueBg, RoundedCornerShape(CornerRadius.md))
                .clickable(enabled = code.isNotBlank(), onClick = copyCode)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            CopyIconButton(value = code, interactive = false)
            NumericText(
                text = code.ifBlank { "—" },
                style = MaterialTheme.typography.labelLarge,
                color = colors.blueText,
            )
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
    valueColor: Color = LocalTaminColors.current.textPrimary,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        if (numeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = valueColor,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = valueColor,
            )
        }
    }
}

@Composable
private fun DeferredDebtBanner(text: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.orangeBg, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.orangeText,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(Res.string.contract_affairs_deferred_debt, text),
            style = MaterialTheme.typography.labelMedium,
            color = colors.orangeText,
        )
    }
}

@Composable
private fun Footer(
    isFraction: Boolean,
    onOperationsClicked: () -> Unit,
    onPrimaryActionClicked: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xlg)
            .padding(top = Spacing.md, bottom = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        TaminFilledButton(
            text = stringResource(
                if (isFraction) Res.string.contract_affairs_view_contract
                else Res.string.contract_affairs_pay_premium,
            ),
            onClick = onPrimaryActionClicked,
            height = 48.dp,
            textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            icon = if (isFraction) Icons.Outlined.Description else Icons.Outlined.CreditCard,
            iconPosition = IconPosition.End,
            background = if (isFraction) colors.heroGradient else colors.successGradient,
            modifier = Modifier.weight(1.8f),
        )
        TaminOutlinedButton(
            text = stringResource(Res.string.contract_affairs_operations),
            onClick = onOperationsClicked,
            height = 48.dp,
            textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            icon = Icons.Outlined.Newspaper,
            iconPosition = IconPosition.End,
            containerColor = colors.bgSurface,
            borderColor = colors.blueBorder,
            contentColor = colors.blueText,
            modifier = Modifier.weight(1f),
        )
    }
}

// ---- helpers ----

@Composable
private fun statusContainerColor(item: ContractPR): Color {
    val colors = LocalTaminColors.current
    return when {
        item.statusCode == ACTIVE_STATUS_CODE -> colors.greenBg
        item.statusDesc.contains("ابطال") -> colors.dangerBorder
        else -> colors.orangeBg
    }
}

@Composable
private fun statusContentColor(item: ContractPR): Color {
    val colors = LocalTaminColors.current
    return when {
        item.statusCode == ACTIVE_STATUS_CODE -> colors.greenText
        item.statusDesc.contains("ابطال") -> colors.dangerText
        else -> colors.orangeText
    }
}

private fun contractIcon(premiumTypeCode: String, title: String): ImageVector = when {
    title.contains("خانه‌دار") -> Icons.Outlined.Home
    premiumTypeCode == "01" -> Icons.Outlined.WorkOutline
    premiumTypeCode == "02" -> Icons.Outlined.HealthAndSafety
    premiumTypeCode == FRACTION_PREMIUM_TYPE_CODE -> Icons.Outlined.CalendarMonth
    else -> Icons.Outlined.Description
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

@Composable
fun StatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    fontWeight: FontWeight = FontWeight.Medium,
    verticalPadding: Dp = 5.dp,
    /** Outlines the pill. Null — the default — leaves it as a plain fill, as before. */
    borderColor: Color? = null,
) {
    Row(
        modifier = modifier
            .background(containerColor, CircleShape)
            .then(
                if (borderColor != null) {
                    Modifier.border(Thickness.border, borderColor, CircleShape)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = Spacing.sm, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(IconSize.small),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = fontWeight,
                fontSize = 10.sp
            ),
            color = contentColor
        )
    }
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

private val PreviewFreelanceActiveWithDebt = ContractPR(
    contractNumber = "4811907432",
    statusDesc = "فعال",
    isActive = true,
    requestDate = "۱۴۰۴/۱۱/۱۲",
    insuranceType = "حرف و مشاغل آزاد",
    monthlyPremiumLabel = "حرف و مشاغل ۱۸ درصد",
    monthlyIncome = "104250000",
    treatmentSupportText = "حمایت درمان دارد",
    hasTreatmentSupport = true,
    jobTitle = "رانندهٔ تاکسی شهری",
    premiumTypeCode = "01",
    statusCode = 1,
    freeJobCode = "",
    premiumRatePercentLabel = "۱۸ درصد",
    deferredDebtLabel = "۵۵٬۶۶۲٬۳۴۱ ریال",
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

@PreviewRtlTheme
@Composable
private fun ContractAffairsItemCardActivePreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsItemCard(
                item = PreviewOptionalActive,
                onOperationsClicked = {},
                onPrimaryActionClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsItemCardActivePreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractAffairsItemCard(
                item = PreviewFreelanceActiveWithDebt,
                onOperationsClicked = {},
                onPrimaryActionClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsItemCardActivePreviewLightWithDebt() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsItemCard(
                item = PreviewFreelanceActiveWithDebt,
                onOperationsClicked = {},
                onPrimaryActionClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsItemCardPendingPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsItemCard(
                item = PreviewHomemakerPending,
                onOperationsClicked = {},
                onPrimaryActionClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsItemCardFractionPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractAffairsItemCard(
                item = PreviewFractionActive,
                onOperationsClicked = {},
                onPrimaryActionClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

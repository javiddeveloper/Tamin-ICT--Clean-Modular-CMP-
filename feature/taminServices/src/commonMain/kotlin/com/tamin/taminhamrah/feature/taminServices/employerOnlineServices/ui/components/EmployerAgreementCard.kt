package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementRowPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.employer_online_services_address
import taminx.core.core_ui.employer_online_services_branch
import taminx.core.core_ui.employer_online_services_commitment_date
import taminx.core.core_ui.employer_online_services_contract_rows
import taminx.core.core_ui.employer_online_services_email
import taminx.core.core_ui.employer_online_services_mobile
import taminx.core.core_ui.employer_online_services_start_date
import taminx.core.core_ui.employer_online_services_status_active
import taminx.core.core_ui.employer_online_services_status_inactive
import taminx.core.core_ui.employer_online_services_workshop_code
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward

private const val CHEVRON_OPEN_DEGREES = -90f
private const val CHEVRON_CLOSED_DEGREES = 90f

/**
 * One تعهدنامه row on the landing screen: status pill + workshop name, a شماره کارگاه / شعبه grid,
 * the تاریخ تعهد line, then two chips — "ردیف‌های پیمان" (drills into that workshop's contract rows)
 * and "جزئیات" (expands address / dates / contact inline, no navigation).
 */
@Composable
internal fun EmployerAgreementCard(
    item: EmployerAgreementRowPR,
    onContractRowsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var expanded by remember(item.workshopId, item.branchCode) { mutableStateOf(false) }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val statusText = item.statusLabel.ifBlank {
                stringResource(
                    if (item.isActive) Res.string.employer_online_services_status_active
                    else Res.string.employer_online_services_status_inactive,
                )
            }
            Text(
                text = item.workshopName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = statusText,
                containerColor = if (item.isActive) colors.greenBg else colors.bgPage,
                contentColor = if (item.isActive) colors.greenText else colors.textMuted,
                borderColor = colors.border,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DetailGridRow(
                labelStart = stringResource(Res.string.employer_online_services_workshop_code),
                valueStart = item.workshopCodeLabel,
                numericStart = true,
                labelEnd = stringResource(Res.string.employer_online_services_branch),
                valueEnd = item.branchLabel,
                numericEnd = false,
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.employer_online_services_commitment_date),
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.textMuted)
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = item.commitmentDate,
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.textPrimary)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    DashedDivider(
                        color = colors.divider,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(Res.string.employer_online_services_email),
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.textMuted)
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = item.email,
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.textPrimary)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text =  stringResource(Res.string.employer_online_services_mobile),
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.textMuted)
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = item.mobile,
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.textPrimary)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text =  stringResource(Res.string.employer_online_services_address),
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.textMuted)
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = item.address,
                            style = MaterialTheme.typography.labelMedium.copy(color = colors.textPrimary)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.md, bottom = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            val rotation = animateFloatAsState(
                targetValue = if (expanded) CHEVRON_OPEN_DEGREES else CHEVRON_CLOSED_DEGREES,
                label = "employer-agreement-card-chevron",
            )
            TaminOutlinedButton(
                height = 48.dp,
                textStyle = MaterialTheme.typography.labelMedium,
                text = stringResource(
                    if (expanded) Res.string.action_hide_details else Res.string.action_show_details,
                ),
                onClick = { expanded = !expanded },
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                iconModifier = Modifier.size(5.dp).graphicsLayer { rotationZ = rotation.value },
                containerColor = colors.bgPage,
                borderColor = Color.Transparent,
                contentColor = colors.blueText,
                iconPosition = IconPosition.Start,
                modifier = Modifier.weight(1f),
            )
            TaminOutlinedButton(
                height = 48.dp,
                textStyle = MaterialTheme.typography.labelMedium,
                text = stringResource(Res.string.employer_online_services_contract_rows),
                onClick = onContractRowsClicked,
                enabled = item.hasIdentity,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconModifier = Modifier.size(5.dp),
                containerColor = colors.chipBg,
                borderColor = Color.Transparent,
                contentColor = colors.blueText,
                iconPosition = IconPosition.Start,
                modifier = Modifier.weight(1f),
            )

        }
    }
}

@Composable
private fun DetailGridRow(
    labelStart: String,
    valueStart: String,
    labelEnd: String,
    valueEnd: String,
    modifier: Modifier = Modifier,
    numericStart: Boolean = true,
    numericEnd: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        InfoBox(
            label = labelStart,
            value = valueStart,
            numeric = numericStart,
            modifier = Modifier.weight(1f),
        )
        InfoBox(
            label = labelEnd,
            value = valueEnd,
            numeric = numericEnd,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InfoBox(
    label: String,
    value: String,
    numeric: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, shape = RoundedCornerShape(CornerRadius.xl))
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
                color = colors.textPrimary,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        }
    }
}

@Composable
private fun DashedDivider(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    dashLength: Dp = 5.dp,
    gapLength: Dp = 5.dp,
) {
    Canvas(modifier = modifier.fillMaxWidth().height(thickness)) {
        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f,
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect,
        )
    }
}

private val PreviewActiveAgreement = EmployerAgreementRowPR(
    workshopId = "0081631829",
    branchCode = "1202",
    hasIdentity = true,
    isActive = true,
    statusLabel = "",
    workshopName = "شرکت صنایع دما بخار مشهد",
    branchLabel = "شعبهٔ ۲ مشهد · ۱۲۰۲",
    workshopCodeLabel = "۰۰۸۱۶۳۱۸۲۹",
    commitmentDate = "۱۴۰۳/۰۵/۱۹",
    startDate = "۱۴۰۳/۰۱/۰۱",
    address = "مشهد، بلوار وکیل‌آباد، نبش وکیل‌آباد ۱۲",
    mobile = "۰۹۱۲۳۴۵۶۷۸۹",
    email = "info@damabokhar.ir",
)

@PreviewRtlTheme
@Composable
private fun EmployerAgreementCardPreviewLight() {
    PreviewRtlThemeContent {
        EmployerAgreementCard(
            item = PreviewActiveAgreement,
            onContractRowsClicked = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerAgreementCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerAgreementCard(
            item = PreviewActiveAgreement,
            onContractRowsClicked = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerAgreementCardInactivePreviewLight() {
    PreviewRtlThemeContent {
        EmployerAgreementCard(
            item = PreviewActiveAgreement.copy(
                isActive = false,
                hasIdentity = false,
                workshopName = "بازرگانی توکلی و پسران",
                statusLabel = "غیرفعال",
            ),
            onContractRowsClicked = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

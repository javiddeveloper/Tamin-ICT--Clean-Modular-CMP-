package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.toPensionerTypeLabel
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toFormattedDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_send_certificate
import taminx.core.core_ui.active_relation_verified_badge
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.pension_status_active_header
import taminx.core.core_ui.pension_status_inactive_badge
import taminx.core.core_ui.pension_status_inactive_header
import taminx.core.core_ui.pension_status_establishment_date
import taminx.core.core_ui.pension_status_insurance_number
import taminx.core.core_ui.pension_status_main_insured_number
import taminx.core.core_ui.pension_status_order_type
import taminx.core.core_ui.pension_status_org_unit
import taminx.core.core_ui.pension_status_payment_date
import taminx.core.core_ui.pension_status_pension_number

@Composable
internal fun PensionStatusCard(
    item: PensionInquiryPR,
    isSendingCertificate: Boolean,
    onSendCertificateClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        border = BorderStroke(Thickness.border, colors.border),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            StatusHeader(isActive = item.isActive)
            TaminDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                ProfileRow(item = item)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    CopyableNumberBox(
                        label = stringResource(Res.string.pension_status_pension_number),
                        value = item.pensionerRisUid,
                        modifier = Modifier.weight(1f),
                    )
                    CopyableNumberBox(
                        label = stringResource(Res.string.pension_status_insurance_number),
                        value = item.insuranceNumber,
                        modifier = Modifier.weight(1f),
                    )
                }

                InfoFieldRow(
                    label = stringResource(Res.string.pension_status_main_insured_number),
                    value = item.insuranceNumber,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    InfoFieldRow(
                        label = stringResource(Res.string.pension_status_establishment_date),
                        value = item.pensionerBaseDate.toFormattedDate(),
                        modifier = Modifier.weight(1f),
                    )
                    InfoFieldRow(
                        label = stringResource(Res.string.pension_status_payment_date),
                        value = item.paymentDate.toFormattedDate(),
                        modifier = Modifier.weight(1f),
                    )
                }

                InfoFieldRow(
                    label = stringResource(Res.string.pension_status_order_type),
                    value = item.pensionerType.toPensionerTypeLabel()
                        ?.let { stringResource(it) }
                        ?: item.pensionerType.orDash(),
                    numeric = false,
                )
            }

            TaminDivider()

            SendCertificateRow(
                enabled = !isSendingCertificate,
                isSending = isSendingCertificate,
                onClick = onSendCertificateClicked,
            )
        }
    }
}

@Composable
private fun StatusHeader(isActive: Boolean) {
    val colors = LocalTaminColors.current
    val headerColor = if (isActive) colors.springGreenText else colors.dangerText
    val headerGradient = Brush.horizontalGradient(
        colors = listOf(
            if (isActive) colors.greenBg else colors.dangerBorder.copy(alpha = 0.12f),
            colors.bgSurface,
        ),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerGradient)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(Spacing.sm)
                    .background(headerColor, CircleShape),
            )
            Text(
                text = stringResource(
                    if (isActive) Res.string.pension_status_active_header
                    else Res.string.pension_status_inactive_header,
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = headerColor,
            )
        }
        OrganizationBadge(isActive = isActive)
    }
}

@Composable
private fun OrganizationBadge(isActive: Boolean) {
    val colors = LocalTaminColors.current
    val accent = if (isActive) colors.springGreenText else colors.dangerText
    val border = if (isActive) colors.greenBorder else colors.dangerBorder
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.max))
            .background(colors.bgSurface)
            .border(Thickness.border, border, RoundedCornerShape(CornerRadius.max))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = vectorResource(
                if (isActive) Res.drawable.ic_tamin_check else Res.drawable.ic_tamin_cross,
            ),
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = stringResource(
                if (isActive) Res.string.active_relation_verified_badge
                else Res.string.pension_status_inactive_badge,
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = accent,
        )
    }
}

@Composable
private fun ProfileRow(item: PensionInquiryPR) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.largePlus)
                .clip(CircleShape)
                .background(colors.chipBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_user),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.medium),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.fullName.orDash(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            if (item.branchName.isNotBlank()) {
                Text(
                    text = stringResource(Res.string.pension_status_org_unit, item.branchName),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun InfoFieldRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)
    DetailRow(
        label = label,
        value = value,
        numeric = numeric,
        modifier = modifier
            .clip(shape)
            .background(colors.bgPage)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalPadding = Spacing.none,
    )
}

@Composable
private fun CopyableNumberBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val copy = rememberCopyAction(value)
    val shape = RoundedCornerShape(CornerRadius.lg)

    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.bgPage)
            .border(Thickness.border, colors.border, shape)
            .then(if (value.isNotBlank()) Modifier.clickable(onClick = copy) else Modifier)
            .padding(Spacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
            NumericText(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        }
        if (value.isNotBlank()) {
            CopyIconButton(
                value = value,
                label = label,
                tint = colors.blueText,
                interactive = false,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun SendCertificateRow(
    enabled: Boolean,
    isSending: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSending) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                strokeWidth = ButtonDimens.loadingIndicatorStroke,
                color = colors.blueText,
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_send),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
            )
        }
        Spacer(modifier = Modifier.size(Spacing.sm))
        Text(
            text = stringResource(Res.string.active_relation_send_certificate),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusCard() {
    PreviewRtlThemeContent {
        PensionStatusCard(
            item = PreviewPensionStatusItem(isActive = true),
            isSendingCertificate = false,
            onSendCertificateClicked = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusCardInactive() {
    PreviewRtlThemeContent {
        PensionStatusCard(
            item = PreviewPensionStatusItem(isActive = false),
            isSendingCertificate = false,
            onSendCertificateClicked = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusCardSending() {
    PreviewRtlThemeContent {
        PensionStatusCard(
            item = PreviewPensionStatusItem(isActive = true),
            isSendingCertificate = true,
            onSendCertificateClicked = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

private fun PreviewPensionStatusItem(isActive: Boolean) = PensionInquiryPR(
    branchCode = "5750",
    insuranceNumber = "0043007196",
    pensionerRisUid = "1003406938",
    pensionerType = "بازنشستگی",
    paymentDate = "14050530",
    pensionerBaseDate = "13881201",
    fullName = "سیدرحمت اله میرفضلی",
    statusDesc = if (isActive) "01" else "02",
    isActive = isActive,
    sexDesc = "",
    branchName = "یک کرج",
    pensionEndDate = "",
    nationalId = "6319889391",
    paymentAmount = "0",
)

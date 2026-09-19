package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.util.toFormattedDate
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_active
import taminx.core.core_ui.active_relation_disconnected_badge
import taminx.core.core_ui.active_relation_inactive
import taminx.core.core_ui.active_relation_insurance_number
import taminx.core.core_ui.active_relation_org_name
import taminx.core.core_ui.active_relation_send_certificate
import taminx.core.core_ui.active_relation_start_date
import taminx.core.core_ui.active_relation_status
import taminx.core.core_ui.active_relation_verified_badge
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_cross

@Composable
internal fun ActiveRelationItemCard(
    item: ActiveRelationPR,
    onSendCertificateClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    val topHeaderGradient = Brush.horizontalGradient(
        colors = listOf(
            if (item.isActive) taminColors.greenBg else taminColors.border.copy(alpha = 0.12f),
            taminColors.bgSurface,
        ),
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = taminColors.bgSurface
        ),
        border = BorderStroke(1.dp, taminColors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (item.isActive) 0.dp else Spacing.lg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topHeaderGradient)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (item.isActive) taminColors.springGreenText else taminColors.textMuted,
                                RoundedCornerShape(50)
                            )
                    )
                    Text(
                        text = if (item.isActive) stringResource(Res.string.active_relation_active) else stringResource(Res.string.active_relation_inactive),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isActive) taminColors.springGreenText else taminColors.textMuted
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    if (item.isVerified && item.isActive) {
                        VerifiedBadge()
                    }
                    if (!item.isActive) {
                        InactiveBadge()
                    }
                }
            }

            TaminDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                InfoRow(label = stringResource(Res.string.active_relation_org_name), value = item.organizationName)
                TaminDivider()
                InfoRow(
                    label = stringResource(Res.string.active_relation_insurance_number),
                    value = item.insuranceId.toPersianDigits(),
                    isNumeric = true
                )
                TaminDivider()
                InfoRow(label = stringResource(Res.string.active_relation_status), value = if(item.isActive) item.relationStatus else stringResource(Res.string.active_relation_inactive))
                if (item.isActive) {
                    TaminDivider()
                    InfoRow(
                        label = stringResource(Res.string.active_relation_start_date),
                        value = item.startDate.toFormattedDate(),
                        isNumeric = true
                    )
                }
            }

            if (item.isActive) {
                TaminDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSendCertificateClicked() }
                        .padding(Spacing.lg),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_send),
                        contentDescription = null,
                        tint = taminColors.blueText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.active_relation_send_certificate),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.blueText
                    )
                }

            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isNumeric: Boolean = false,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textSecondary
        )
        if (isNumeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun VerifiedBadge(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    CustomChip(
        text = stringResource(Res.string.active_relation_verified_badge),
        modifier = modifier,
        containerColor = taminColors.bgSurface,
        textColor = taminColors.springGreenText,
        border = BorderStroke(width = 1.dp, color = taminColors.greenBorder),
        icon = vectorResource(Res.drawable.ic_tamin_check)
    )
}

@Composable
private fun InactiveBadge(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    CustomChip(
        text = stringResource(Res.string.active_relation_disconnected_badge),
        modifier = modifier,
        containerColor = taminColors.bgPage,
        textColor = taminColors.textMuted,
        border = BorderStroke(width = 1.dp, color = taminColors.border),
        icon = vectorResource(Res.drawable.ic_tamin_cross)
    )
}

@PreviewRtlTheme
@Composable
private fun PreviewActiveRelationItemCard() {
    PreviewRtlThemeContent {
        ActiveRelationItemCard(
            item = ActiveRelationPR(
                id = 1,
                organizationName = "شعبه ۴ تبریز",
                insuranceId = "۰۰۴۱۷۳۶۲۲۷",
                branchCode = "5750",
                relationStatus = "اصلی - بیمه‌پرداز - اجتماعی خاص - کارگران ساختمانی",
                startDate = "۱۴۰۵/۰۴/۰۱",
                endDate = null,
                isActive = true,
                isVerified = true
            ),
            onSendCertificateClicked = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInactiveRelationItemCard() {
    PreviewRtlThemeContent {
        ActiveRelationItemCard(
            item = ActiveRelationPR(
                id = 2,
                organizationName = "شعبه ۶ تبریز",
                insuranceId = "۰۰۴۱۷۳۶۲۲۷",
                branchCode = "5751",
                relationStatus = "فاقد ارتباط فعال",
                startDate = "۱۴۰۲/۰۱/۰۱",
                endDate = "۱۴۰۴/۰۱/۰۱",
                isActive = false,
                isVerified = false
            ),
            onSendCertificateClicked = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewActiveRelationIItemCardDark() {
    TaminHamrahTheme(darkTheme = true) {
        ActiveRelationItemCard(
            item = ActiveRelationPR(
                id = 1,
                organizationName = "شعبه ۴ تبریز",
                insuranceId = "۰۰۴۱۷۳۶۲۲۷",
                branchCode = "5750",
                relationStatus = "اصلی - بیمه‌پرداز - اجتماعی خاص - کارگران ساختمانی",
                startDate = "۱۴۰۵/۰۴/۰۱",
                endDate = null,
                isActive = true,
                isVerified = true
            ),
            onSendCertificateClicked = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInActiveRelationIItemCardDark() {
    TaminHamrahTheme(darkTheme = true) {
        ActiveRelationItemCard(
            item = ActiveRelationPR(
                id = 2,
                organizationName = "شعبه ۶ تبریز",
                insuranceId = "۰۰۴۱۷۳۶۲۲۷",
                branchCode = "5751",
                relationStatus = "فاقد ارتباط فعال",
                startDate = "۱۴۰۲/۰۱/۰۱",
                endDate = "۱۴۰۴/۰۱/۰۱",
                isActive = false,
                isVerified = false
            ),
            onSendCertificateClicked = {}
        )
    }
}

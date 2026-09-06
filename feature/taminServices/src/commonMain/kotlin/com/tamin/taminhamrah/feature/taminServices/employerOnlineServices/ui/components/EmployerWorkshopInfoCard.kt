package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_contract_rows_workshop_code
import taminx.core.core_ui.ic_tamin_workshop

/**
 * The workshop identity card that straddles the bottom of the contract-rows header — name on top,
 * «کد کارگاه <code>» under it, a glassy workshop-icon tile on the leading (right) edge. Values are
 * pre-formatted by the caller.
 */
@Composable
internal fun EmployerWorkshopInfoCard(
    workshopName: String,
    workshopCodeLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(Color.Transparent)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.card))
            .padding(vertical = Spacing.md, horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(CornerRadius.xl))
                .background(colors.chipBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_workshop),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = workshopName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.employer_online_services_contract_rows_workshop_code),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
                NumericText(
                    text = workshopCodeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                )
            }
        }
    }
}

private const val PREVIEW_NAME = "شرکت صنایع دما بخار مشهد"
private const val PREVIEW_CODE = "۰۰۸۱۶۳۱۸۲۹"

@PreviewRtlTheme
@Composable
private fun EmployerWorkshopInfoCardPreviewLight() {
    PreviewRtlThemeContent {
        EmployerWorkshopInfoCard(
            workshopName = PREVIEW_NAME,
            workshopCodeLabel = PREVIEW_CODE,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerWorkshopInfoCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerWorkshopInfoCard(
            workshopName = PREVIEW_NAME,
            workshopCodeLabel = PREVIEW_CODE,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

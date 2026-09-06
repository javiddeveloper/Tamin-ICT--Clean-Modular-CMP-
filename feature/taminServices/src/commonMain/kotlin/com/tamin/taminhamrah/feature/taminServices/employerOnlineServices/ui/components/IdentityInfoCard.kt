package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.IdentityCardPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_full_name
import taminx.core.core_ui.employer_online_services_national_code

/**
 * The کارفرما identity card that sits over the bottom of the header: full name on one side, national
 * code on the other, a hairline between them — value on top, muted label under it, matching the
 * design. Both fields arrive pre-formatted from [IdentityCardPR].
 */
@Composable
internal fun IdentityInfoCard(
    identity: IdentityCardPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(vertical = Spacing.lg, horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IdentityField(
            value = identity.fullName,
            label = stringResource(Res.string.employer_online_services_full_name),
            numeric = false,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(colors.border),
        )
        IdentityField(
            value = identity.nationalCode,
            label = stringResource(Res.string.employer_online_services_national_code),
            numeric = true,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IdentityField(
    value: String,
    label: String,
    numeric: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.padding(horizontal = Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        if (numeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
    }
}

private val PreviewIdentity = IdentityCardPR(
    fullName = "حسین توکلی کرمانی",
    nationalCode = "۴۴۷۹۸۹۰۸۸۲",
)

@PreviewRtlTheme
@Composable
private fun IdentityInfoCardPreviewLight() {
    PreviewRtlThemeContent {
        IdentityInfoCard(identity = PreviewIdentity, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun IdentityInfoCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        IdentityInfoCard(identity = PreviewIdentity, modifier = Modifier.padding(Spacing.lg))
    }
}

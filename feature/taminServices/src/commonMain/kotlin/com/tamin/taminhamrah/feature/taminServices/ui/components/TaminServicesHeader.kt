package com.tamin.taminhamrah.feature.taminServices.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme

@Composable
fun TaminServicesHeader(
    title: String,
    badgeText: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(color = LocalTaminColors.current.textSecondary),
            fontWeight = FontWeight.SemiBold
        )

        if (!badgeText.isNullOrEmpty()) {
            CustomChip(
                containerColor = LocalTaminColors.current.chipBg,
                text = badgeText, border = BorderStroke(
                    width = 1.dp,
                    color = LocalTaminColors.current.hawkesBlue
                )
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminServicesHeaderPreview() {
    PreviewRtlThemeContent {
        TaminServicesHeader(
            title = "خدمات بیمه‌شدگان",
            badgeText = "۲۴ خدمت",
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminServicesHeaderPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        TaminServicesHeader(
            title = "خدمات بیمه‌شدگان",
            badgeText = "۲۴ خدمت",
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}


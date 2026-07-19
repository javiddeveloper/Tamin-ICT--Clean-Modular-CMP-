package com.tamin.taminhamrah.ui.components.khadamat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun KhadamatHeader(
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
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!badgeText.isNullOrEmpty()) {
            CustomChip(text = badgeText)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun KhadamatHeaderPreview() {
    PreviewRtlThemeContent {
        KhadamatHeader(
            title = "خدمات بیمه‌شدگان",
            badgeText = "۲۴ خدمت",
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}

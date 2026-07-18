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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CuustomChip
import com.tamin.taminhamrah.ui.theme.TaminLightTextTertiary

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
            color = TaminLightTextTertiary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!badgeText.isNullOrEmpty()) {
            CuustomChip(text = badgeText, borderWidth = 1.dp, borderColor = Color(0xFFCBD7EC))
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
            modifier = Modifier.padding(16.dp)
        )
    }
}

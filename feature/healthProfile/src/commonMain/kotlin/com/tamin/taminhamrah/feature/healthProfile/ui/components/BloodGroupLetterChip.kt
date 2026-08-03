package com.tamin.taminhamrah.feature.healthProfile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun BloodGroupLetterChip(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val backgroundColor = if (selected) taminColors.blueBg else taminColors.bgSurface
    val borderColor = if (selected) taminColors.blueText else taminColors.border
    val textColor = if (selected) taminColors.blueText else taminColors.textPrimary

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
fun BloodGroupChipsRow(
    letters: List<String>,
    selectedLetter: String?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onLetterSelected: (String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        letters.forEach { letter ->
            BloodGroupLetterChip(
                label = letter,
                selected = letter == selectedLetter,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = { onLetterSelected(letter) }
            )
        }
    }
}

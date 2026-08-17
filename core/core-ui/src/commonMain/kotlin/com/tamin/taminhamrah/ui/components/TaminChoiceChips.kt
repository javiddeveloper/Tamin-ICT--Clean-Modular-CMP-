package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * Interactive filter choice chips wrapping nicely inside containers.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveChoiceChips(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndices: Set<Int>,
    onSelectionChanged: (Set<Int>) -> Unit,
    activeColor: Color = LocalTaminColors.current.blueText,
    activeBgColor: Color = LocalTaminColors.current.blueBg,
) {
    val taminColors = LocalTaminColors.current

    // Custom Flow layout using Compose row wrapping
    // Multiplatform standard wrap
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = selectedIndices.contains(index)
            val chipBgColor = if (isSelected) activeBgColor else Color.Transparent
            val chipBorderColor = if (isSelected) activeColor else taminColors.border
            val chipTextColor = if (isSelected) activeColor else taminColors.textPrimary

            Box(
                modifier = Modifier
                    .background(chipBgColor, RoundedCornerShape(100.dp))
                    .border(
                        BorderStroke(if (isSelected) 1.5.dp else 1.dp, chipBorderColor),
                        RoundedCornerShape(100.dp)
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .clickable {
                        val newSelection = selectedIndices.toMutableSet()
                        if (newSelection.contains(index)) {
                            newSelection.remove(index)
                        } else {
                            newSelection.add(index)
                        }
                        onSelectionChanged(newSelection)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TaminText(
                    text = label.replace(Regex("\\r?\\n"), " ").trim(),
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = chipTextColor
                )
            }
        }
    }
}

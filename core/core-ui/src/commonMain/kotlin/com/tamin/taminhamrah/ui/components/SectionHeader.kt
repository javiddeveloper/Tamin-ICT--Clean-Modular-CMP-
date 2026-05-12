package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier) {
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(bottom = Spacing.sm))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun PersonInfoCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = LocalTaminColors.current.textPrimary,
            )
        }
        content()
    }
}

@Composable
fun PersonInfoGridItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted,
        )

        if (numeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = taminColors.textPrimary,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = taminColors.textPrimary,
            )
        }
    }
}

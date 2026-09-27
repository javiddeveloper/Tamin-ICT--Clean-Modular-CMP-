package com.tamin.taminhamrah.feature.history.ui.jobinfo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.CopyCodeChip
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/** A label above its value in the shared copy chip. */
@Composable
fun InfoChip(
    label: String,
    value: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LocalTaminColors.current.textMuted,
        )
        CopyCodeChip(
            text = value,
            onCopy = { onCopy(value) },
            contentDescription = label,
        )
    }
}

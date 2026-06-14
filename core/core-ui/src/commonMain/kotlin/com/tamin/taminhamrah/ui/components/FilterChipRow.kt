package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList

/**
 * A horizontal row of [FilterChip]s for selecting a single value from [options].
 *
 * @param options The list of selectable values.
 * @param selected The currently selected value.
 * @param onSelect Called when the user taps a chip.
 * @param label Maps each option to its display string.
 */
@Composable
fun <T> FilterChipRow(
    options: ImmutableList<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(label(option), style = MaterialTheme.typography.labelSmall) },
            )
        }
    }
}

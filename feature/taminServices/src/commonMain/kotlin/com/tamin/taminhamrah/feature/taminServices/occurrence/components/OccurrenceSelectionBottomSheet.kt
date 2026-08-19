package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

/**
 * One selectable choice inside an [OccurrenceSelectionBottomSheet].
 *
 * [id] is the value stored back into the step state on selection — for options that don't carry
 * a real code (e.g. marital status), the display [title] itself is used as [id], matching how the
 * rest of the occurrence steps already key on the localized string.
 */
@Immutable
data class OccurrenceSheetOption(
    val id: String,
    val title: String,
    val subtitle: String? = null,
)

/**
 * Reusable single-select bottom sheet for the occurrence flow's picker fields (workshop, marital
 * status, accident outcome, document type) — a titled list of bordered, radio-marked option cards
 * that selects and dismisses on tap, no submit button.
 */
@Composable
fun OccurrenceSelectionBottomSheet(
    title: String,
    options: List<OccurrenceSheetOption>,
    selectedId: String?,
    onSelect: (OccurrenceSheetOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgPage,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminText(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )
            options.forEach { option ->
                OccurrenceSelectionOptionRow(
                    option = option,
                    selected = option.id == selectedId,
                    onClick = { onSelect(option) },
                )
            }
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun OccurrenceSelectionOptionRow(
    option: OccurrenceSheetOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val borderColor = if (selected) taminColors.blueText else taminColors.border

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(color = taminColors.bgSurface, shape = RoundedCornerShape(CornerRadius.lg))
            .border(
                BorderStroke(if (selected) 1.5.dp else Thickness.border, borderColor),
                RoundedCornerShape(CornerRadius.lg),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = taminColors.blueText,
                unselectedColor = taminColors.border,
            ),
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            TaminText(
                text = option.title,
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textPrimary,
            )
            option.subtitle?.let { subtitle ->
                TaminText(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

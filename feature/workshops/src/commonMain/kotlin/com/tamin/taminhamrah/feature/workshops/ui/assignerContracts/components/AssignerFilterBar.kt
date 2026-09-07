package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_filter_clear
import taminx.core.core_ui.assigner_filter_label
import taminx.core.core_ui.ic_tamin_cross

/** What the design joins the applied filter's parts with. */
private const val FilterSeparator = " · "

/**
 * The applied search, as one line: «کد کارگاه X · کد شعبه Y · ردیف Z», blanks dropped.
 *
 * Pure and separate from the composable so the join is testable without a resource loader — the
 * three labels arrive already resolved.
 */
internal fun buildAssignerFilterText(
    workshop: String,
    branch: String?,
    row: String?,
): String = listOfNotNull(
    workshop.takeIf { it.isNotBlank() },
    branch?.takeIf { it.isNotBlank() },
    row?.takeIf { it.isNotBlank() },
).joinToString(FilterSeparator)

/**
 * Which workshop the list is showing, and the one control that drops it.
 *
 * The chip itself is inert; the design reopens the search from the toolbar's magnifier and from
 * the empty state's button, and «حذف» clears the filter rather than editing it.
 */
@Composable
fun AssignerFilterBar(
    filterText: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.assigner_filter_label),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Text(
            text = filterText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .background(colors.blueBg)
                .border(Thickness.border, colors.blueBorder, CircleShape)
                .padding(
                    horizontal = WorkshopDimens.chipHorizontalPadding,
                    vertical = WorkshopDimens.chipVerticalPadding,
                ),
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, CircleShape)
                .clickable(onClick = onClear)
                .padding(
                    horizontal = WorkshopDimens.chipHorizontalPadding,
                    vertical = WorkshopDimens.chipVerticalPadding,
                ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_cross),
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(WorkshopDimens.chipCrossSize),
            )
            Text(
                text = stringResource(Res.string.assigner_filter_clear),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                maxLines = 1,
            )
        }
    }
}

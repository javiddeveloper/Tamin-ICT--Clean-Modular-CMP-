package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_filter_change
import taminx.core.core_ui.assigner_filter_clear
import taminx.core.core_ui.ic_tamin_cross

/** What the design joins the applied filter's parts with. */
internal const val FilterSeparator = " · "

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
 * Which workshop the list is showing, drawn as ردیف‌های پیمان draws its own: a blue chip carrying the
 * line, and «تغییر» beside it to reopen the search.
 *
 * @param onClear drops the search from inside the chip. Null draws no cross, for a line that names
 *   the واگذارنده rather than a search there is anything to clear.
 */
@Composable
fun AssignerFilterBar(
    filterText: String,
    onEdit: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .background(colors.blueBg)
                .border(Thickness.border, colors.blueBorder, CircleShape)
                .padding(
                    horizontal = WorkshopDimens.chipHorizontalPadding,
                    vertical = WorkshopDimens.chipVerticalPadding,
                ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = filterText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.blueText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (onClear != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClear)
                        .padding(Spacing.xxs),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = stringResource(Res.string.assigner_filter_clear),
                        tint = colors.blueText,
                        modifier = Modifier.size(WorkshopDimens.chipCrossSize),
                    )
                }
            }
        }
        Text(
            text = stringResource(Res.string.assigner_filter_change),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            maxLines = 1,
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, CircleShape)
                .clickable(onClick = onEdit)
                .padding(
                    horizontal = WorkshopDimens.chipHorizontalPadding,
                    vertical = WorkshopDimens.chipVerticalPadding,
                ),
        )
    }
}

package com.tamin.taminhamrah.feature.workshops.ui.contractRows.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowTab
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_filter_change
import taminx.core.core_ui.contract_rows_read_only

/**
 * The two services, as a segmented control.
 *
 * [ContractRowTab] declares the order and the copy, so this draws whatever the table holds rather
 * than naming either tab itself.
 */
@Composable
fun ContractRowTabs(
    selected: ContractRowTab,
    onSelect: (ContractRowTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val stripShape = remember { RoundedCornerShape(CornerRadius.xl) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(stripShape)
            .background(colors.bgPage)
            .border(Thickness.border, colors.border, stripShape)
            .padding(WorkshopDimens.contractRowTabStripPadding),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTabGap),
    ) {
        ContractRowTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(WorkshopDimens.contractRowTabHeight)
                    .clip(RoundedCornerShape(CornerRadius.listRow))
                    .background(if (isSelected) colors.bgSurface else Color.Transparent)
                    .clickable { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(tab.label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) colors.textPrimary else colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(tab.hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Which workshop is in force, and what the list is.
 *
 * The «این فهرست فقط برای مشاهده است.» line is the design's own, and it is the only thing on the
 * screen that says the rows are inert — the cards carry no chevron and no ripple, so without it a
 * user would be left tapping to find out.
 */
@Composable
fun ContractRowFilterBar(
    filterText: String,
    countText: String,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
            Text(
                text = stringResource(Res.string.contract_rows_filter_change),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                maxLines = 1,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.bgSurface)
                    .border(Thickness.border, colors.border, CircleShape)
                    .clickable(onClick = onChange)
                    .padding(
                        horizontal = WorkshopDimens.chipHorizontalPadding,
                        vertical = WorkshopDimens.chipVerticalPadding,
                    ),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(IconSize.small),
            )
            Text(
                text = stringResource(Res.string.contract_rows_read_only),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
            )
        }
    }
}

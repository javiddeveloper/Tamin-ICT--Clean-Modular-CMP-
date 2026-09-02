package com.tamin.taminhamrah.feature.workshops.ui.contractRows.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_address
import taminx.core.core_ui.contract_rows_badge
import taminx.core.core_ui.contract_rows_commitment_date
import taminx.core.core_ui.contract_rows_email
import taminx.core.core_ui.contract_rows_mobile
import taminx.core.core_ui.contract_rows_workshop_number

/**
 * One ردیف پیمان.
 *
 * Deliberately not [com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard]: the
 * sibling کارگاه screens draw a stack of label/value rows with dividers, and the design draws this
 * one as a grid of filled tiles under a heading and a badge. That is a different card in the mock,
 * not a variant of the same one, so bending the shared card into it would cost every other screen
 * a flag it does not want.
 *
 * The card is inert by design — no ripple, no chevron, no click target. The old app wired an
 * `onItemClickListener` into both of its adapters and never called it; this reproduces what the
 * screen actually does rather than what its plumbing implied.
 *
 * [showContact] follows the tab, not the row: only the تعهدنامه‌دار service sends the contact
 * columns, so on the other tab the block is absent rather than dashed.
 */
@Composable
fun ContractRowCard(
    row: ContractRowPR,
    showContact: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(
                horizontal = WorkshopDimens.contractRowCardHorizontalPadding,
                vertical = WorkshopDimens.contractRowCardVerticalPadding,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = row.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = stringResource(Res.string.contract_rows_badge, row.rowLabel),
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                borderColor = colors.blueBorder,
                fontWeight = FontWeight.Bold,
                verticalPadding = WorkshopDimens.contractRowBadgeVerticalPadding,
            )
        }

        TileRow(modifier = Modifier.padding(top = Spacing.sm)) {
            Tile(
                label = stringResource(Res.string.contract_rows_workshop_number),
                value = row.workshopCodeLabel,
                modifier = Modifier.weight(1f),
            )
            Tile(
                label = stringResource(Res.string.contract_rows_commitment_date),
                value = row.commitmentDate,
                modifier = Modifier.weight(1f),
            )
        }

        if (showContact) {
            TileRow(modifier = Modifier.padding(top = WorkshopDimens.contractRowTileGap)) {
                Tile(
                    label = stringResource(Res.string.contract_rows_mobile),
                    value = row.mobile,
                    modifier = Modifier.weight(1f),
                )
                Tile(
                    label = stringResource(Res.string.contract_rows_email),
                    value = row.email,
                    modifier = Modifier.weight(1f),
                )
            }

            // Dropped entirely when the service sent no address: a full-width dash claims more
            // about the gap than the design does.
            if (row.address.isNotBlank()) {
                Tile(
                    label = stringResource(Res.string.contract_rows_address),
                    value = row.address,
                    numeric = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = WorkshopDimens.contractRowTileGap),
                )
            }
        }
    }
}

@Composable
private fun TileRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTileGap),
        content = content,
    )
}

/**
 * One filled cell of the card: a muted caption over the value.
 *
 * [numeric] false is the address, the only value on this card that is prose. Everything else is a
 * code, a date or a contact detail, all of which read left-to-right even on this right-to-left
 * page — a workshop number laid out RTL reads back to front.
 */
@Composable
private fun Tile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgPage)
            .padding(
                horizontal = WorkshopDimens.contractRowTileHorizontalPadding,
                vertical = WorkshopDimens.contractRowTileVerticalPadding,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        val valueStyle = MaterialTheme.typography.labelMedium
        if (numeric) {
            NumericText(
                text = value,
                style = valueStyle.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                text = value,
                style = valueStyle,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

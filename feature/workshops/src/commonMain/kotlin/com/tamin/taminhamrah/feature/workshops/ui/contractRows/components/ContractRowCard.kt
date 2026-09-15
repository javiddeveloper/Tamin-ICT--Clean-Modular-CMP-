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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.StringResource
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
 * On ردیف‌های پیمان the card is inert — no ripple, no chevron, no click target. The old app wired
 * an `onItemClickListener` into both of its adapters and never called it; that reproduces what the
 * screen actually does rather than what its plumbing implied. واگذارندگان offers its two
 * destinations as [buttons] on the card itself, which is what every other کارگاه card here does.
 *
 * [showContact] follows the tab, not the row: only the تعهدنامه‌دار service sends the contact
 * columns, so on the other tab the block is absent rather than dashed.
 *
 * @param dateLabel what the second tile is called. ردیف‌های پیمان reads تاریخ تعهد off the
 *   agreement; واگذارندگان reads تاریخ قرارداد off the پیمان, and they are different columns.
 * @param buttons the card's own actions, laid out in one equal-width row the way
 *   [com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard] lays out its own.
 *   Null — the default — leaves the card inert, which is what ردیف‌های پیمان wants.
 */
@Composable
fun ContractRowCard(
    row: ContractRowPR,
    showContact: Boolean,
    modifier: Modifier = Modifier,
    dateLabel: StringResource = Res.string.contract_rows_commitment_date,
    buttons: (@Composable RowScope.() -> Unit)? = null,
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
                label = stringResource(dateLabel),
                value = row.commitmentDate,
                modifier = Modifier.weight(1f),
            )
        }

        if (showContact) {
            // Blank, not dashed, is how a service that never sends these columns at all is told
            // apart from one that sent them empty. ردیف‌های پیمان dashes them, so this row always
            // draws there; واگذارندگان leaves them blank and the row is dropped entirely rather
            // than printing two permanent «—» tiles.
            if (row.mobile.isNotBlank() || row.email.isNotBlank()) {
                TileRow(modifier = Modifier.padding(top = WorkshopDimens.contractRowTileGap)) {
                    Tile(
                        label = stringResource(Res.string.contract_rows_mobile),
                        value = row.mobile,
                        modifier = Modifier.weight(1f),
                    )
                    Tile(
                        label = stringResource(Res.string.contract_rows_email),
                        value = row.email,
                        // An address, not a number. The theme's `ss01` would paint its digits as
                        // Persian glyphs, so `…۲۰۲۰@gmail.com` is what the user reads back
                        // and retypes — and it is not the address. Confirmed against a live row.
                        latinDigits = true,
                        // The design ellipses this one cell rather than wrapping it.
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
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

        if (buttons != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WorkshopDimens.cardButtonsTopMargin),
                horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.cardButtonGap),
                content = buttons,
            )
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
    /**
     * Drops the theme's `ss01`, so ASCII digits stay Latin.
     *
     * For the one value on this card that is an identifier rather than a quantity. `"tnum"`
     * *replaces* the default feature list — it does not add to it — which is exactly the effect
     * wanted here.
     */
    latinDigits: Boolean = false,
    singleLine: Boolean = false,
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
        val base = MaterialTheme.typography.labelMedium
        val valueStyle = if (latinDigits) base.copy(fontFeatureSettings = "tnum") else base
        // Same job as `NumericText` — force left-to-right so a code does not read back to front —
        // but inline, because this cell also needs a line limit and widening the shared component
        // would touch every one of its callers for one screen's sake.
        val direction = if (numeric) LayoutDirection.Ltr else LocalLayoutDirection.current
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            Text(
                text = value,
                style = valueStyle,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = if (numeric) Modifier.fillMaxWidth() else Modifier,
            )
        }
    }
}

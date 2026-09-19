package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_my_workshops

/**
 * The parts every کارگاه bottom sheet in this design opens with.
 *
 * Every sheet here switches `ModalBottomSheet`'s own `dragHandle` off and draws the design's
 * grabber instead, then a centred title and an optional line under it. Three sheets drew that same
 * opening; this is it declared once.
 */

/** The design's own grabber — `width:44px; height:4px; border-radius:100px`. */
@Composable
fun WorkshopSheetGrabber(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .size(
                width = WorkshopDimens.contractRowGrabberWidth,
                height = WorkshopDimens.contractRowGrabberHeight,
            )
            .background(colors.border, RoundedCornerShape(CornerRadius.full)),
    )
}

/**
 * A sheet's body: page insets, the navigation-bar inset, the grabber, a title, and its content.
 *
 * The column scrolls because a sheet that does not can push its own primary button past the bottom
 * edge on a short screen — a bug this design already shipped once. Every sheet here holds a bounded
 * number of rows, so a plain scroll is right; a lazy list inside a sheet that already scrolls would
 * nest two scrollers.
 */
@Composable
fun WorkshopSheetBody(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page)
            .padding(top = Spacing.smd, bottom = Spacing.page)
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        WorkshopSheetGrabber(modifier = Modifier.padding(bottom = Spacing.smd))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
        content()
    }
}

/**
 * کارگاه‌های شما — the employer's own workshops, offered above a sheet's code fields.
 *
 * Both search sheets under کارگاه‌های کارفرما ask for a کد کارگاه, and typing a ten-digit number
 * from memory is the worst part of either. Picking a row fills the code *and* its branch in one go.
 *
 * Absent rather than empty when there is nothing to offer: a heading over no rows reads as a list
 * that failed to load. [total] is the employer's real count — the sheet asks for one page, so when
 * it holds more the shortfall is stated rather than paged away, because the fields above still
 * reach any workshop by number and a list that silently stops at ten looks complete.
 */
@Composable
fun WorkshopQuickPickList(
    workshops: ImmutableList<WorkshopPR>,
    total: Int,
    selectedWorkshopId: String,
    onPick: (workshopId: String, branchCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (workshops.isEmpty()) return
    val colors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.contract_rows_my_workshops),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.smd, bottom = Spacing.sm),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTileGap),
        ) {
            workshops.forEach { workshop ->
                WorkshopQuickPickRow(
                    name = workshop.name,
                    codeLabel = workshop.codeLabel,
                    isSelected = workshop.workshopId == selectedWorkshopId,
                    onPick = { onPick(workshop.workshopId, workshop.branchCode) },
                )
            }
        }
    }
}

/** One کارگاه‌های شما row: picking it fills both codes at once, branch included. */
@Composable
private fun WorkshopQuickPickRow(
    name: String,
    codeLabel: String,
    isSelected: Boolean,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(CornerRadius.chip) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isSelected) colors.blueBg else colors.bgSurface, shape)
            .border(
                Thickness.border,
                if (isSelected) colors.blueBorder else colors.border,
                shape,
            )
            .clickable(onClick = onPick)
            .padding(
                horizontal = WorkshopDimens.fieldHorizontalPadding,
                vertical = WorkshopDimens.fieldVerticalPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        NumericText(
            text = codeLabel,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textMuted,
        )
    }
}

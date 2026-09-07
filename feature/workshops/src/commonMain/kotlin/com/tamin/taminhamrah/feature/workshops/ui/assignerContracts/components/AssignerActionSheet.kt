package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSheetBody
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_action_bases_unavailable
import taminx.core.core_ui.assigner_contract_subtitle
import taminx.core.core_ui.assigner_action_title
import taminx.core.core_ui.assigner_action_view_bases
import taminx.core.core_ui.assigner_action_view_detail
import taminx.core.core_ui.ic_tamin_computational_base
import taminx.core.core_ui.ic_tamin_workshop_contract_rows

/**
 * The two things a پیمان can be opened as.
 *
 * The design draws a third row, «ثبت درخواست صدور مفاصاحساب», which leaves this feature for a
 * multi-step form of its own with its own endpoints (`mad38-head`, `mad38-detail`,
 * `contractSubject-request-issuance-invoices38`). That is a separate ticket, and the row is absent
 * rather than present-and-dead: a button that does nothing is worse than one that is not there.
 *
 * @param canOpenBases false when the پیمان is missing one of the four keys the bases call is
 *   addressed with. The row is then disabled and says why, rather than opening a screen onto
 *   another contract's records.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignerActionSheet(
    workshopName: String,
    rowLabel: String,
    canOpenBases: Boolean,
    onViewDetail: () -> Unit,
    onViewBases: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
    ) {
        AssignerActionSheetContent(
            workshopName = workshopName,
            rowLabel = rowLabel,
            canOpenBases = canOpenBases,
            onViewDetail = onViewDetail,
            onViewBases = onViewBases,
        )
    }
}

/**
 * The sheet's body, apart from the sheet.
 *
 * A [ModalBottomSheet] renders as a full-screen scrim in a preview, so what is worth previewing
 * lives here and the wrapper above stays a shell.
 */
@Composable
fun AssignerActionSheetContent(
    workshopName: String,
    rowLabel: String,
    canOpenBases: Boolean,
    onViewDetail: () -> Unit,
    onViewBases: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val detailIcon = painterResource(Res.drawable.ic_tamin_workshop_contract_rows)
    val basesIcon = painterResource(Res.drawable.ic_tamin_computational_base)
    val detailLabel = stringResource(Res.string.assigner_action_view_detail)
    val basesLabel = stringResource(Res.string.assigner_action_view_bases)
    val basesUnavailable = stringResource(Res.string.assigner_action_bases_unavailable)

    // Built once per set of inputs rather than per recomposition: a fresh list — or a fresh
    // ListItemData inside an unchanged one — costs ListGroupView its ability to skip.
    val items = remember(
        detailIcon, basesIcon, detailLabel, basesLabel, basesUnavailable,
        canOpenBases, onViewDetail, onViewBases, colors,
    ) {
        persistentListOf(
            ListItemData(
                title = detailLabel,
                leadingIconPainter = detailIcon,
                leadingIconShape = ActionIconShape,
                showArrow = false,
                onClick = onViewDetail,
                colors = ListItemColors(
                    leadingIconBackgroundColor = colors.blueBg,
                    leadingIconTintColor = colors.blueText,
                ),
            ),
            ListItemData(
                title = basesLabel,
                // The reason travels as the row's own subtitle: the design has nowhere else to
                // put it, and a greyed row with no explanation reads as a bug.
                subtitle = basesUnavailable.takeUnless { canOpenBases },
                leadingIconPainter = basesIcon,
                leadingIconShape = ActionIconShape,
                showArrow = false,
                enabled = canOpenBases,
                onClick = onViewBases,
                colors = ListItemColors(
                    leadingIconBackgroundColor = colors.tealBg,
                    leadingIconTintColor = colors.teal,
                ),
            ),
        )
    }

    WorkshopSheetBody(
        title = stringResource(Res.string.assigner_action_title),
        subtitle = stringResource(Res.string.assigner_contract_subtitle, workshopName, rowLabel),
        modifier = modifier,
    ) {
        ListGroupView(
            items = items,
            containerBackgroundColor = colors.bgSurface,
            showDividers = true,
            itemContentPadding = ActionRowPadding,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.smd),
        )
    }
}

/** `border-radius:13px` on the design's 38px action glyph tile. */
private val ActionIconShape = RoundedCornerShape(CornerRadius.md)

/** `min-height:54px; padding:9px 13px` — the action rows breathe more than a plain list row. */
private val ActionRowPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.smd)

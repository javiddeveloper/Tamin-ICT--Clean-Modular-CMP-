package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.LabeledBlock
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.StatTileStyle
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_total
import taminx.core.core_ui.detail_action_date
import taminx.core.core_ui.detail_center
import taminx.core.core_ui.detail_cost_breakdown
import taminx.core.core_ui.detail_date
import taminx.core.core_ui.detail_diagnosis
import taminx.core.core_ui.detail_doctor_note
import taminx.core.core_ui.detail_dose
import taminx.core.core_ui.detail_normal_range
import taminx.core.core_ui.detail_patient_share
import taminx.core.core_ui.detail_prescribed
import taminx.core.core_ui.detail_received
import taminx.core.core_ui.detail_result
import taminx.core.core_ui.detail_tracking_code
import taminx.core.core_ui.detail_visit_reason
import taminx.core.core_ui.share_organization
import taminx.core.core_ui.share_yours

/**
 * Cards for a single medical record's detail screen. The record type decides which of
 * them a screen shows: prescriptions list [PrescriptionItemCard]s, visits show a
 * [VisitSummaryCard], paraclinic records list [LabTestCard]s. All of them sit under a
 * shared [RecordSummaryCard] and above a [CostBreakdownCard].
 */

/** Identity block at the top of the detail screen: center, tracking code and date. */
@Composable
fun RecordSummaryCard(
    metaLabel: String,
    metaValue: String,
    trackingCode: String,
    date: String,
    modifier: Modifier = Modifier,
    /** The tracking code in ASCII digits — what the clipboard gets, not the Persian rendering. */
    trackingCodeRaw: String = trackingCode,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        DetailRow(label = metaLabel, value = metaValue, numeric = false)
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.xxs))
        DetailRow(
            label = stringResource(Res.string.detail_tracking_code),
            value = trackingCode,
            copyValue = trackingCodeRaw,
        )
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.xxs))
        DetailRow(label = stringResource(Res.string.detail_date), value = date)
    }
}

/**
 * One dispensed drug: its name, the prescriber's instructions, and the prescribed
 * versus actually-received quantities side by side.
 */
@Composable
fun PrescriptionItemCard(
    name: String,
    dose: String,
    prescribedCount: String,
    receivedCount: String,
    modifier: Modifier = Modifier,
    // Optional, from the old app's per-item detail: dispensing center, action date and the cost
    // split. Old app labels: سهم بیمار = ssoPayment, سهم سازمان = insurancePayment, جمع کل = sumPriceItem.
    centerName: String = "",
    actionDate: String = "",
    itemTotal: String = "",
    patientShare: String = "",
    organizationShare: String = "",
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        // Drug names are long, Latin and easy to mistype — the one field on this card someone
        // actually needs to carry somewhere else. The whole line copies, not just the glyph.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = rememberCopyAction(name)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = colors.blueText,
                modifier = Modifier.weight(1f),
            )
            CopyIconButton(
                value = name,
                tint = colors.blueText,
                interactive = false,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgPage, RoundedCornerShape(CornerRadius.chip))
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            LabeledBlock(label = stringResource(Res.string.detail_dose), value = dose)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            // Caption beside the figure, not over it: two words and two digits do not need two
            // lines, and the card is shorter for it.
            StatTile(
                label = stringResource(Res.string.detail_prescribed),
                amount = prescribedCount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
                style = StatTileStyle.Inline,
            )
            StatTile(
                label = stringResource(Res.string.detail_received),
                amount = receivedCount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
                style = StatTileStyle.Inline,
            )
        }

        // Where and when, from the old app's per-item detail. Each shown only when present.
        if (centerName.isNotBlank() || actionDate.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgPage, RoundedCornerShape(CornerRadius.chip))
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                if (centerName.isNotBlank()) DetailRow(label = stringResource(Res.string.detail_center), value = centerName)
                if (actionDate.isNotBlank()) DetailRow(label = stringResource(Res.string.detail_action_date), value = actionDate)
            }
        }

        // The item's own cost split, in the same three tiles the timeline and the record total use
        // — just smaller. Three stacked rows of digits said the same thing in three times the
        // height, and did not read as the same quantity as the figures above.
        if (itemTotal.isNotBlank() || patientShare.isNotBlank() || organizationShare.isNotBlank()) {
            CostSplitTiles(
                insuredShareLabel = stringResource(Res.string.detail_patient_share),
                insuredShareAmount = patientShare,
                organizationShareLabel = stringResource(Res.string.share_organization),
                organizationShareAmount = organizationShare,
                totalLabel = stringResource(Res.string.amount_total),
                totalAmount = itemTotal,
                dense = true,
            )
        }
    }
}

/** Narrative summary of a doctor's visit. */
@Composable
fun VisitSummaryCard(
    reason: String,
    diagnosis: String,
    note: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        LabeledBlock(label = stringResource(Res.string.detail_visit_reason), value = reason)
        TaminDivider()
        LabeledBlock(label = stringResource(Res.string.detail_diagnosis), value = diagnosis)
        TaminDivider()
        LabeledBlock(label = stringResource(Res.string.detail_doctor_note), value = note)
    }
}

/**
 * A single lab measurement: the reading beside its reference range, with a pill
 * flagging whether it falls inside that range.
 */
@Composable
fun LabTestCard(
    name: String,
    status: String,
    result: String,
    normalRange: String,
    statusContainerColor: Color,
    statusContentColor: Color,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
            )
            StatusPill(
                text = status,
                containerColor = statusContainerColor,
                contentColor = statusContentColor,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = stringResource(Res.string.detail_result),
                amount = result,
                containerColor = colors.bgPage,
                contentColor = colors.textPrimary,
                labelColor = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(Res.string.detail_normal_range),
                amount = normalRange,
                containerColor = colors.bgPage,
                contentColor = colors.textPrimary,
                labelColor = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * How a record's cost splits between the organization and the insured person. The
 * insured person's share is the emphasized figure — it is the number they came for.
 */
@Composable
fun CostBreakdownCard(
    total: String,
    organizationShare: String,
    insuredShare: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.detail_cost_breakdown),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
        )
        // The record's total, in the same three tiles the timeline pins under the list — so the
        // figure a person sees on the card and the one they see here read as the same quantity.
        CostSplitTiles(
            insuredShareLabel = stringResource(Res.string.share_yours),
            insuredShareAmount = insuredShare,
            organizationShareLabel = stringResource(Res.string.share_organization),
            organizationShareAmount = organizationShare,
            totalLabel = stringResource(Res.string.amount_total),
            totalAmount = total,
        )
    }
}

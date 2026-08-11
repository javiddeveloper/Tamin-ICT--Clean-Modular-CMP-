package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
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
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LabeledBlock
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
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
import taminx.core.core_ui.unit_rial
import org.jetbrains.compose.resources.stringResource

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
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        DetailRow(label = metaLabel, value = metaValue, numeric = false)
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.xxs))
        DetailRow(label = stringResource(Res.string.detail_tracking_code), value = trackingCode)
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
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = colors.blueText,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgPage, RoundedCornerShape(CornerRadius.chip))
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            LabeledBlock(label = stringResource(Res.string.detail_dose), value = dose)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = stringResource(Res.string.detail_prescribed),
                amount = prescribedCount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(Res.string.detail_received),
                amount = receivedCount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
            )
        }

        // The old app's remaining per-item fields, each shown only when present.
        if (centerName.isNotBlank() || actionDate.isNotBlank() || itemTotal.isNotBlank() ||
            patientShare.isNotBlank() || organizationShare.isNotBlank()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgPage, RoundedCornerShape(CornerRadius.chip))
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                if (centerName.isNotBlank()) DetailRow(label = stringResource(Res.string.detail_center), value = centerName)
                if (actionDate.isNotBlank()) DetailRow(label = stringResource(Res.string.detail_action_date), value = actionDate)
                if (itemTotal.isNotBlank()) DetailRow(label = stringResource(Res.string.amount_total), value = itemTotal, unit = stringResource(Res.string.unit_rial))
                if (patientShare.isNotBlank()) DetailRow(label = stringResource(Res.string.detail_patient_share), value = patientShare, unit = stringResource(Res.string.unit_rial))
                if (organizationShare.isNotBlank()) DetailRow(label = stringResource(Res.string.share_organization), value = organizationShare, unit = stringResource(Res.string.unit_rial))
            }
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
    ) {
        Text(
            text = stringResource(Res.string.detail_cost_breakdown),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
        DetailRow(label = stringResource(Res.string.amount_total), value = total, unit = stringResource(Res.string.unit_rial))
        DetailRow(
            label = stringResource(Res.string.share_organization),
            value = organizationShare,
            unit = stringResource(Res.string.unit_rial),
            valueColor = colors.blueText,
        )
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
        DetailRow(
            label = stringResource(Res.string.share_yours),
            value = insuredShare,
            unit = stringResource(Res.string.unit_rial),
            valueColor = colors.greenText,
            valueStyle = MaterialTheme.typography.titleLarge,
        )
    }
}

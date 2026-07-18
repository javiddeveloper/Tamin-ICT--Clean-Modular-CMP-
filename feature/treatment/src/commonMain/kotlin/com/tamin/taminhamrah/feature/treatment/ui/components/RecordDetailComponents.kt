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
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

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
            .treatmentSurface(CornerRadius.cardCompact)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        DetailRow(label = metaLabel, value = metaValue, numeric = false)
        TreatmentDivider(modifier = Modifier.padding(vertical = Spacing.xxs))
        DetailRow(label = "کد رهگیری", value = trackingCode)
        TreatmentDivider(modifier = Modifier.padding(vertical = Spacing.xxs))
        DetailRow(label = "تاریخ", value = date)
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
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .treatmentSurface(CornerRadius.cardCompact)
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
            LabeledBlock(label = "دستور مصرف", value = dose)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = "تجویزی",
                amount = prescribedCount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "دریافتی",
                amount = receivedCount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
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
            .treatmentSurface(CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        LabeledBlock(label = "دلیل مراجعه", value = reason)
        TreatmentDivider()
        LabeledBlock(label = "تشخیص", value = diagnosis)
        TreatmentDivider()
        LabeledBlock(label = "یادداشت پزشک", value = note)
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
            .treatmentSurface(CornerRadius.cardCompact)
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
                label = "نتیجه",
                amount = result,
                containerColor = colors.bgPage,
                contentColor = colors.textPrimary,
                labelColor = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "محدودهٔ طبیعی",
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
            .treatmentSurface(CornerRadius.cardCompact)
            .padding(Spacing.lg),
    ) {
        Text(
            text = "تفکیک هزینه",
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
        DetailRow(label = "جمع کل", value = total)
        DetailRow(
            label = "سهم سازمان",
            value = organizationShare,
            valueColor = colors.blueText,
        )
        TreatmentDivider(modifier = Modifier.padding(vertical = Spacing.xs))
        DetailRow(
            label = "سهم شما",
            value = insuredShare,
            valueColor = colors.greenText,
            valueStyle = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "ریال",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

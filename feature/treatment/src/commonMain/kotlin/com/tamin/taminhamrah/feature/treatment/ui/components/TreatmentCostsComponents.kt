package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.model.NO_AMOUNT
import com.tamin.taminhamrah.feature.treatment.ui.model.isActionable
import com.tamin.taminhamrah.feature.treatment.ui.model.isFileSettled
import com.tamin.taminhamrah.feature.treatment.ui.model.isPaid
import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.costs_admission_date
import taminx.core.core_ui.costs_admission_no
import taminx.core.core_ui.costs_center_name
import taminx.core.core_ui.costs_empty
import taminx.core.core_ui.costs_file_status
import taminx.core.core_ui.costs_main_insured
import taminx.core.core_ui.costs_other_services_payment
import taminx.core.core_ui.costs_patient_national_code
import taminx.core.core_ui.costs_prosthesis_payment
import taminx.core.core_ui.costs_refund_amount
import taminx.core.core_ui.costs_refund_date
import taminx.core.core_ui.costs_return_reason
import taminx.core.core_ui.costs_send_to_inbox
import taminx.core.core_ui.costs_view_certificate
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_misc_claims
import taminx.core.core_ui.unit_rial

/**
 * The refund-certificate list and its card, for «خسارت متفرقه».
 *
 * Kept beside the other treatment component files so the screen stays a description of the page's
 * shape rather than of every row in it.
 */

/** Shown where the service sent nothing, matching the previous app's placeholder. */
private const val ABSENT_VALUE = "-"

@Composable
internal fun CertificateList(
    certificates: ImmutableList<TreatmentCostPR>,
    isLoading: Boolean,
    error: String?,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
    onRetry: () -> Unit,
) {
    // Remembers completed entrance animations across list scrolls to avoid re-triggering entrance animations on already-visible items.
    val staggerState = rememberStaggeredEntranceState()

    // A failed request and a genuinely empty result read very differently: the dialog says what
    // went wrong, the list underneath falls through to its empty state.
    ErrorStateView(message = error, onRetry = onRetry)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && certificates.isEmpty() -> item { CostsShimmerSkeleton() }

            // Guarded on error: "no claims" and "we could not ask" are different answers.
            error == null && certificates.isEmpty() -> item {
                TaminEmptyState(message = stringResource(Res.string.costs_empty))
            }

            else -> itemsIndexed(certificates) { index, item ->
                CertificateCard(
                    item = item,
                    onOpenCertificate = onOpenCertificate,
                    onSendToInbox = onSendToInbox,
                    modifier = Modifier
                        .staggeredItemEntrance(index = index, key = item.repId, state = staggerState)
                        .padding(horizontal = Spacing.page)
                        .padding(
                            top = if (index == 0) Spacing.md else 0.dp,
                            bottom = if (index == certificates.lastIndex) {
                                Spacing.md
                            } else {
                                Spacing.cardGap
                            },
                        ),
                )
            }
        }
    }
}

/**
 * One refund certificate.
 *
 * The collapsed card answers the three questions the list is scanned for — was it paid, whose is
 * it, how much — and nothing else. The file's paperwork lives behind «نمایش جزئیات», so ten
 * certificates stay readable instead of being ten identical tables.
 */
@Composable
private fun CertificateCard(
    item: TreatmentCostPR,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var expanded by remember(item.repId) { mutableStateOf(false) }

    val paid = item.isPaid
    val accent = if (paid) colors.greenText else colors.orangeText
    val actionable = item.isActionable

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            // The same leading stripe the records list uses, so both read as one family.
            .accentStripe(accent)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatusPill(
                text = item.payStatusDesc,
                containerColor = if (paid) colors.greenBg else colors.orangeBg,
                contentColor = accent,
            )
            NumericText(
                text = item.serviceDate.toPersianDigits(),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
        }

        Text(
            text = item.nameFamil,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(Res.string.costs_admission_no, item.noPazir).toPersianDigits(),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary,
        )

        // The one number the list is scanned for, given the weight to match.
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.costs_refund_amount),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                NumericText(
                    text = item.payPrice.toLongOrNull()?.toPriceFormat() ?: ABSENT_VALUE,
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                )
                Text(
                    text = stringResource(Res.string.unit_rial),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted,
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            CertificateDetails(
                item = item,
                actionable = actionable,
                onOpenCertificate = onOpenCertificate,
                onSendToInbox = onSendToInbox,
            )
        }

        TaminDivider(modifier = Modifier.padding(top = Spacing.xs))
        ExpandToggle(expanded = expanded, onToggle = { expanded = !expanded })
    }
}

/** The paperwork behind the summary: the file's own fields, then what was paid, then the actions. */
@Composable
private fun CertificateDetails(
    item: TreatmentCostPR,
    actionable: Boolean,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
) {
    val colors = LocalTaminColors.current
    val fileSettled = item.isFileSettled

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))

        DetailRow(label = stringResource(Res.string.costs_patient_national_code), value = item.maliCode)
        DetailRow(label = stringResource(Res.string.costs_main_insured), value = item.nameAsli)
        DetailRow(label = stringResource(Res.string.costs_center_name), value = item.healthcenterName)
        DetailRow(label = stringResource(Res.string.costs_admission_date), value = item.datePaz, valueColor = colors.blueText)
        DetailRow(
            label = stringResource(Res.string.costs_refund_date),
            value = item.estimatePayDate,
            valueColor = colors.blueText,
        )
        DetailRow(
            label = stringResource(Res.string.costs_file_status),
            value = item.statusDesc,
            valueColor = if (fileSettled) colors.greenText else colors.dangerText,
        )
        DetailRow(label = stringResource(Res.string.costs_return_reason), value = item.returnReason)

        // The two amounts side by side rather than as two more label/value lines — the same
        // split-tile treatment the hub gives the insured and organization shares.
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            StatTile(
                label = stringResource(Res.string.costs_prosthesis_payment),
                amount = item.payService.toRialAmount(ABSENT_VALUE),
                containerColor = colors.blueBg,
                contentColor = if (item.payService != NO_AMOUNT) colors.blueText else colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(Res.string.costs_other_services_payment),
                amount = item.payOtherService.toRialAmount(ABSENT_VALUE),
                containerColor = colors.greenBg,
                contentColor = if (item.payOtherService != NO_AMOUNT) colors.greenText else colors.textMuted,
                modifier = Modifier.weight(1f),
            )
        }

        if (actionable) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                CertificateAction(
                    label = stringResource(Res.string.costs_view_certificate),
                    icon = vectorResource(Res.drawable.ic_tamin_download),
                    containerColor = colors.greenBg,
                    contentColor = colors.teal,
                    onClick = { onOpenCertificate(item.repId) },
                    modifier = Modifier.weight(1f),
                )
                CertificateAction(
                    label = stringResource(Res.string.costs_send_to_inbox),
                    icon = vectorResource(Res.drawable.ic_tamin_misc_claims),
                    containerColor = colors.blueBg,
                    contentColor = colors.blueText,
                    onClick = { onSendToInbox(item.repId) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** «نمایش جزئیات» with a chevron that turns as the card opens. */
@Composable
private fun ExpandToggle(expanded: Boolean, onToggle: () -> Unit) {
    val colors = LocalTaminColors.current
    val rotation by animateFloatAsState(
        targetValue = if (expanded) TreatmentDimens.chevronOpenDegrees else 0f,
        label = "certificate-chevron",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(top = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
    ) {
        Text(
            text = stringResource(
            if (expanded) Res.string.action_hide_details else Res.string.action_show_details,
        ),
            style = MaterialTheme.typography.labelLarge,
            color = colors.teal,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.teal,
            modifier = Modifier.size(IconSize.small).rotate(rotation),
        )
    }
}

/** Label on one side, value on the other — the key/value line the file's paperwork reads as. */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = LocalTaminColors.current.textPrimary,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value.ifBlank { ABSENT_VALUE }.toPersianDigits(),
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** One of the card's two operations, sized to share the row evenly. */
@Composable
private fun CertificateAction(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(CornerRadius.chip))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


@Composable
private fun CostsShimmerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        repeat(TreatmentDimens.certificateSkeletonRows) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TreatmentDimens.certificateSkeletonHeight)
                    .taminSurface(CornerRadius.card)
                    .shimmer(),
            )
        }
    }
}

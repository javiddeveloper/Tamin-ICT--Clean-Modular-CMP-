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
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_misc_claims

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
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && certificates.isEmpty() -> item { CostsShimmerSkeleton() }

            // A failed request and a genuinely empty result read very differently, so they get
            // different states. Both recover the same way: pull to refresh.
            error != null -> item { CostsErrorState(message = error) }

            certificates.isEmpty() -> item {
                TaminEmptyState(message = "بازپرداخت هزینه‌ای برای نمایش وجود ندارد.")
            }

            else -> itemsIndexed(certificates) { index, item ->
                CertificateCard(
                    item = item,
                    onOpenCertificate = onOpenCertificate,
                    onSendToInbox = onSendToInbox,
                    modifier = Modifier
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
            text = "شماره پذیرش ${item.noPazir}".toPersianDigits(),
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
                text = "مبلغ بازپرداخت",
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
                    text = "ریال",
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

        DetailRow(label = "کد ملی بیمار", value = item.maliCode)
        DetailRow(label = "بیمه شده اصلی", value = item.nameAsli)
        DetailRow(label = "نام مرکز درمانی", value = item.healthcenterName)
        DetailRow(label = "تاریخ پذیرش", value = item.datePaz, valueColor = colors.blueText)
        DetailRow(
            label = "تاریخ بازپرداخت هزینه درمان",
            value = item.estimatePayDate,
            valueColor = colors.blueText,
        )
        DetailRow(
            label = "وضعیت پرونده",
            value = item.statusDesc,
            valueColor = if (fileSettled) colors.greenText else colors.dangerText,
        )
        DetailRow(label = "دلایل برگشت پرونده از مالی", value = item.returnReason)

        // The two amounts side by side rather than as two more label/value lines — the same
        // split-tile treatment the hub gives the insured and organization shares.
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            StatTile(
                label = "پرداختی پروتز",
                amount = item.payService.asRial(),
                containerColor = colors.blueBg,
                contentColor = if (item.payService != NO_AMOUNT) colors.blueText else colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "پرداختی سایر خدمات پزشکی",
                amount = item.payOtherService.asRial(),
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
                    label = "مشاهده گواهی",
                    icon = vectorResource(Res.drawable.ic_tamin_download),
                    containerColor = colors.greenBg,
                    contentColor = colors.teal,
                    onClick = { onOpenCertificate(item.repId) },
                    modifier = Modifier.weight(1f),
                )
                CertificateAction(
                    label = "ارسال به صندوق شخصی",
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
            text = if (expanded) "مخفی کردن جزئیات" else "نمایش جزئیات",
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

/** Money the way every amount on these screens reads: grouped digits, then the unit. */
private fun String.asRial(): String =
    "${toLongOrNull()?.toPriceFormat() ?: ABSENT_VALUE} ریال"

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

/**
 * Failure state: what went wrong and how to recover, nothing more.
 *
 * No retry button — the list is pull-to-refresh, so one gesture both reloads a good list and
 * recovers from a failure, instead of the screen offering two ways to do the same thing.
 */
@Composable
private fun CostsErrorState(message: String) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(Spacing.page),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_cross),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.large),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "برای تلاش دوباره، صفحه را به پایین بکشید.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
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

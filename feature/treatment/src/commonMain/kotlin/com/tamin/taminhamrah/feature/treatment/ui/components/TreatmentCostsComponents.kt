package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentCostsDimens
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.model.NO_AMOUNT
import com.tamin.taminhamrah.feature.treatment.ui.model.isActionable
import com.tamin.taminhamrah.feature.treatment.ui.model.isFileSettled
import com.tamin.taminhamrah.feature.treatment.ui.model.isPaid
import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.ui.ActionMenuItem
import com.tamin.taminhamrah.ui.components.RecordCard
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminCostsAccentBottom
import com.tamin.taminhamrah.ui.theme.TaminCostsAccentTop
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.category_misc_claims
import taminx.core.core_ui.costs_action_operations
import taminx.core.core_ui.costs_admission_date
import taminx.core.core_ui.costs_admission_label
import taminx.core.core_ui.costs_center_name
import taminx.core.core_ui.costs_empty
import taminx.core.core_ui.costs_file_status
import taminx.core.core_ui.costs_list_section_title
import taminx.core.core_ui.costs_main_insured
import taminx.core.core_ui.costs_other_services_payment
import taminx.core.core_ui.costs_patient_national_code
import taminx.core.core_ui.costs_prosthesis_payment
import taminx.core.core_ui.costs_refund_amount
import taminx.core.core_ui.costs_refund_date
import taminx.core.core_ui.costs_return_reason
import taminx.core.core_ui.costs_send_to_inbox
import taminx.core.core_ui.costs_view_certificate
import taminx.core.core_ui.ic_email
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_eye
import taminx.core.core_ui.ic_tamin_medical_records
import taminx.core.core_ui.ic_tamin_misc_claims

/** Shown where the service sent nothing, matching the previous app's placeholder. */
private const val ABSENT_VALUE = "-"

/** What the «عملیات» menu can do. */
private enum class CostsAction { VIEW_CERTIFICATE, SEND_TO_INBOX }

@Composable
internal fun CertificateList(
    certificates: ImmutableList<TreatmentCostPR>,
    isLoading: Boolean,
    error: String?,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
) {
    val colors = LocalTaminColors.current
    val staggerState = rememberStaggeredEntranceState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        overscrollEffect = rememberJellyOverscroll(),
    ) {
        when {
            isLoading && certificates.isEmpty() -> item { CostsShimmerSkeleton() }

            error != null -> item { CostsErrorState(message = error) }

            certificates.isEmpty() -> item {
                TaminEmptyState(message = stringResource(Res.string.costs_empty))
            }

            else -> {
                item {
                    Text(
                        text = stringResource(Res.string.costs_list_section_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textMuted,
                        modifier = Modifier
                            .padding(horizontal = Spacing.page)
                            .padding(top = Spacing.md, bottom = Spacing.xs),
                    )
                }

                itemsIndexed(certificates) { index, item ->
                    CertificateCard(
                        item = item,
                        onOpenCertificate = onOpenCertificate,
                        onSendToInbox = onSendToInbox,
                        modifier = Modifier
                            .staggeredItemEntrance(index = index, key = item.noPazir, state = staggerState)
                            .padding(horizontal = Spacing.page)
                            .padding(
                                top = Spacing.xs,
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
}

/**
 * One refund row, mapped onto the shared [RecordCard].
 *
 * Everything visual now lives in that component; this only says what a treatment cost *is* — the
 * claim chip, the payment stamp, the receipt number and the two operations it offers.
 */
@Composable
private fun CertificateCard(
    item: TreatmentCostPR,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    // Keyed on the admission number, not repId: a certificate the service never issued reports
    // repId "0", so several rows would share one key and bleed each other's expansion state.
    var expanded by remember(item.noPazir) { mutableStateOf(false) }

    val viewLabel = stringResource(Res.string.costs_view_certificate)
    val sendLabel = stringResource(Res.string.costs_send_to_inbox)
    val actions = remember(viewLabel, sendLabel) {
        persistentListOf(
            ActionMenuItem(CostsAction.VIEW_CERTIFICATE, viewLabel, Res.drawable.ic_tamin_eye),
            ActionMenuItem(CostsAction.SEND_TO_INBOX, sendLabel, Res.drawable.ic_email),
        )
    }
    val rail = remember {
        Brush.verticalGradient(listOf(TaminCostsAccentTop, TaminCostsAccentBottom))
    }

    val fileSettled = item.isFileSettled

    RecordCard(
        chipLabel = stringResource(Res.string.category_misc_claims),
        chipIcon = vectorResource(Res.drawable.ic_tamin_medical_records),
        chipContainerColor = colors.greenBg,
        chipContentColor = colors.teal,
        date = item.serviceDate.toPersianDigits(),
        title = item.nameFamil,
        stampLabel = item.payStatusDesc,
        stampColor = if (item.isPaid) colors.greenText else colors.orangeText,
        codeLabel = stringResource(Res.string.costs_admission_label),
        code = item.noPazir,
        codeIcon = vectorResource(Res.drawable.ic_tamin_misc_claims),
        onCopyCode = { clipboardManager.setText(AnnotatedString(item.noPazir)) },
        actionsLabel = stringResource(Res.string.costs_action_operations),
        actions = actions,
        onActionSelect = { action ->
            when (action) {
                CostsAction.VIEW_CERTIFICATE -> onOpenCertificate(item.repId)
                CostsAction.SEND_TO_INBOX -> onSendToInbox(item.repId)
            }
        },
        railBrush = rail,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
        actionsEnabled = item.isActionable,
    ) {
        DetailRow(
            label = stringResource(Res.string.costs_patient_national_code),
            value = item.maliCode,
            divider = RowDivider.Solid,
        )
        DetailRow(
            label = stringResource(Res.string.costs_admission_date),
            value = item.datePaz,
            valueColor = colors.blueText,
        )
        DetailRow(
            label = stringResource(Res.string.costs_main_insured),
            value = item.nameAsli,
        )
        DetailRow(
            label = stringResource(Res.string.costs_file_status),
            value = item.statusDesc,
            valueColor = if (fileSettled) colors.greenText else colors.dangerText,
            valueBold = true,
        )
        DetailRow(
            label = stringResource(Res.string.costs_return_reason),
            value = item.returnReason,
        )
        DetailRow(
            label = stringResource(Res.string.costs_refund_date),
            value = item.estimatePayDate,
            valueColor = colors.teal,
        )
        DetailRow(
            label = stringResource(Res.string.costs_center_name),
            value = item.healthcenterName,
        )
        // A reported zero is drawn muted rather than colored: the previous app used the color to
        // say "something was actually paid under this heading", not merely "here is a number".
        DetailRow(
            label = stringResource(Res.string.costs_prosthesis_payment),
            value = item.payService.toRialAmount(ABSENT_VALUE),
            valueColor = if (item.payService == NO_AMOUNT) colors.textMuted else colors.blueText,
        )
        DetailRow(
            label = stringResource(Res.string.costs_other_services_payment),
            value = item.payOtherService.toRialAmount(ABSENT_VALUE),
            valueColor = if (item.payOtherService == NO_AMOUNT) colors.textMuted else colors.greenText,
        )
        // The refund total closes the list in a pill: it is the number the screen exists for.
        DetailPillRow(
            label = stringResource(Res.string.costs_refund_amount),
            amount = item.payPrice.toRialAmount(ABSENT_VALUE),
        )
    }
}


/** Which rule a detail row draws beneath itself. The design's first row is solid, the rest are
 *  dashed, and the last carries none. */
private enum class RowDivider { Solid, Dashed, None }

/**
 * The 12dp-above-and-below rhythm every detail row shares, plus its trailing rule.
 *
 * Holding the padding and the divider here rather than spacing the rows from the parent is what
 * lets a rule sit flush between two rows instead of floating in the gap.
 */
@Composable
private fun DetailRowContainer(divider: RowDivider, content: @Composable () -> Unit) {
    val colors = LocalTaminColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = TreatmentCostsDimens.detailRowPadding)) { content() }
        when (divider) {
            RowDivider.Solid -> TaminDivider()
            RowDivider.Dashed -> DashedDivider(
                modifier = Modifier.fillMaxWidth(),
                color = colors.divider,
            )
            RowDivider.None -> Unit
        }
    }
}

/** Dashed horizontal divider for admission number row */
@Composable
private fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalTaminColors.current.border,
    strokeWidth: Dp = TreatmentCostsDimens.dashedStrokeWidth,
    dashLength: Dp = TreatmentCostsDimens.dashedDashLength,
    gapLength: Dp = TreatmentCostsDimens.dashedGapLength,
) {
    Canvas(modifier = modifier.height(strokeWidth)) {
        val strokeWidthPx = strokeWidth.toPx()
        val dashPx = dashLength.toPx()
        val gapPx = gapLength.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidthPx,
            pathEffect = pathEffect,
        )
    }
}

/** Key/Value detail line */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = LocalTaminColors.current.textPrimary,
    valueBold: Boolean = false,
    divider: RowDivider = RowDivider.Dashed,
) = DetailRowContainer(divider) {
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
            fontWeight = if (valueBold) FontWeight.SemiBold else FontWeight.Normal,
            color = valueColor,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Key/Value detail line where value is formatted inside a green pill/badge container */
@Composable
private fun DetailPillRow(
    label: String,
    amount: String,
    divider: RowDivider = RowDivider.None,
) = DetailRowContainer(divider) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(colors.greenBg)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            Text(
                text = amount.toPersianDigits(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.greenText,
                textAlign = TextAlign.End,
            )
        }
    }
}

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
                    .raisedCard(CornerRadius.card)
                    .shimmer(),
            )
        }
    }
}



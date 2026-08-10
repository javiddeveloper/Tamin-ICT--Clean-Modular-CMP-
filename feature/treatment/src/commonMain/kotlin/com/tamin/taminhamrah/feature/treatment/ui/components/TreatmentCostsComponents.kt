package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.model.isFileSettled
import com.tamin.taminhamrah.feature.treatment.ui.model.isPaid
import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminCostsAccentBottom
import com.tamin.taminhamrah.ui.theme.TaminCostsAccentTop
import com.tamin.taminhamrah.ui.theme.TaminCostsOperationsEnd
import com.tamin.taminhamrah.ui.theme.TaminCostsOperationsStart
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.category_misc_claims
import taminx.core.core_ui.costs_action_operations
import taminx.core.core_ui.costs_admission_label
import taminx.core.core_ui.costs_center_name
import taminx.core.core_ui.costs_empty
import taminx.core.core_ui.costs_file_payment
import taminx.core.core_ui.costs_file_status
import taminx.core.core_ui.costs_list_section_title
import taminx.core.core_ui.costs_main_insured
import taminx.core.core_ui.costs_other_services_payment
import taminx.core.core_ui.costs_patient_national_code
import taminx.core.core_ui.costs_refund_date
import taminx.core.core_ui.costs_return_reason
import taminx.core.core_ui.costs_send_to_inbox
import taminx.core.core_ui.costs_view_certificate
import taminx.core.core_ui.ic_check
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_share
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_medical_records
import taminx.core.core_ui.ic_tamin_misc_claims
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentCostsDimens
import androidx.compose.ui.graphics.graphicsLayer
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.TaminCostsOperationsInk
import taminx.core.core_ui.ic_tamin_chevron_back

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
                        index = index,
                        totalCount = certificates.size,
                        onOpenCertificate = onOpenCertificate,
                        onSendToInbox = onSendToInbox,
                        modifier = Modifier
                            .staggeredItemEntrance(index = index, key = item.repId, state = staggerState)
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

/** The «عملیات» button is green rather than the card's teal. */
private val OperationsGradient =
    Brush.linearGradient(listOf(TaminCostsOperationsStart, TaminCostsOperationsEnd))

/**
 * One refund card.
 *
 * Laid out from the design's own values rather than measured off a render: a teal rail down the
 * trailing edge, a wash under the top edge, then four bands — claim chip and date, patient and
 * stamp, the receipt line, and the details behind a disclosure.
 *
 * The rail is drawn at the *physical* right through [drawBehind] rather than aligned to an edge,
 * because `End` follows the reading direction and would put it on the left of a Persian page.
 */
@Composable
private fun CertificateCard(
    item: TreatmentCostPR,
    index: Int,
    totalCount: Int,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    var expanded by remember(item.repId) { mutableStateOf(false) }
    var menuOpen by remember(item.repId) { mutableStateOf(false) }

    val paid = item.isPaid
    val stampColor = if (paid) colors.greenText else colors.orangeText
    val fileSettled = item.isFileSettled

    val rail = remember { Brush.verticalGradient(listOf(TaminCostsAccentTop, TaminCostsAccentBottom)) }
    val topWash = remember(colors.teal) {
        Brush.verticalGradient(
            listOf(TaminCostsAccentBottom.copy(alpha = TreatmentCostsDimens.TOP_WASH_ALPHA), Color.Transparent),
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = TreatmentCostsDimens.cardCorner,
                blurRadius = TreatmentCostsDimens.cardShadowBlur,
                offsetY = TreatmentCostsDimens.cardShadowOffsetY,
            )
            .clip(RoundedCornerShape(TreatmentCostsDimens.cardCorner))
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, RoundedCornerShape(TreatmentCostsDimens.cardCorner))
            .drawBehind {
                drawRect(
                    brush = topWash,
                    size = Size(size.width, TreatmentCostsDimens.topWashHeight.toPx()),
                )
                val railWidth = TreatmentCostsDimens.railWidth.toPx()
                drawRect(
                    brush = rail,
                    topLeft = Offset(size.width - railWidth, 0f),
                    size = Size(railWidth, size.height),
                )
            },
    ) {
        // Claim type and the date it was filed.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = TreatmentCostsDimens.cardPaddingHorizontal, end = TreatmentCostsDimens.cardPaddingHorizontal, top = TreatmentCostsDimens.claimRowTop),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.greenBg)
                    .padding(horizontal = TreatmentCostsDimens.chipPaddingHorizontal, vertical = TreatmentCostsDimens.chipPaddingVertical),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TreatmentCostsDimens.chipGap),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_misc_claims),
                    contentDescription = null,
                    tint = colors.teal,
                    modifier = Modifier.size(TreatmentCostsDimens.chipIconSize),
                )
                Text(
                    text = stringResource(Res.string.category_misc_claims),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.teal,
                )
            }
            NumericText(
                text = item.serviceDate.toPersianDigits(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = colors.textMuted,
            )
        }

        // Patient, and the payment stamp inside its ring.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = TreatmentCostsDimens.cardPaddingHorizontal,
                    end = TreatmentCostsDimens.cardPaddingHorizontal,
                    top = TreatmentCostsDimens.patientRowTop,
                    bottom = TreatmentCostsDimens.patientRowBottom,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = item.nameFamil,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            PaymentStamp(label = item.payStatusDesc, color = stampColor)
        }

        // Receipt number: a leader ruled across to a copyable chip.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TreatmentCostsDimens.cardPaddingHorizontal)
                .padding(top = TreatmentCostsDimens.receiptRowTop, bottom = TreatmentCostsDimens.receiptRowBottom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_medical_records),
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(TreatmentCostsDimens.receiptIconSize),
            )
            Text(
                text = stringResource(Res.string.costs_admission_label),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textMuted,
            )
            DashedDivider(modifier = Modifier.weight(1f), color = colors.border)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(TreatmentCostsDimens.copyChipCorner))
                    .background(colors.blueBg)
                    .dashedBorder(colors.blueText, TreatmentCostsDimens.copyChipCorner, TreatmentCostsDimens.copyChipBorderWidth)
                    .clickable { clipboardManager.setText(AnnotatedString(item.noPazir)) }
                    .padding(start = TreatmentCostsDimens.copyChipPaddingStart,
                        end = TreatmentCostsDimens.copyChipPaddingEnd,
                        top = TreatmentCostsDimens.copyChipPaddingVertical,
                        bottom = TreatmentCostsDimens.copyChipPaddingVertical,),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TreatmentCostsDimens.copyChipGap),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_share),
                    contentDescription = stringResource(Res.string.costs_admission_label),
                    tint = colors.blueText,
                    modifier = Modifier.size(TreatmentCostsDimens.chipIconSize),
                )
                NumericText(
                    text = item.noPazir.toPersianDigits(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        letterSpacing = TreatmentCostsDimens.receiptLetterSpacing,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = colors.blueText,
                )
            }
        }

        DoubleRule(
            modifier = Modifier
                .padding(horizontal = TreatmentCostsDimens.cardPaddingHorizontal)
                .padding(top = TreatmentCostsDimens.ruleTop, bottom = TreatmentCostsDimens.ruleBottom),
        )

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(horizontal = TreatmentCostsDimens.cardPaddingHorizontal)) {
                DetailRow(
                    label = stringResource(Res.string.costs_patient_national_code),
                    value = item.maliCode,
                    divider = RowDivider.Solid,
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
                DetailRow(
                    label = stringResource(Res.string.costs_file_payment),
                    value = item.payService.toRialAmount(ABSENT_VALUE),
                )
                DetailPillRow(
                    label = stringResource(Res.string.costs_other_services_payment),
                    amount = item.payOtherService.toRialAmount(ABSENT_VALUE),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TreatmentCostsDimens.cardPaddingHorizontal)
                .padding(top = TreatmentCostsDimens.footerTop, bottom = TreatmentCostsDimens.footerBottom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TreatmentCostsDimens.footerButtonGap),
        ) {
            // Held as State, not delegated: reading it here would recompose the footer on every
            // frame of the turn. Read inside graphicsLayer, the animation costs none.
            val rotation = animateFloatAsState(
                targetValue = if (expanded) {
                    TreatmentDimens.chevronOpenDegrees
                } else {
                    TreatmentDimens.chevronClosedDegrees
                },
                label = "certificate-chevron",
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(TreatmentCostsDimens.footerButtonCorner))
                    .background(colors.bgPage)
                    .clickable { expanded = !expanded }
                    .padding(vertical = TreatmentCostsDimens.footerButtonPaddingVertical),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TreatmentCostsDimens.copyChipGap, Alignment.CenterHorizontally),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    tint = colors.teal,
                    modifier = Modifier
                        .size(TreatmentCostsDimens.chevronSize)
                        .graphicsLayer { rotationZ = rotation.value },
                )
                Text(
                    text = stringResource(
                        if (expanded) Res.string.action_hide_details else Res.string.action_show_details,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.teal,
                )
            }

            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(TreatmentCostsDimens.footerButtonCorner))
                        .background(OperationsGradient)
                        .clickable { menuOpen = true }
                        .padding(horizontal = TreatmentCostsDimens.operationsPaddingHorizontal,
                            vertical = TreatmentCostsDimens.footerButtonPaddingVertical,),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TreatmentCostsDimens.copyChipGap),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_setting),
                        contentDescription = null,
                        tint = TaminCostsOperationsInk,
                        modifier = Modifier.size(TreatmentCostsDimens.chevronSize),
                    )
                    Text(
                        text = stringResource(Res.string.costs_action_operations),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TaminCostsOperationsInk,
                    )
                }
                OperationsMenu(
                    expanded = menuOpen,
                    onDismiss = { menuOpen = false },
                    onOpenCertificate = {
                        menuOpen = false
                        onOpenCertificate(item.repId)
                    },
                    onSendToInbox = {
                        menuOpen = false
                        onSendToInbox(item.repId)
                    },
                )
            }
        }
    }
}

/**
 * The payment stamp: a check over its label, inside the design's faint ring.
 *
 * The ring is drawn rather than laid out so it can overhang the row without taking space, which is
 * what lets it sit behind the check the way a stamp would.
 */
@Composable
private fun PaymentStamp(label: String, color: Color) {
    val ringColor = color.copy(alpha = TreatmentCostsDimens.STAMP_RING_ALPHA)
    Column(
        modifier = Modifier.drawBehind {
            val diameter = TreatmentCostsDimens.stampRingSize.toPx()
            val stroke = Thickness.border.toPx()
            drawCircle(
                color = ringColor,
                radius = diameter / 2f,
                center = Offset(size.width / 2f, size.height / 2f),
                style = Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(TreatmentCostsDimens.stampDashOn.toPx(), TreatmentCostsDimens.stampDashOff.toPx()),
                    ),
                ),
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_check),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(TreatmentCostsDimens.stampCheckSize),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
        )
    }
}

/** The «عملیات» menu. A popover anchored to the button, which is what the design shows. */
@Composable
private fun OperationsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onOpenCertificate: () -> Unit,
    onSendToInbox: () -> Unit,
) {
    val colors = LocalTaminColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(TreatmentCostsDimens.menuCorner),
        containerColor = colors.bgSurface,
        modifier = Modifier.width(TreatmentCostsDimens.menuWidth),
    ) {
        OperationsMenuItem(
            text = stringResource(Res.string.costs_view_certificate),
            icon = vectorResource(Res.drawable.ic_tamin_medical_records),
            iconTint = colors.teal,
            onClick = onOpenCertificate,
        )
        TaminDivider()
        OperationsMenuItem(
            text = stringResource(Res.string.costs_send_to_inbox),
            icon = vectorResource(Res.drawable.ic_share),
            iconTint = colors.textTertiary,
            onClick = onSendToInbox,
        )
    }
}

@Composable
private fun OperationsMenuItem(
    text: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = TreatmentCostsDimens.menuItemPaddingHorizontal,
                vertical = TreatmentCostsDimens.menuItemPaddingVertical,),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(TreatmentCostsDimens.menuIconSize),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
    }
}

/** A dashed rounded outline, which Compose has no first-class modifier for. */
private fun Modifier.dashedBorder(color: Color, cornerRadius: Dp, width: Dp): Modifier = drawBehind {
    val stroke = width.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(stroke / 2f, stroke / 2f),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
        style = Stroke(
            width = stroke,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(TreatmentCostsDimens.copyChipDashOn.toPx(), TreatmentCostsDimens.copyChipDashOff.toPx()),
            ),
        ),
    )
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

/** The design's 3px double rule: a solid hairline over a lighter one. */
@Composable
private fun DoubleRule(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(thickness = TreatmentCostsDimens.ruleThickness, color = colors.border)
        Spacer(Modifier.height(TreatmentCostsDimens.ruleGap))
        HorizontalDivider(thickness = TreatmentCostsDimens.ruleThickness, color = colors.divider)
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
                    .taminSurface(CornerRadius.card)
                    .shimmer(),
            )
        }
    }
}


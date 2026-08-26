package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.jalali_months
import taminx.core.core_ui.unit_rial
import taminx.core.core_ui.workers_payment_daily_wage
import taminx.core.core_ui.workers_payment_date_range
import taminx.core.core_ui.workers_payment_date_range_value
import taminx.core.core_ui.workers_payment_days_format
import taminx.core.core_ui.workers_payment_insurance_days
import taminx.core.core_ui.workers_payment_job_code
import taminx.core.core_ui.workers_payment_job_coefficient
import taminx.core.core_ui.workers_payment_job_title
import taminx.core.core_ui.workers_payment_no_penalty
import taminx.core.core_ui.workers_payment_paid_on
import taminx.core.core_ui.workers_payment_pay_action
import taminx.core.core_ui.workers_payment_penalty
import taminx.core.core_ui.workers_payment_premium
import taminx.core.core_ui.workers_payment_status_overdue
import taminx.core.core_ui.workers_payment_status_paid
import taminx.core.core_ui.workers_payment_status_payable
import taminx.core.core_ui.workers_payment_total_amount

private const val CHEVRON_OPEN_DEGREES = -90f
private const val CHEVRON_CLOSED_DEGREES = 90f

/**
 * One month's premium/penalty row — collapsed it shows the amount, the insured-day count and a
 * status pill; expanded it opens the full breakdown (job, wage, premium, penalty). Card shell,
 * grid rows and dashed divider mirror `InspectionItemCard`.
 */
@Composable
internal fun WorkersPaymentMonthCard(
    item: WorkersPaymentInfoPR,
    onPayClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rial = stringResource(Res.string.unit_rial)
    var expanded by remember(item.fromDateToDate) { mutableStateOf(false) }
    val months = stringArrayResource(Res.array.jalali_months)
    val monthName = item.month.toIntOrNull()
        ?.minus(1)
        ?.let { months.getOrNull(it) }
        ?: item.monthTitle
    val price: (Long) -> String = { "${it.toPriceFormat()} $rial" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 26.dp,
                offsetY = 10.dp,
            )
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.lg, bottom = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "$monthName ${item.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            WorkersPaymentStatusChip(item.status)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DetailGridRow(
                labelStart = stringResource(Res.string.workers_payment_total_amount),
                valueStart = price(item.totalPayable),
                labelEnd = stringResource(Res.string.workers_payment_insurance_days),
                valueEnd = stringResource(Res.string.workers_payment_days_format, item.days),
                numericEnd = false,
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    DashedDivider(
                        color = colors.divider,
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                    )

                    InfoBox(
                        label = stringResource(Res.string.workers_payment_date_range),
                        value = stringResource(
                            Res.string.workers_payment_date_range_value,
                            item.fromDatePersian.toFormattedDate(),
                            item.toDatePersian.toFormattedDate(),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    DetailGridRow(
                        labelStart = stringResource(Res.string.workers_payment_job_title),
                        valueStart = item.professionalTitle,
                        numericStart = false,
                        labelEnd = stringResource(Res.string.workers_payment_job_code),
                        valueEnd = item.professional,
                    )

                    DetailGridRow(
                        labelStart = stringResource(Res.string.workers_payment_daily_wage),
                        valueStart = price(item.salary),
                        labelEnd = stringResource(Res.string.workers_payment_job_coefficient),
                        valueEnd = item.rate,
                    )

                    InfoBox(
                        label = stringResource(Res.string.workers_payment_premium),
                        value = price(item.amount),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    InfoBox(
                        label = stringResource(Res.string.workers_payment_penalty),
                        value = if (item.amountFines > 0L) {
                            price(item.amountFines)
                        } else {
                            stringResource(Res.string.workers_payment_no_penalty)
                        },
                        emphasisColor = if (item.amountFines > 0L) colors.dangerText else null,
                        containerColor = if (item.amountFines > 0L) colors.dangerBg else null,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    InfoBox(
                        label = stringResource(Res.string.workers_payment_total_amount),
                        value = price(item.totalPayable),
                        emphasisColor = colors.greenText,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (item.status == WorkersPaymentInfoPR.Status.PAID && !item.paymentDate.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.greenBg, RoundedCornerShape(CornerRadius.xl))
                                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NumericText(
                                text = stringResource(
                                    Res.string.workers_payment_paid_on,
                                    item.paymentDate.toFormattedDate(),
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.greenText,
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.md, bottom = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            val rotation = animateFloatAsState(
                targetValue = if (expanded) CHEVRON_OPEN_DEGREES else CHEVRON_CLOSED_DEGREES,
                label = "workers-payment-card-chevron",
            )
            TaminOutlinedButton(
                height = 48.dp,
                textStyle = MaterialTheme.typography.titleSmall,
                text = stringResource(
                    if (expanded) Res.string.action_hide_details else Res.string.action_show_details,
                ),
                onClick = { expanded = !expanded },
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                iconModifier = Modifier.size(5.dp).graphicsLayer { rotationZ = rotation.value },
                containerColor = colors.bgPage,
                borderColor = Color.Transparent,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )

            if (item.status == WorkersPaymentInfoPR.Status.PAYABLE) {
                TaminFilledButton(
                    text = stringResource(Res.string.workers_payment_pay_action),
                    textStyle = MaterialTheme.typography.titleSmall,
                    onClick = onPayClicked,
                    background = colors.iconGradientSuccess,
                    modifier = Modifier.weight(1f).height(48.dp),
                )
            }
        }
    }
}

@Composable
private fun WorkersPaymentStatusChip(status: WorkersPaymentInfoPR.Status) {
    val colors = LocalTaminColors.current
    val (text, container, content) = when (status) {
        WorkersPaymentInfoPR.Status.PAID -> Triple(
            stringResource(Res.string.workers_payment_status_paid),
            colors.greenBg,
            colors.greenText,
        )
        WorkersPaymentInfoPR.Status.PAYABLE -> Triple(
            stringResource(Res.string.workers_payment_status_payable),
            colors.blueBg,
            colors.blueText,
        )
        WorkersPaymentInfoPR.Status.OVERDUE -> Triple(
            stringResource(Res.string.workers_payment_status_overdue),
            colors.bgPage,
            colors.textMuted,
        )
    }
    CustomChip(
        text = text,
        containerColor = container,
        textColor = content,
        border = BorderStroke(1.dp, colors.border),
    )
}

@Composable
private fun InfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
    emphasisColor: Color? = null,
    containerColor: Color? = null,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                containerColor ?: colors.bgPage,
                shape = RoundedCornerShape(CornerRadius.xl),
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        val valueStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        val valueColor = emphasisColor ?: colors.textPrimary
        if (numeric) {
            NumericText(text = value, style = valueStyle, color = valueColor)
        } else {
            Text(text = value, style = valueStyle, color = valueColor)
        }
    }
}

@Composable
private fun DetailGridRow(
    labelStart: String,
    valueStart: String,
    labelEnd: String,
    valueEnd: String,
    modifier: Modifier = Modifier,
    numericStart: Boolean = true,
    numericEnd: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        InfoBox(
            label = labelStart,
            value = valueStart,
            numeric = numericStart,
            modifier = Modifier.weight(1f),
        )
        InfoBox(
            label = labelEnd,
            value = valueEnd,
            numeric = numericEnd,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DashedDivider(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    dashLength: Dp = 5.dp,
    gapLength: Dp = 5.dp,
) {
    Canvas(modifier = modifier.fillMaxWidth().height(thickness)) {
        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f,
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect,
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

private val PreviewPayable = WorkersPaymentInfoPR(
    pay = false, payable = true, month = "07", monthTitle = "حق بیمه مهر", year = "1405",
    professionalTitle = "استاد لوله‌کش و نصاب وسایل بهداشتی", professional = "041597", rate = "1.9",
    days = "30", fromDatePersian = "14050701", toDatePersian = "14050730",
    amount = 22111981, amountFines = 0, totalPayable = 22111981, salary = 10529515, payDay = 5541850,
    payableDes = "هست", paymentDate = null, fishStatus = "دارد", maharatStatus = "دارد",
    bazresiStatus = "دارد", kargarStatus = "فعال می‌باشد.", type = "Premium",
    fromDateToDate = "1405070114050730",
)

private val PreviewPayableWithFine = PreviewPayable.copy(
    month = "04", monthTitle = "حق بیمه تیر", payable = true, amountFines = 1341000,
    totalPayable = 23452981,
)

private val PreviewPaid = PreviewPayable.copy(
    month = "03", monthTitle = "حق بیمه خرداد", pay = true, payable = false,
    paymentDate = "14040412",
)

@PreviewRtlTheme
@Composable
private fun WorkersPaymentMonthCardPayablePreview() {
    PreviewRtlThemeContent {
        WorkersPaymentMonthCard(item = PreviewPayable, onPayClicked = {}, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentMonthCardFinePreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkersPaymentMonthCard(item = PreviewPayableWithFine, onPayClicked = {}, modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentMonthCardPaidPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentMonthCard(item = PreviewPaid, onPayClicked = {}, modifier = Modifier.padding(Spacing.lg))
    }
}

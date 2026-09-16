package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.document_viewer_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_warning
import taminx.core.core_ui.inspection_activity_type
import taminx.core.core_ui.inspection_branch
import taminx.core.core_ui.inspection_date
import taminx.core.core_ui.inspection_id
import taminx.core.core_ui.inspection_insurance_no
import taminx.core.core_ui.inspection_relation_type
import taminx.core.core_ui.inspection_request_field_national_code
import taminx.core.core_ui.inspection_status_objectable
import taminx.core.core_ui.inspection_status_objection_expired
import taminx.core.core_ui.inspection_submit_objection
import taminx.core.core_ui.inspection_workshop_code

/** The server marks an objectable inspection with `"1"` — same convention the insured-side card uses. */
private val InspectionPerformedPR.isObjectable: Boolean get() = objectable == "1"

private const val CHEVRON_OPEN_DEGREES = -90f
private const val CHEVRON_CLOSED_DEGREES = 90f

/**
 * Employer-side inspection card. Differs visually from the insured-side `InspectionItemCard` in one
 * way: "دریافت گزارش" is an always-visible icon button in the main action row here, not nested inside
 * the expanded section — matching the design bundle for this screen.
 */
@Composable
internal fun WorkshopInspectionItemCard(
    item: InspectionPerformedPR,
    onSubmitObjectionClicked: () -> Unit,
    onDownloadReportClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var expanded by remember(item.inspectionNo) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.md, bottom = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = item.workshopName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (item.isObjectable) {
                CustomChip(
                    text = stringResource(Res.string.inspection_status_objectable),
                    containerColor = colors.greenBg,
                    textColor = colors.greenText,
                    border = BorderStroke(width = 1.dp, color = colors.border)
                )
            } else {
                CustomChip(
                    text = stringResource(Res.string.inspection_status_objection_expired),
                    containerColor = colors.bgPage,
                    textColor = colors.textMuted,
                    border = BorderStroke(width = 1.dp, color = colors.border)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DetailGridRow(
                labelStart = stringResource(Res.string.inspection_workshop_code),
                valueStart = item.workshopNo,
                copyValueStart = item.workshopNo,
                labelEnd = stringResource(Res.string.inspection_branch),
                valueEnd = item.branchdesc,
                numericEnd = false,
            )

            DetailGridRow(
                labelStart = stringResource(Res.string.inspection_date),
                valueStart = PersianDateFormatter.formatTimestamp(item.inspectionDate),
                labelEnd = stringResource(Res.string.inspection_id),
                valueEnd = item.inspectionNo,
                copyValueEnd = item.inspectionNo,
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    DashedDivider(
                        color = colors.divider,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs),
                    )

                    DetailGridRow(
                        labelStart = stringResource(Res.string.inspection_relation_type),
                        valueStart = item.relationType,
                        numericStart = false,
                        labelEnd = stringResource(Res.string.inspection_request_field_national_code),
                        valueEnd = item.nationalCode,
                    )
                    DetailGridRow(
                        labelStart = stringResource(Res.string.inspection_insurance_no),
                        valueStart = item.insuranceNo.ifBlank { "—" },
                        numericStart = false,
                        labelEnd = stringResource(Res.string.inspection_activity_type),
                        valueEnd = item.activityDesc,
                        numericEnd = false,
                    )
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
                label = "workshop-inspection-card-chevron",
            )

            // Row order (RTL, so first child renders rightmost): details toggle, then the report
            // document button, then the objection button last.
            TaminOutlinedButton(
                height = 48.dp,
                textStyle = MaterialTheme.typography.titleSmall,
                text = stringResource(if (expanded) Res.string.action_hide_details else Res.string.action_show_details),
                onClick = { expanded = !expanded },
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                iconModifier = Modifier.size(5.dp).graphicsLayer { rotationZ = rotation.value },
                containerColor = colors.bgPage,
                borderColor = Color.Transparent,
                contentColor = colors.blueText,
                iconPosition = IconPosition.End,
                modifier = Modifier.weight(1f),
            )

            ReportIconButton(
                modifier = Modifier.weight(1f),
                onClick = { onDownloadReportClicked(item.inspectionNo) },
            )

            if (item.isObjectable) {
                TaminFilledButton(
                    text = stringResource(Res.string.inspection_submit_objection),
                    textStyle = MaterialTheme.typography.titleSmall,
                    onClick = onSubmitObjectionClicked,
                    background = colors.iconGradientSuccess,
                    icon = vectorResource(Res.drawable.ic_warning),
                    iconPosition = IconPosition.End,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                )
            }
        }
    }
}

@Composable
private fun ReportIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .height(48.dp)
            .border(width = 1.dp, color = colors.border, shape = RoundedCornerShape(CornerRadius.iconTile))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            tint = colors.textSecondary,
            contentDescription = null,
        )
        Text(
            text = stringResource(Res.string.document_viewer_title),
            style = MaterialTheme.typography.titleSmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun InfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
    copyValue: String? = null,
) {
    val colors = LocalTaminColors.current
    val copy = copyValue?.let { rememberCopyAction(it) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgPage, shape = RoundedCornerShape(CornerRadius.xl))
            .then(if (copy != null) Modifier.clickable(onClick = copy) else Modifier)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
            if (numeric) {
                NumericText(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
            }
        }
        if (copyValue != null) {
            CopyIconButton(
                value = copyValue,
                label = label,
                interactive = false,
            )
        }
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
        val dashPx = dashLength.toPx()
        val gapPx = gapLength.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)

        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect,
        )
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
    copyValueStart: String? = null,
    copyValueEnd: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        InfoBox(
            label = labelStart,
            value = valueStart,
            numeric = numericStart,
            copyValue = copyValueStart,
            modifier = Modifier.weight(1f),
        )
        InfoBox(
            label = labelEnd,
            value = valueEnd,
            numeric = numericEnd,
            copyValue = copyValueEnd,
            modifier = Modifier.weight(1f),
        )
    }
}

private val PreviewObjectableInspection = InspectionPerformedPR(
    activityDesc = "دبستان غیردولتی",
    branchCode = "0210",
    branchdesc = "یک بجنورد",
    inspectionDate = 1743280800000L,
    inspectionNo = "6310020000706",
    insuranceNo = "",
    objectable = "1",
    relationType = "کارفرما",
    workshopName = "دبستان کارن ۲ مجتبی غلامیان",
    workshopNo = "9028212822",
    nationalCode = "0681895705",
)

private val PreviewExpiredInspection = PreviewObjectableInspection.copy(
    inspectionNo = "6310020000598",
    objectable = "0",
    relationType = "کارفرما",
)

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionItemCardObjectablePreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkshopInspectionItemCard(
                item = PreviewObjectableInspection,
                onSubmitObjectionClicked = {},
                onDownloadReportClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionItemCardObjectablePreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            WorkshopInspectionItemCard(
                item = PreviewObjectableInspection,
                onSubmitObjectionClicked = {},
                onDownloadReportClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopInspectionItemCardExpiredPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkshopInspectionItemCard(
                item = PreviewExpiredInspection,
                onSubmitObjectionClicked = {},
                onDownloadReportClicked = {},
                modifier = Modifier.padding(Spacing.lg),
            )
        }
    }
}

package com.tamin.taminhamrah.feature.objectionInsurance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionWorkshopPickerRowPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.objection_insurance_days_count
import taminx.core.core_ui.objection_insurance_edited_label
import taminx.core.core_ui.objection_insurance_workshop_picker_hint
import taminx.core.core_ui.objection_insurance_workshop_picker_title

private val CardCorner = RoundedCornerShape(CornerRadius.lg)
private val CellCorner = RoundedCornerShape(3.dp)

/**
 * Shown only when a year has more than one workshop record — pick which one to open in the
 * detail editor. A full screen (own hero header), not a bottom sheet, matching the design.
 */
@Composable
fun ObjectionWorkshopPickerScreen(
    year: String,
    rows: ImmutableList<ObjectionWorkshopPickerRowPR>,
    onRowClicked: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.objection_insurance_workshop_picker_title, year.toPersianDigits()),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBack,
                        bordered = true,
                    )
                },
            ) {
                TaminText(
                    text = stringResource(Res.string.objection_insurance_workshop_picker_hint),
                    style = MaterialTheme.typography.labelSmall.copy(lineHeight = 19.sp),
                    color = colors.onGradient.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.md,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.lg,
                    start = Spacing.page,
                    end = Spacing.page,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            rows.forEach { row ->
                WorkshopPickerCard(row = row, onClick = { onRowClicked(row.recordIndex) })
            }
        }
    }
}

@Composable
private fun WorkshopPickerCard(row: ObjectionWorkshopPickerRowPR, onClick: () -> Unit) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardCorner)
            .background(colors.bgSurface)
            .border(1.dp, colors.border, CardCorner)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(Spacing.smd),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Column(modifier = Modifier.weight(1f)) {
                TaminText(
                    text = row.workshopName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (row.meta.isNotBlank()) {
                    TaminText(
                        text = row.meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(18.dp),
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            row.monthOpacities.forEach { opacity ->
                val worked = opacity > 0f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(CellCorner)
                        .background(if (worked) colors.blueText.copy(alpha = opacity) else colors.historyGridLine),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Badge(
                text = stringResource(Res.string.objection_insurance_days_count, row.totalDays.toString().toPersianDigits()),
                background = colors.blueBg,
                contentColor = colors.blueText,
            )
            if (row.isEdited) {
                Badge(
                    text = stringResource(Res.string.objection_insurance_edited_label),
                    background = colors.greenBg,
                    contentColor = colors.greenText,
                )
            }
        }
    }
}

@Composable
private fun Badge(text: String, background: Color, contentColor: Color) {
    NumericText(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
        color = contentColor,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

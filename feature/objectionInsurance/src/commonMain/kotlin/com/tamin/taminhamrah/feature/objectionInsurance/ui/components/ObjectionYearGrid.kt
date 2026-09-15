package com.tamin.taminhamrah.feature.objectionInsurance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearCardPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearSubtitle
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.YearCompletionStatus
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_insurance_days_count
import taminx.core.core_ui.objection_insurance_edited_label
import taminx.core.core_ui.objection_insurance_workshop_count

private const val GRID_COLUMNS = 3
private const val DEFICIT_BORDER_ALPHA = 0.35f
private val RingSize = 62.dp
private val RingStroke = 7.dp
private val InnerCircleSize = 48.dp
private val CardCorner = RoundedCornerShape(17.dp)

/**
 * The «سال‌های سابقهٔ شما» grid — one donut-ring card per year, three to a row.
 *
 * Plain [Column]/[Row]s rather than `LazyVerticalGrid`: the whole screen is already one scrolling
 * container, and a lazy grid nested in a scrollable column needs its own fixed height to avoid
 * Compose's infinite-constraints crash. A insured person's history rarely exceeds a couple dozen
 * years, so virtualizing this grid buys nothing.
 */
@Composable
fun ObjectionYearGrid(
    cards: ImmutableList<ObjectionYearCardPR>,
    onCardClick: (ObjectionYearCardPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        cards.chunked(GRID_COLUMNS).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { card ->
                    ObjectionYearCard(
                        card = card,
                        onClick = { onCardClick(card) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(GRID_COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ObjectionYearCard(card: ObjectionYearCardPR, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val statusColor = when (card.status) {
        YearCompletionStatus.Edited -> colors.blueText
        YearCompletionStatus.Complete -> colors.greenText
        YearCompletionStatus.Deficit -> colors.orangeText
    }
    val borderColor = when (card.status) {
        YearCompletionStatus.Edited -> colors.blueBorder
        YearCompletionStatus.Complete -> colors.greenBorder
        YearCompletionStatus.Deficit -> colors.orangeText.copy(alpha = DEFICIT_BORDER_ALPHA)
    }
    val cardBackground = if (card.status == YearCompletionStatus.Edited) colors.blueBg else colors.bgSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CardCorner)
            .background(cardBackground)
            .border(1.dp, borderColor, CardCorner)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(top = 11.dp, bottom = 10.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(RingSize)) {
            Canvas(modifier = Modifier.size(RingSize)) {
                val stroke = Stroke(width = RingStroke.toPx(), cap = StrokeCap.Round)
                val inset = stroke.width / 2f
                val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
                drawArc(
                    color = colors.historyGridLine,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = stroke,
                )
                drawArc(
                    color = statusColor,
                    startAngle = -90f,
                    sweepAngle = 360f * card.fraction,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = stroke,
                )
            }
            Box(
                modifier = Modifier
                    .size(InnerCircleSize)
                    .clip(CircleShape)
                    .background(colors.bgSurface),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    NumericText(
                        text = card.year,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = colors.textPrimary,
                    )
                    NumericText(
                        text = card.days,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = statusColor,
                    )
                }
            }
        }

        val subtitleText = when (val subtitle = card.subtitle) {
            ObjectionYearSubtitle.Edited -> stringResource(Res.string.objection_insurance_edited_label)
            is ObjectionYearSubtitle.WorkshopCount ->
                stringResource(Res.string.objection_insurance_workshop_count, subtitle.count.toString().toPersianDigits())
            is ObjectionYearSubtitle.TotalDays ->
                stringResource(Res.string.objection_insurance_days_count, subtitle.days.toString().toPersianDigits())
        }
        TaminText(
            text = subtitleText,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
            color = if (card.status == YearCompletionStatus.Edited) colors.blueText else colors.textMuted,
        )
    }
}

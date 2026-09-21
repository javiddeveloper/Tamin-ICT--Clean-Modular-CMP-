package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminColors
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check

private val DotSize = 22.dp
private val ConnectorWidth = 2.dp
private val ConnectorGap = 2.dp
private const val TransitionMillis = 260

/**
 * A vertical run of numbered stages a request moves through: the ones behind it tick green, the one
 * it is sitting on is highlighted and captioned, the rest stay muted.
 *
 * Distinct from [StepIndicator], which lays a short wizard out horizontally — a back-office
 * pipeline is usually too long and too wordy to fit across a phone.
 *
 * The connector between two dots is measured, not guessed: each row lays its rail out to the height
 * the label actually took, so a two-line stage does not leave a gap in the line.
 */
@Composable
fun TaminStageTimeline(
    /** Stage names, in order. Index `0` is the first stage. */
    stages: ImmutableList<String>,
    /** Zero-based index of the stage in progress; out of range means none is. */
    currentIndex: Int,
    modifier: Modifier = Modifier,
    /** Caption under the stage in progress — «مرحلهٔ جاری». Omitted when null. */
    currentLabel: String? = null,
) {
    val colors = LocalTaminColors.current
    val checkIcon = vectorResource(Res.drawable.ic_tamin_check)

    Column(modifier = modifier.fillMaxWidth()) {
        stages.forEachIndexed { index, title ->
            key(title) {
                StageRow(
                    title = title,
                    number = (index + 1).toString().toPersianDigits(),
                    passed = index < currentIndex,
                    current = index == currentIndex,
                    isLast = index == stages.lastIndex,
                    currentLabel = currentLabel,
                    colors = colors,
                    checkIcon = checkIcon,
                )
            }
        }
    }
}

@Composable
private fun StageRow(
    title: String,
    number: String,
    passed: Boolean,
    current: Boolean,
    isLast: Boolean,
    currentLabel: String?,
    colors: TaminColors,
    checkIcon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    val dotFill = when {
        passed -> colors.greenText
        current -> colors.orangeBg
        else -> colors.bgPage
    }
    val dotStroke = when {
        passed -> colors.greenText
        current -> colors.orangeText
        else -> colors.border
    }
    val titleColor by animateColorAsState(
        targetValue = when {
            passed -> colors.textSecondary
            current -> colors.textPrimary
            else -> colors.textMuted
        },
        animationSpec = tween(TransitionMillis),
        label = "StageTitleColor",
    )
    val railColor = if (passed) colors.greenBorder else colors.divider

    // Intrinsic-min height so the rail can stretch to whatever the label beside it took: a stage
    // whose name wraps to two lines must not leave a gap in the line.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(DotSize)
                    .background(dotFill, CircleShape)
                    .border(Thickness.medium, dotStroke, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (passed) {
                    Icon(
                        imageVector = checkIcon,
                        contentDescription = null,
                        tint = colors.bgSurface,
                        modifier = Modifier.size(Spacing.md),
                    )
                } else {
                    TaminText(
                        text = number,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (current) colors.orangeText else colors.textMuted,
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = ConnectorGap)
                        .width(ConnectorWidth)
                        .background(railColor),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.smPlus, bottom = Spacing.cardGap),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            TaminText(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (current) FontWeight.Bold else FontWeight.SemiBold,
                ),
                color = titleColor,
            )
            if (current && currentLabel != null) {
                TaminText(
                    text = currentLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.orangeText,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminStageTimelinePreview() {
    PreviewRtlThemeContent {
        TaminStageTimeline(
            stages = persistentListOf(
                "احراز هویت",
                "ثبت اطلاعات",
                "بارگذاری مدارک هویتی",
                "نیاز به رسیدگی شعبه",
                "در حال بررسی شعبه - گام اعلام سابقه",
                "بارگذاری نامهٔ ترک کار",
                "بررسی شعبه - گام اعلام ترک کار",
                "صدور حکم",
            ),
            currentIndex = 4,
            currentLabel = "مرحلهٔ جاری",
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

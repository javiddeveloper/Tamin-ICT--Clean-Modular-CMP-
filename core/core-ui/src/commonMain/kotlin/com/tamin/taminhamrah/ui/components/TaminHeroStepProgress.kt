package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Primary700
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInk
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkFaint
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkSoft
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.step_of_total_label

private const val HeroStepSegmentFillDurationMs = 420

/**
 * Hero-header step chrome: current step title + "step X of Y", then equal-width segments where
 * the current index **and every step before it** are opaque (cumulative fill, so the bar reads as
 * progress made rather than just "you are here") — revised from the original "only current index"
 * behavior (which matched an early pension-survivor mockup) after disability-pension UX feedback.
 * Each segment fills progressively (track + a width-animated overlay via [animateFloatAsState]),
 * the same technique [StepIndicator]'s `StepConnector` uses between step circles, rather than
 * snapping or cross-fading color — only the segment newly becoming complete/current visibly
 * animates, since already-complete ones are already at full fraction. Only
 * `:feature:pensioner`'s disability-pension screen consumes this component today, so this default
 * changed with no other screen affected.
 *
 * Intended for [TaminTopAppBar]'s `content` slot on a blue/gradient hero.
 */
@Composable
fun TaminHeroStepProgress(
    stepTitle: String,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    require(totalSteps > 0) { "totalSteps must be > 0" }
    val clampedStep = currentStep.coerceIn(1, totalSteps)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaminText(
                text = stepTitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TaminOnAccentInkSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            TaminText(
                text = stringResource(
                    Res.string.step_of_total_label,
                    clampedStep.toString().toPersianDigits(),
                    totalSteps.toString().toPersianDigits(),
                ),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = TaminOnAccentInkSoft,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            for (index in 1..totalSteps) {
                val fillFraction by animateFloatAsState(
                    targetValue = if (index <= clampedStep) 1f else 0f,
                    animationSpec = tween(HeroStepSegmentFillDurationMs, easing = FastOutSlowInEasing),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(IconSize.heroStepSegmentHeight)
                        .clip(RoundedCornerShape(CornerRadius.full))
                        .background(TaminOnAccentInkFaint),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fillFraction)
                            .background(TaminOnAccentInk),
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminHeroStepProgressPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary700)
                .padding(Spacing.lg),
        ) {
            TaminHeroStepProgress(
                stepTitle = "مقررات و ضوابط",
                currentStep = 1,
                totalSteps = 5,
            )
        }
    }
}

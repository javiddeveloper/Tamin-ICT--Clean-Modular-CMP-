package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkReached
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkMuted
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkSoft
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.step_of_total_label

/**
 * Hero-header step chrome: current step title + "step X of Y", then equal-width segments.
 *
 * By default, **only the current index** is opaque (matches pension-survivor mockups; not
 * cumulative fill). Pass [maxReachedStep] for a wizard that also shows how far the user has got,
 * and [onStepClick] to let them tap back to a step they have already completed.
 *
 * Intended for [TaminTopAppBar]'s `content` slot on a blue/gradient hero.
 */
@Composable
fun TaminHeroStepProgress(
    stepTitle: String,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    /** One line under the segments saying what this step asks for. Omitted when the step has none. */
    hint: String? = null,
    /**
     * Highest step the user has reached, so earlier segments read as done rather than pending.
     * `0` — the default — keeps the current-only highlight every existing caller expects.
     */
    maxReachedStep: Int = 0,
    /**
     * Makes segments *before* the current one tappable, called with that step's 1-based index.
     * Future steps stay inert: a wizard cannot be skipped forward from its own progress bar.
     */
    onStepClick: ((Int) -> Unit)? = null,
    stepSubtitle: String? = null,
) {
    require(totalSteps > 0) { "totalSteps must be > 0" }
    val clampedStep = currentStep.coerceIn(1, totalSteps)
    val reached = maxReachedStep.coerceIn(0, totalSteps)

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
            val segmentShape = RoundedCornerShape(CornerRadius.full)
            for (index in 1..totalSteps) {
                val fill = when {
                    index == clampedStep -> TaminOnAccentInk
                    index <= reached -> TaminOnAccentInkReached
                    else -> TaminOnAccentInkFaint
                }
                val goBack = onStepClick?.takeIf { index < clampedStep }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(IconSize.heroStepSegmentHeight)
                        .background(color = fill, shape = segmentShape)
                        .then(
                            if (goBack != null) {
                                Modifier.clickable { goBack(index) }
                            } else {
                                Modifier
                            },
                        ),
                )
            }
        }

        if (stepSubtitle != null) {
            TaminText(
                text = stepSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TaminOnAccentInkMuted,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (hint != null) {
            TaminText(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = TaminOnAccentInkSoft,
            )
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

@PreviewRtlTheme
@Composable
private fun TaminHeroStepProgressWithHintPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary700)
                .padding(Spacing.lg),
        ) {
            TaminHeroStepProgress(
                stepTitle = "اطلاعات هویتی",
                currentStep = 3,
                totalSteps = 8,
                hint = "اطلاعات هویتی را بررسی و شمارهٔ تلفن ثابت و نشانی را تکمیل کنید.",
                maxReachedStep = 5,
                onStepClick = {},
            )
        }
    }
}

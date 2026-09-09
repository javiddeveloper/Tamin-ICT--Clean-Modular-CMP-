package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
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
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkMuted
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkSoft
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.step_of_total_label

/**
 * Hero-header step chrome: current step title + "step X of Y", then equal-width segments where
 * **only the current index** is opaque (matches pension-survivor mockups; not cumulative fill).
 *
 * Intended for [TaminTopAppBar]'s `content` slot on a blue/gradient hero.
 */
@Composable
fun TaminHeroStepProgress(
    stepTitle: String,
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    stepSubtitle: String? = null,
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(IconSize.heroStepSegmentHeight)
                        .background(
                            color = if (index == clampedStep) TaminOnAccentInk else TaminOnAccentInkFaint,
                            shape = RoundedCornerShape(CornerRadius.full),
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

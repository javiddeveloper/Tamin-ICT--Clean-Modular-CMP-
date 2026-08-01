package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TextDimens
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class StepIndicatorModel(
    val title: String,
    val stepNumber: String
)

@Composable
fun StepIndicator(
    steps: ImmutableList<StepIndicatorModel>,
    currentStepIndex: Int,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            val isActive = index == currentStepIndex

            key(step.stepNumber) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    StepCircle(
                        stepNumber = step.stepNumber,
                        isActive = isActive
                    )
                    Spacer(modifier = Modifier.height(Spacing.tabSelector))

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = TextDimens.stepperTitle,
                            textAlign = TextAlign.Center
                        ),
                        color = if (isActive) taminColors.blueText else taminColors.textMuted,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (index < steps.size - 1) {
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Box(
                        modifier = Modifier
                            .padding(top = Spacing.smd)
                            .height(IconSize.stepperConnectorHeight)
                            .width(IconSize.stepperConnectorWidth)
                            .background(taminColors.outerBorder, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                }
            }
        }
    }
}

@Composable
private fun StepCircle(
    stepNumber: String,
    isActive: Boolean
) {
    val taminColors = LocalTaminColors.current

    val backgroundBrush = remember(isActive, taminColors) {
        if (isActive) {
            taminColors.iconGradientPrimary
        } else {
            SolidColor(taminColors.bgSurface)
        }
    }

    Box(
        modifier = Modifier
            .size(IconSize.stepperCircle)
            .then(
                if (isActive) {
                    Modifier.shadow(
                        elevation = Elevation.lg,
                        shape = CircleShape,
                        ambientColor = taminColors.shadowPrimary,
                        spotColor = taminColors.shadowPrimary
                    )
                } else {
                    Modifier.border(Thickness.border, taminColors.outerBorder, CircleShape)
                }
            )
            .clip(CircleShape)
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stepNumber,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = TextDimens.stepperNumber
            ),
            color = if (isActive) Color.White else taminColors.textMuted
        )
    }
}

@PreviewRtlTheme
@Composable
private fun StepIndicatorPreview() {
    PreviewRtlThemeContent {
        StepIndicator(
            steps = persistentListOf(
                StepIndicatorModel("شمارهٔ جدید", "۱"),
                StepIndicatorModel("کد تأیید", "۲"),
                StepIndicatorModel("تکمیل", "۳"),
            ),
            currentStepIndex = 0,
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun StepIndicatorPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        StepIndicator(
            steps = persistentListOf(
                StepIndicatorModel("شمارهٔ جدید", "۱"),
                StepIndicatorModel("کد تأیید", "۲"),
                StepIndicatorModel("تکمیل", "۳")
            ),
            currentStepIndex = 0,
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}

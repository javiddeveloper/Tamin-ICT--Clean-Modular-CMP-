    package com.tamin.taminhamrah.ui.components

    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.material3.Icon
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
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.Dp
    import com.tamin.taminhamrah.ui.PreviewRtlTheme
    import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
    import com.tamin.taminhamrah.ui.theme.Elevation
    import com.tamin.taminhamrah.ui.theme.IconSize
    import com.tamin.taminhamrah.ui.theme.LocalTaminColors
    import com.tamin.taminhamrah.ui.theme.Spacing
    import com.tamin.taminhamrah.ui.theme.TaminColors
    import com.tamin.taminhamrah.ui.theme.TextDimens
    import com.tamin.taminhamrah.ui.theme.Thickness
    import kotlinx.collections.immutable.ImmutableList
    import kotlinx.collections.immutable.persistentListOf
    import org.jetbrains.compose.resources.painterResource
    import taminx.core.core_ui.Res
    import taminx.core.core_ui.ic_tamin_check

    sealed interface StepState {
        data object Inactive : StepState
        data object Active : StepState
        data object Completed : StepState
    }

    @Immutable
    data class StepIndicatorModel(
        val title: String,
        val stepNumber: String,
        val state: StepState
    )

    @Immutable
    private data class StepStyle(
        val circleBackground: Brush,
        val circleBorderColor: Color?,
        val elevation: Dp,
        val numberColor: Color,
        val titleColor: Color,
        val titleWeight: FontWeight,
        val connectorColor: Color,
        val showCheckIcon: Boolean = false
    )

    private fun StepState.toStyle(colors: TaminColors): StepStyle = when (this) {

        StepState.Inactive -> StepStyle(
            circleBackground = SolidColor(colors.bgSurface),
            circleBorderColor = colors.outerBorder,
            elevation = Elevation.none,
            numberColor = colors.textMuted,
            titleColor = colors.textMuted,
            titleWeight = FontWeight.SemiBold,
            connectorColor = colors.outerBorder,
            showCheckIcon = false
        )
        StepState.Active -> StepStyle(
            circleBackground = colors.iconGradientPrimary,
            circleBorderColor = null,
            elevation = Elevation.lg,
            numberColor = Color.White,
            titleColor = colors.blueText,
            titleWeight = FontWeight.Bold,
            connectorColor = colors.outerBorder,
            showCheckIcon = false
        )
        StepState.Completed -> StepStyle(
            circleBackground = SolidColor(colors.verifiedContainerBg),
            circleBorderColor = colors.verifiedContainerBorder,
            elevation = Elevation.none,
            numberColor = colors.greenText,
            titleColor = colors.greenText,
            titleWeight = FontWeight.SemiBold,
            connectorColor = colors.greenText,
            showCheckIcon = true
        )
    }

    @Composable
    fun StepIndicator(
        steps: ImmutableList<StepIndicatorModel>,
        modifier: Modifier = Modifier
    ) {
        val colors = LocalTaminColors.current

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            steps.forEachIndexed { index, step ->
                key(step.stepNumber) {
                    val style = remember(step.state, colors) { step.state.toStyle(colors) }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        StepCircle(number = step.stepNumber, style = style)
                        Spacer(Modifier.height(Spacing.tabSelector))
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = style.titleWeight,
                                fontSize = TextDimens.stepperTitle,
                                textAlign = TextAlign.Center
                            ),
                            color = style.titleColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (index < steps.lastIndex) {
                        Spacer(Modifier.width(Spacing.xs))
                        Box(
                            Modifier
                                .padding(top = Spacing.smd)
                                .height(IconSize.stepperConnectorHeight)
                                .width(IconSize.stepperConnectorWidth)
                                .background(style.connectorColor, CircleShape)
                        )
                        Spacer(Modifier.width(Spacing.xs))
                    }
                }
            }
        }
    }

    @Composable
    private fun StepCircle(number: String, style: StepStyle) {
        Box(
            modifier = Modifier
                .size(IconSize.stepperCircle)
                .then(
                    if (style.elevation > Elevation.none) {
                        Modifier.shadow(style.elevation, CircleShape)
                    } else if (style.circleBorderColor != null) {
                        Modifier.border(Thickness.border, style.circleBorderColor, CircleShape)
                    } else Modifier
                )
                .clip(CircleShape)
                .background(style.circleBackground),
            contentAlignment = Alignment.Center
        ) {
            if (style.showCheckIcon) {
                Icon(
                    painter = painterResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = style.numberColor,
                    modifier = Modifier.size(IconSize.small)
                )
            } else {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = TextDimens.stepperNumber
                    ),
                    color = style.numberColor
                )
            }
        }
    }

    @PreviewRtlTheme
    @Composable
    private fun StepIndicatorPreview() {
        PreviewRtlThemeContent {
            StepIndicator(
                steps = persistentListOf(
                    StepIndicatorModel("شمارهٔ جدید", "۱", StepState.Completed),
                    StepIndicatorModel("کد تأیید", "۲", StepState.Active),
                    StepIndicatorModel("تکمیل", "۳", StepState.Inactive),
                ),
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
                    StepIndicatorModel("شمارهٔ جدید", "۱", StepState.Completed),
                    StepIndicatorModel("کد تأیید", "۲", StepState.Active),
                    StepIndicatorModel("تکمیل", "۳", StepState.Inactive),
                ),
                modifier = Modifier.padding(Spacing.lg)
            )
        }
    }

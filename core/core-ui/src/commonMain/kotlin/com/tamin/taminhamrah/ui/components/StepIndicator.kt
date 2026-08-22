    package com.tamin.taminhamrah.ui.components

    import androidx.compose.animation.Crossfade
    import androidx.compose.animation.animateColorAsState
    import androidx.compose.animation.core.Animatable
    import androidx.compose.animation.core.FastOutSlowInEasing
    import androidx.compose.animation.core.LinearOutSlowInEasing
    import androidx.compose.animation.core.Spring
    import androidx.compose.animation.core.animateDpAsState
    import androidx.compose.animation.core.animateFloatAsState
    import androidx.compose.animation.core.spring
    import androidx.compose.animation.core.tween
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
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
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.key
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.draw.drawBehind
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.SolidColor
    import androidx.compose.ui.graphics.drawscope.Stroke
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.Dp
    import androidx.compose.ui.unit.dp
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
    import kotlinx.coroutines.coroutineScope
    import kotlinx.coroutines.launch
    import org.jetbrains.compose.resources.painterResource
    import taminx.core.core_ui.Res
    import taminx.core.core_ui.ic_tamin_check

    private const val StepAnimationDurationMs = 260
    private const val StepPopStartScale = 0.85f
    private const val RipplePulseDurationMs = 600
    private const val RippleMaxScale = 1.6f
    private const val RippleMaxAlpha = 0.5f
    private val RippleStrokeWidth = 2.dp
    private const val FlipDurationMs = 420
    private const val FlipCameraDistance = 14f
    private const val ConnectorFillDurationMs = 420

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
        val rippleColor: Color,
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
            rippleColor = Color.Transparent,
            showCheckIcon = false
        )
        StepState.Active -> StepStyle(
            circleBackground = colors.iconGradientPrimary,
            circleBorderColor = null,
            elevation = Elevation.lg,
            numberColor = Color.White,
            titleColor = colors.blueText,
            titleWeight = FontWeight.Bold,
            rippleColor = colors.blueText,
            showCheckIcon = false
        )
        StepState.Completed -> StepStyle(
            circleBackground = SolidColor(colors.verifiedContainerBg),
            circleBorderColor = colors.verifiedContainerBorder,
            elevation = Elevation.none,
            numberColor = colors.greenText,
            titleColor = colors.greenText,
            titleWeight = FontWeight.SemiBold,
            rippleColor = colors.greenText,
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

                    val titleColor by animateColorAsState(
                        targetValue = style.titleColor,
                        animationSpec = tween(StepAnimationDurationMs)
                    )

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
                            color = titleColor,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (index < steps.lastIndex) {
                        Spacer(Modifier.width(Spacing.xs))
                        StepConnector(
                            filled = step.state == StepState.Completed,
                            trackColor = colors.outerBorder,
                            fillColor = colors.greenText
                        )
                        Spacer(Modifier.width(Spacing.xs))
                    }
                }
            }
        }
    }

    @Composable
    private fun StepConnector(filled: Boolean, trackColor: Color, fillColor: Color) {
        val fillFraction by animateFloatAsState(
            targetValue = if (filled) 1f else 0f,
            animationSpec = tween(ConnectorFillDurationMs, easing = FastOutSlowInEasing)
        )
        Box(
            modifier = Modifier
                .padding(top = Spacing.smd)
                .height(IconSize.stepperConnectorHeight)
                .width(IconSize.stepperConnectorWidth)
                .clip(CircleShape)
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fillFraction)
                    .background(fillColor)
            )
        }
    }

    @Composable
    private fun StepCircle(number: String, style: StepStyle) {
        val isActive = style.elevation > Elevation.none
        val advanced = isActive || style.showCheckIcon

        val scale = remember { Animatable(1f) }
        val ripple = remember { Animatable(0f) }
        LaunchedEffect(advanced) {
            if (advanced) {
                scale.snapTo(StepPopStartScale)
                ripple.snapTo(0f)
                coroutineScope {
                    launch {
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                    launch {
                        ripple.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(RipplePulseDurationMs, easing = LinearOutSlowInEasing)
                        )
                    }
                }
            } else {
                scale.snapTo(1f)
                ripple.snapTo(0f)
            }
        }

        val elevation by animateDpAsState(
            targetValue = style.elevation,
            animationSpec = tween(StepAnimationDurationMs)
        )
        val borderColor by animateColorAsState(
            targetValue = style.circleBorderColor ?: Color.Transparent,
            animationSpec = tween(StepAnimationDurationMs)
        )
        val numberColor by animateColorAsState(
            targetValue = style.numberColor,
            animationSpec = tween(StepAnimationDurationMs)
        )
        val flipRotation by animateFloatAsState(
            targetValue = if (style.showCheckIcon) 180f else 0f,
            animationSpec = tween(FlipDurationMs, easing = FastOutSlowInEasing)
        )

        Box(
            modifier = Modifier
                .size(IconSize.stepperCircle * RippleMaxScale)
                .drawBehind {
                    if (ripple.value > 0f && ripple.value < 1f) {
                        val baseRadius = IconSize.stepperCircle.toPx() / 2f
                        val radius = baseRadius * (1f + (RippleMaxScale - 1f) * ripple.value)
                        drawCircle(
                            color = style.rippleColor.copy(alpha = (1f - ripple.value) * RippleMaxAlpha),
                            radius = radius,
                            style = Stroke(width = RippleStrokeWidth.toPx())
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.stepperCircle)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .shadow(elevation, CircleShape, clip = false)
                    .border(Thickness.border, borderColor, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = style.circleBackground,
                    animationSpec = tween(StepAnimationDurationMs),
                    modifier = Modifier.fillMaxSize()
                ) { brush ->
                    Box(Modifier.fillMaxSize().background(brush))
                }

                if (flipRotation <= 90f) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = TextDimens.stepperNumber
                        ),
                        color = numberColor,
                        modifier = Modifier.graphicsLayer {
                            rotationY = flipRotation
                            cameraDistance = FlipCameraDistance * density
                        }
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_tamin_check),
                        contentDescription = null,
                        tint = numberColor,
                        modifier = Modifier
                            .size(IconSize.small)
                            .graphicsLayer {
                                rotationY = flipRotation - 180f
                                cameraDistance = FlipCameraDistance * density
                            }
                    )
                }
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

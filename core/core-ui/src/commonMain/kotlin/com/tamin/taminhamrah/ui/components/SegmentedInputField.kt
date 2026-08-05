package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import com.tamin.taminhamrah.util.toPersianDigits
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_info
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import taminx.core.core_ui.ic_close

@Composable
fun SegmentedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    slotCount: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
    errorMessage: String? = null,
    showClearButton: Boolean = true,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Number,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.End,
    placeholders: ImmutableList<Char> = List(slotCount) { 'ـ' }.toImmutableList(),
    groupBreaks: ImmutableSet<Int> = persistentSetOf(),
    groupSpacing: Dp = Spacing.sm,
    slotSpacing: Dp = Spacing.xxs,
    valueFilter: (String) -> String = { raw ->
        raw.filter { it.isDigit() || it.isPersianDigit() }.take(slotCount)
    },
) {
    val colors = LocalTaminColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val blinkState = rememberCursorBlink()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgSurface, RoundedCornerShape(CornerRadius.lg))
                .animatedErrorBorder(
                isError = error,
                errorColor = colors.dangerText,
                normalColor = if (isFocused) colors.blueText else colors.textMuted,
                borderWidth = Thickness.border,
                cornerRadius = CornerRadius.lg
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(IconSize.textFieldIconContainer)
                    .background(
                        color = colors.blueBg,
                        shape = RoundedCornerShape(CornerRadius.textFieldIcon)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.banner),
                )
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            BasicTextField(
                value = value,
                onValueChange = { newValue -> onValueChange(valueFilter(newValue)) },
                enabled = enabled,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.alpha(0f)) {
                            innerTextField()
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = horizontalArrangement,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (i in 0 until slotCount) {
                                    val digit = value.getOrNull(i)
                                    val isCursor = enabled && isFocused && i == value.length

                                    DigitSlot(
                                        digit = digit,
                                        placeholder = placeholders.getOrElse(i) { 'ـ' },
                                        isCursor = isCursor,
                                        textColor = colors.textPrimary,
                                        placeholderColor = colors.textMuted,
                                        blinkState = blinkState
                                    )
                                    if (i != slotCount - 1) {
                                        val spacing = if (i in groupBreaks) groupSpacing else slotSpacing
                                        Spacer(modifier = Modifier.width(spacing))
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        if (showClearButton && enabled && value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(IconSize.textFieldIconContainer)
                    .clip(CircleShape)
                    .clickable { onValueChange("") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_close),
                    contentDescription = "Clear",
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.banner)
                )
            }
        }
    }

    if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_info),
                    contentDescription = null,
                    tint = colors.dangerText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    color = colors.dangerText,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
    errorMessage: String? = null,
    showClearButton: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    SegmentedInputField(
        value = value,
        onValueChange = onValueChange,
        slotCount = 11,
        modifier = modifier,
        enabled = enabled,
        error = error,
        errorMessage = errorMessage,
        showClearButton = showClearButton,
        leadingIcon = leadingIcon,
        placeholders = persistentListOf('۰', '۹', 'ـ', 'ـ', 'ـ', 'ـ', 'ـ', 'ـ', 'ـ', 'ـ', 'ـ'),
        groupBreaks = persistentSetOf(3, 6)
    )
}

@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 5,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
    errorMessage: String? = null,
    showClearButton: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    SegmentedInputField(
        value = value,
        onValueChange = onValueChange,
        slotCount = length,
        modifier = modifier,
        enabled = enabled,
        error = error,
        errorMessage = errorMessage,
        showClearButton = showClearButton,
        leadingIcon = leadingIcon,
        horizontalArrangement = Arrangement.Center,
        slotSpacing = Spacing.sm
    )
}

@Composable
private fun rememberCursorBlink(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "CursorBlink")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BlinkingCursor"
    )
}

@Composable
private fun DigitSlot(
    digit: Char?,
    placeholder: Char,
    isCursor: Boolean,
    textColor: Color,
    placeholderColor: Color,
    blinkState: State<Float>
) {
    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            if (targetState != null) {
                (fadeIn(tween(300)) + scaleIn(initialScale = 0.5f, animationSpec = tween(300)) + slideInVertically(tween(300)) { it / 2 })
                    .togetherWith(fadeOut(tween(150)) + scaleOut(targetScale = 0.8f, animationSpec = tween(150)))
            } else {
                (fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)))
                    .togetherWith(fadeOut(tween(300)) + scaleOut(targetScale = 0.5f, animationSpec = tween(300)) + slideOutVertically(tween(300)) { it / 2 })
            }
        },
        label = "DigitAnimation"
    ) { currentDigit ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.defaultMinSize(minWidth = 14.dp)
        ) {
            if (currentDigit != null) {
                Text(
                    text = currentDigit.toString().toPersianDigits(),
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
            } else {
                Text(
                    text = placeholder.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = placeholderColor,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isCursor) blinkState.value else 1f
                    }
                )
            }
        }
    }
}

private fun Char.isPersianDigit(): Boolean {
    return this in '۰'..'۹'
}

/**
 * The field-error treatment used across the app: the border animates to the danger colour and back
 * rather than snapping. Public so rows that are not text fields -- pickers, date rows -- report a
 * problem the same way the inputs beside them do.
 */
fun Modifier.animatedErrorBorder(
    isError: Boolean,
    errorColor: Color,
    normalColor: Color,
    borderWidth: Dp,
    cornerRadius: Dp
): Modifier = composed {
    val borderProgress = remember { Animatable(if (isError) 1f else 0f) }
    val errorColorAlpha = remember { Animatable(if (isError) 1f else 0f) }

    val animatedNormalColor by animateColorAsState(
        targetValue = normalColor,
        animationSpec = tween(durationMillis = 300),
        label = "NormalBorderColor"
    )

    LaunchedEffect(isError) {
        if (isError) {
            errorColorAlpha.snapTo(1f)
            borderProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            errorColorAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
            borderProgress.snapTo(0f)
        }
    }

    this.drawWithCache {
        val strokeWidthPx = borderWidth.toPx()
        val halfStroke = strokeWidthPx / 2f

        val rect = Rect(
            left = halfStroke,
            top = halfStroke,
            right = size.width - halfStroke,
            bottom = size.height - halfStroke
        )
        val cornerRadiusPx = cornerRadius.toPx()

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = rect,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
        }
        val pathMeasure = PathMeasure().apply {
            setPath(path, forceClosed = false)
        }
        val totalLength = pathMeasure.length
        val errorPath = Path()

        onDrawWithContent {
            drawContent()

            drawPath(
                path = path,
                color = animatedNormalColor,
                style = Stroke(width = strokeWidthPx)
            )

            if (errorColorAlpha.value > 0f && borderProgress.value > 0f) {
                errorPath.reset()
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = totalLength * borderProgress.value,
                    destination = errorPath,
                    startWithMoveTo = true
                )

                drawPath(
                    path = errorPath,
                    color = errorColor.copy(alpha = errorColorAlpha.value),
                    style = Stroke(width = strokeWidthPx * 1.5f)
                )
            }
        }
    }
}

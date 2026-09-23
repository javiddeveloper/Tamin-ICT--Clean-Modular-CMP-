package com.tamin.taminhamrah.ui.components.toast

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Timings and sizes of the morphing toast capsule.
 *
 * The choreography is: the icon disc pops in ([ICON_ENTER_MS], overshooting), widens into the
 * full capsule while the message fades in ([EXPAND_MS]), stays for the toast's duration, narrows
 * back to the disc while the message fades out ([COLLAPSE_MS]), and finally shrinks away
 * ([ICON_EXIT_MS]).
 *
 * Neighbouring phases overlap ([EXPAND_OVERLAP_MS], [EXIT_OVERLAP_MS]) so the motion flows from
 * one phase into the next instead of pausing between them.
 */
internal object MorphingToastDefaults {
    const val ICON_ENTER_MS = 420
    const val EXPAND_MS = 560
    const val COLLAPSE_MS = 480
    const val ICON_EXIT_MS = 320

    /** How long before the disc settles the capsule already starts to widen. */
    const val EXPAND_OVERLAP_MS = 120

    /** How long before the capsule is fully narrowed the disc already starts to leave. */
    const val EXIT_OVERLAP_MS = 100

    /** A gentle back-out: the disc swells just past its size and eases back, no hard bounce. */
    val EnterEasing = CubicBezierEasing(0.34f, 1.25f, 0.64f, 1f)

    /** Quint-out: the capsule opens quickly, then glides into its full width. */
    val ExpandEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

    /** Cubic in-out: the capsule eases into the collapse and eases out of it. */
    val CollapseEasing = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)

    /** Cubic-in: the disc drifts off softly rather than snapping away. */
    val ExitEasing = CubicBezierEasing(0.32f, 0f, 0.67f, 0f)

    /**
     * How far into the expansion the message starts to show. Below it the capsule is still too
     * narrow for the text to be anything but a clipped sliver, so it stays hidden until there is
     * room — and, mirrored, it is gone before the collapse gets that narrow again.
     */
    const val MESSAGE_REVEAL_START = 0.3f

    /** The collapsed disc — also the capsule's minimum height. */
    val DiscSize: Dp = Spacing.xxxxl
    val IconSize: Dp = Spacing.xl

    /** Half of [DiscSize], so the collapsed capsule is an exact circle. */
    val Shape = RoundedCornerShape(CornerRadius.x2l)
    val Elevation: Dp = com.tamin.taminhamrah.ui.theme.Elevation.md

    /** A step under the title scale, kept at medium weight so the short message stays legible. */
    val MessageTextStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
}

/** The colors one toast type is drawn in. */
@Immutable
data class TaminToastColors(
    val container: Color,
    val content: Color,
    val border: Color,
)

/**
 * The colors of a toast of [type], from the app's semantic tokens: each status wears the same
 * background/ink/hairline trio its status pills already use, so a toast and a pill for the same
 * outcome read as one color.
 */
@Composable
fun taminToastColors(type: ToastType): TaminToastColors {
    val colors = LocalTaminColors.current
    return when (type) {
        ToastType.Success -> TaminToastColors(colors.greenBg, colors.greenText, colors.greenBorder)
        ToastType.Error -> TaminToastColors(colors.dangerBg, colors.dangerText, colors.dangerBorder)
        ToastType.Warning -> TaminToastColors(colors.orangeBg, colors.orangeText, colors.historyWarningBorder)
        ToastType.Info -> TaminToastColors(colors.blueBg, colors.blueText, colors.blueBorder)
        ToastType.Normal -> TaminToastColors(colors.bgSurface, colors.textPrimary, colors.border)
    }
}

/** The status icon for [type], sized for the capsule's disc. Nothing for [ToastType.Normal]. */
@Composable
fun TaminToastIcon(type: ToastType, tint: Color = LocalToastContentColor.current) {
    val icon = when (type) {
        ToastType.Success -> Icons.Default.CheckCircle
        ToastType.Error -> Icons.Default.Error
        ToastType.Info -> Icons.Default.Info
        ToastType.Warning -> Icons.Default.Warning
        ToastType.Normal -> return
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(MorphingToastDefaults.IconSize),
    )
}

/**
 * A toast that morphs from an icon disc into a capsule and back, like an expanding island.
 *
 * It runs its own choreography: entering while [dismissing] is false, and on [dismissing]
 * collapsing and leaving, after which [onInvisible] is called so the toaster can drop it.
 * A dismissal that arrives mid-entrance collapses from wherever the capsule is.
 *
 * All animated values are read only in the layout and draw phases, so the animation itself never
 * recomposes the capsule.
 */
@Composable
internal fun MorphingToast(
    toast: Toast,
    dismissing: Boolean,
    onInvisible: () -> Unit,
    colors: TaminToastColors,
    maxWidth: Dp,
    iconSlot: @Composable (toast: Toast) -> Unit,
    messageSlot: @Composable (toast: Toast) -> Unit,
    actionSlot: @Composable (toast: Toast) -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconScale = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(0f) }
    val expansion = remember { Animatable(0f) }
    val currentOnInvisible by rememberUpdatedState(onInvisible)

    LaunchedEffect(dismissing) {
        with(MorphingToastDefaults) {
            if (!dismissing) {
                coroutineScope {
                    // Phase 1 — the disc swells in.
                    launch { iconAlpha.animateTo(1f, tween(ICON_ENTER_MS, easing = Easing.decelerate)) }
                    launch { iconScale.animateTo(1f, tween(ICON_ENTER_MS, easing = EnterEasing)) }
                    // Phase 2 — it widens into the capsule, starting as the disc settles.
                    // Phase 3, the stay, is the toaster's timer.
                    delay((ICON_ENTER_MS - EXPAND_OVERLAP_MS).toLong())
                    expansion.animateTo(1f, tween(EXPAND_MS, easing = ExpandEasing))
                }
            } else {
                coroutineScope {
                    // Phase 4 — back to the disc. Skipped when dismissed before it ever widened.
                    if (expansion.value > 0f) {
                        launch { expansion.animateTo(0f, tween(COLLAPSE_MS, easing = CollapseEasing)) }
                        // Phase 5 starts just before the capsule has fully closed.
                        delay((COLLAPSE_MS - EXIT_OVERLAP_MS).toLong())
                    }
                    // Phase 5 — the disc leaves.
                    launch { iconAlpha.animateTo(0f, tween(ICON_EXIT_MS, easing = ExitEasing)) }
                    launch { iconScale.animateTo(0f, tween(ICON_EXIT_MS, easing = ExitEasing)) }
                }
                currentOnInvisible()
            }
        }
    }

    MorphingToastContent(
        toast = toast,
        colors = colors,
        maxWidth = maxWidth,
        expansion = { expansion.value },
        iconScale = { iconScale.value },
        iconAlpha = { iconAlpha.value },
        iconSlot = iconSlot,
        messageSlot = messageSlot,
        actionSlot = actionSlot,
        modifier = modifier,
    )
}

@Composable
private fun MorphingToastContent(
    toast: Toast,
    colors: TaminToastColors,
    maxWidth: Dp,
    expansion: () -> Float,
    iconScale: () -> Float,
    iconAlpha: () -> Float,
    iconSlot: @Composable (toast: Toast) -> Unit,
    messageSlot: @Composable (toast: Toast) -> Unit,
    actionSlot: @Composable (toast: Toast) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MorphingToastDefaults.Shape
    CompositionLocalProvider(LocalToastContentColor provides colors.content) {
        Box(
            modifier = modifier
                .graphicsLayer {
                    val scale = iconScale()
                    scaleX = scale
                    scaleY = scale
                    alpha = iconAlpha()
                }
                .widthIn(max = maxWidth)
                .shadow(elevation = MorphingToastDefaults.Elevation, shape = shape)
                .border(width = Dp.Hairline, color = colors.border, shape = shape)
                .background(color = colors.container, shape = shape)
                .clip(shape)
                // Draw modifiers above sit outside this one, so they follow the morphing size
                // rather than the full size of the content measured inside it.
                .morphFromDisc(MorphingToastDefaults.DiscSize, expansion)
                .semantics { liveRegion = LiveRegionMode.Polite },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(MorphingToastDefaults.DiscSize),
                    contentAlignment = Alignment.Center,
                ) {
                    iconSlot(toast)
                }
                Row(
                    modifier = Modifier
                        .graphicsLayer {
                            val start = MorphingToastDefaults.MESSAGE_REVEAL_START
                            alpha = ((expansion() - start) / (1f - start)).coerceIn(0f, 1f)
                        }
                        .padding(end = Spacing.lg, top = Spacing.md, bottom = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Box(modifier = Modifier.weight(1f, fill = false)) { messageSlot(toast) }
                    actionSlot(toast)
                }
            }
        }
    }
}

/**
 * Sizes the node between a [disc]-sized circle (at 0) and its content's own size (at 1).
 *
 * The content is always measured at its full size and pinned to the start edge, so as the node
 * narrows the clip eats the message from the end and leaves the start-side icon in place — on the
 * right in this RTL app. The toaster centers the node, so it opens and closes symmetrically.
 */
private fun Modifier.morphFromDisc(disc: Dp, progress: () -> Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val discPx = disc.roundToPx()
        val fraction = progress()
        val width = lerp(minOf(discPx, placeable.width).toFloat(), placeable.width.toFloat(), fraction)
        val height = lerp(minOf(discPx, placeable.height).toFloat(), placeable.height.toFloat(), fraction)
        val w = width.roundToInt()
        val h = height.roundToInt()
        layout(w, h) {
            placeable.placeRelative(x = 0, y = (h - placeable.height) / 2)
        }
    }

// region Previews

private val PreviewToasts = listOf(
    Toast(message = "اطلاعات با موفقیت ذخیره شد", type = ToastType.Success),
    Toast(message = "ارتباط با سرور برقرار نشد", type = ToastType.Error),
    Toast(message = "مهلت پرداخت رو به پایان است", type = ToastType.Warning),
    Toast(message = "در حال همگام‌سازی اطلاعات", type = ToastType.Info),
)

@Composable
private fun MorphingToastPhasesPreview(darkTheme: Boolean) {
    PreviewRtlThemeContent(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .background(LocalTaminColors.current.bgPage)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Each type at three points of the morph: the disc, mid-expansion, and the capsule.
            for (toast in PreviewToasts) {
                for (fraction in listOf(0f, 0.5f, 1f)) {
                    MorphingToastContent(
                        toast = toast,
                        colors = taminToastColors(toast.type),
                        maxWidth = ToastWidthPolicy().max,
                        expansion = { fraction },
                        iconScale = { 1f },
                        iconAlpha = { 1f },
                        iconSlot = { TaminToastIcon(it.type) },
                        messageSlot = { PreviewMessage(it) },
                        actionSlot = {},
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewMessage(toast: Toast) {
    Text(
        text = toast.message.toString(),
        style = MorphingToastDefaults.MessageTextStyle,
        color = LocalToastContentColor.current,
    )
}

@PreviewRtlTheme
@Composable
private fun MorphingToastLightPreview() = MorphingToastPhasesPreview(darkTheme = false)

@PreviewRtlTheme
@Composable
private fun MorphingToastDarkPreview() = MorphingToastPhasesPreview(darkTheme = true)

/** Runs the real choreography — use the preview's interactive mode to watch it. */
@PreviewRtlTheme
@Composable
private fun MorphingToastAnimatedPreview() {
    PreviewRtlThemeContent {
        val toast = PreviewToasts.first()
        MorphingToast(
            toast = toast,
            dismissing = false,
            onInvisible = {},
            colors = taminToastColors(toast.type),
            maxWidth = ToastWidthPolicy().max,
            iconSlot = { TaminToastIcon(it.type) },
            messageSlot = { PreviewMessage(it) },
            actionSlot = {},
        )
    }
}

// endregion
